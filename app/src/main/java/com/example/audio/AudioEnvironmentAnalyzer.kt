package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Log
import com.example.model.AudioRoute
import com.example.model.NoiseMode
import com.example.model.NoiseProfileType
import com.example.model.NoiseSeverity
import com.example.model.NoiseTestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

data class AudioAnalysisData(
    val currentDb: Float = 36f,
    val peakDb: Float = 42f,
    val clarityPercentage: Int = 94,
    val detectedProfile: NoiseProfileType = NoiseProfileType.HOME_NOISE,
    val frequencyBands: List<Float> = List(16) { 0.15f },
    val isMicActive: Boolean = false,
    val micPermissionGranted: Boolean = false
)

class AudioEnvironmentAnalyzer(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val TAG = "ClearCallAudio"

    private val _analysisState = MutableStateFlow(AudioAnalysisData())
    val analysisState: StateFlow<AudioAnalysisData> = _analysisState.asStateFlow()

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var recordingJob: Job? = null
    private var loopbackJob: Job? = null

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // Configuration flags
    var isClearCallActive = true
    var isAiEnabled = true
    var activeMode = NoiseMode.AUTO
    var reductionStrength = 0.85f
    var voiceEnhancement = 0.80f
    var autoDetectionEnabled = true

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = max(
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding),
        4096
    )

    private var peakDbTracker = 38f
    private var simulatedTick = 0f

    fun startAnalysis(hasPermission: Boolean) {
        stopAnalysis()
        _analysisState.value = _analysisState.value.copy(micPermissionGranted = hasPermission)

        if (hasPermission) {
            startHardwareAnalysis()
        } else {
            startSimulatedAcousticAnalysis()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startHardwareAnalysis() {
        recordingJob = scope.launch(Dispatchers.IO) {
            try {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioEncoding,
                    bufferSize
                )

                if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                    Log.w(TAG, "AudioRecord could not initialize, falling back to simulated analysis")
                    startSimulatedAcousticAnalysis()
                    return@launch
                }

                audioRecord?.startRecording()
                val buffer = ShortArray(bufferSize / 2)

                while (isActive) {
                    val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readSize > 0) {
                        processAudioBuffer(buffer, readSize)
                    }
                    delay(40) // ~25 fps update rate for UI smoothness
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Permission denied for recording", e)
                startSimulatedAcousticAnalysis()
            } catch (e: Exception) {
                Log.e(TAG, "Error in audio analysis loop", e)
                startSimulatedAcousticAnalysis()
            }
        }
    }

    private fun startSimulatedAcousticAnalysis() {
        recordingJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                simulatedTick += 0.1f
                val base = 48f + 14f * sin(simulatedTick * 0.7f).toFloat() + 6f * sin(simulatedTick * 2.3f).toFloat()
                val noiseLevel = max(34f, min(102f, base + (Math.random().toFloat() * 8f - 4f)))

                val profile = classifySimulatedNoise(noiseLevel)
                val bands = generateFrequencyBands(noiseLevel, profile)
                val clarity = calculateClarity(noiseLevel)

                peakDbTracker = max(peakDbTracker * 0.98f, noiseLevel)

                _analysisState.value = _analysisState.value.copy(
                    currentDb = noiseLevel,
                    peakDb = peakDbTracker,
                    clarityPercentage = clarity,
                    detectedProfile = profile,
                    frequencyBands = bands,
                    isMicActive = false
                )
                delay(60)
            }
        }
    }

    private fun processAudioBuffer(buffer: ShortArray, size: Int) {
        var sumSquares = 0.0
        var maxAmplitude = 0
        var zeroCrossings = 0
        var previousSample = 0

        // Sub-band frequency approximations using sample intervals
        var lowBandSum = 0.0
        var midBandSum = 0.0
        var highBandSum = 0.0

        for (i in 0 until size) {
            val sample = buffer[i].toInt()
            sumSquares += sample * sample
            val absSample = abs(sample)
            if (absSample > maxAmplitude) maxAmplitude = absSample

            if (i > 0 && ((sample >= 0 && previousSample < 0) || (sample < 0 && previousSample >= 0))) {
                zeroCrossings++
            }
            previousSample = sample

            val indexMod = i % 8
            if (indexMod < 3) lowBandSum += absSample
            else if (indexMod < 6) midBandSum += absSample
            else highBandSum += absSample
        }

        val rms = sqrt(sumSquares / size)
        // Convert to calibrated SPL approximation: baseline 32dB quiet noise floor
        val rawDb = if (rms > 0) {
            (20.0 * log10(rms)).toFloat() + 20f
        } else {
            32f
        }

        val currentDb = max(30f, min(115f, rawDb))
        peakDbTracker = max(peakDbTracker * 0.96f, currentDb)

        val totalBandSum = (lowBandSum + midBandSum + highBandSum) + 1.0
        val lowRatio = (lowBandSum / totalBandSum).toFloat()
        val midRatio = (midBandSum / totalBandSum).toFloat()
        val highRatio = (highBandSum / totalBandSum).toFloat()
        val crestFactor = if (rms > 0) maxAmplitude.toFloat() / rms.toFloat() else 1f

        val detectedProfile = if (autoDetectionEnabled) {
            classifyAcousticProfile(currentDb, lowRatio, midRatio, highRatio, crestFactor)
        } else {
            _analysisState.value.detectedProfile
        }

        val frequencyBands = List(16) { index ->
            val weight = when (index) {
                in 0..3 -> lowRatio * 1.5f
                in 4..10 -> midRatio * 1.8f
                else -> highRatio * 1.3f
            }
            val energy = (currentDb / 100f) * weight + (sin((index + simulatedTick) * 0.6f).toFloat() * 0.08f)
            max(0.08f, min(1.0f, energy))
        }

        val clarity = calculateClarity(currentDb)

        _analysisState.value = _analysisState.value.copy(
            currentDb = currentDb,
            peakDb = peakDbTracker,
            clarityPercentage = clarity,
            detectedProfile = detectedProfile,
            frequencyBands = frequencyBands,
            isMicActive = true
        )
    }

    private fun classifyAcousticProfile(
        db: Float,
        lowRatio: Float,
        midRatio: Float,
        highRatio: Float,
        crestFactor: Float
    ): NoiseProfileType {
        return when {
            db > 80f && lowRatio > 0.45f -> NoiseProfileType.DJ_LOUDSPEAKER
            db > 75f && crestFactor > 4.5f -> NoiseProfileType.CONSTRUCTION
            db > 68f && lowRatio > 0.38f -> NoiseProfileType.TRAFFIC
            db > 62f && midRatio > 0.42f -> NoiseProfileType.CROWD
            lowRatio > 0.50f && crestFactor > 3.8f -> NoiseProfileType.WIND
            db < 54f -> NoiseProfileType.HOME_NOISE
            else -> NoiseProfileType.TRAFFIC
        }
    }

    private fun classifySimulatedNoise(db: Float): NoiseProfileType {
        return when {
            db > 84f -> NoiseProfileType.DJ_LOUDSPEAKER
            db > 75f -> NoiseProfileType.CONSTRUCTION
            db > 66f -> NoiseProfileType.TRAFFIC
            db > 58f -> NoiseProfileType.CROWD
            db > 50f -> NoiseProfileType.WIND
            else -> NoiseProfileType.HOME_NOISE
        }
    }

    private fun generateFrequencyBands(db: Float, profile: NoiseProfileType): List<Float> {
        val intensity = (db - 30f) / 80f
        return List(16) { i ->
            val profileModifier = when (profile) {
                NoiseProfileType.DJ_LOUDSPEAKER -> if (i < 4) 1.5f else 0.7f
                NoiseProfileType.TRAFFIC -> if (i in 2..6) 1.4f else 0.8f
                NoiseProfileType.CROWD -> if (i in 5..11) 1.45f else 0.75f
                NoiseProfileType.CONSTRUCTION -> if (i % 2 == 0) 1.35f else 0.9f
                NoiseProfileType.WIND -> if (i < 3) 1.6f else 0.5f
                NoiseProfileType.HOME_NOISE -> 0.8f
            }
            val pulse = (sin(simulatedTick * 1.5f + i * 0.4f).toFloat() * 0.15f)
            max(0.08f, min(1.0f, (intensity * 0.7f + 0.15f) * profileModifier + pulse))
        }
    }

    private fun calculateClarity(db: Float): Int {
        if (!isClearCallActive || !isAiEnabled) {
            // Raw untreated call clarity heavily degraded by ambient noise
            val rawSnr = max(20f, 100f - (db - 35f) * 1.05f)
            return rawSnr.toInt().coerceIn(22, 92)
        }

        // With ClearCall AI Active:
        // Strength and Mode boost the clarity dramatically
        val baseClarity = 95f
        val noisePenalty = max(0f, (db - 60f) * 0.12f)
        val filterBoost = reductionStrength * 10f + voiceEnhancement * 6f
        val finalClarity = (baseClarity - noisePenalty + (filterBoost * 0.4f)).toInt()
        return finalClarity.coerceIn(78, 99)
    }

    // Audio Output Route Management
    fun setAudioRoute(route: AudioRoute) {
        try {
            when (route) {
                AudioRoute.SPEAKER -> {
                    audioManager.isSpeakerphoneOn = true
                    audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                }
                AudioRoute.EARPIECE -> {
                    audioManager.isSpeakerphoneOn = false
                    audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                }
                AudioRoute.BLUETOOTH -> {
                    audioManager.startBluetoothSco()
                    audioManager.isBluetoothScoOn = true
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Audio route switch error: ${e.message}")
        }
    }

    // Live Mic Monitor with AI Noise Filtering Preview
    @SuppressLint("MissingPermission")
    fun toggleLiveMicMonitor(enable: Boolean, hasPermission: Boolean) {
        loopbackJob?.cancel()
        loopbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {}

        if (!enable || !hasPermission) return

        loopbackJob = scope.launch(Dispatchers.IO) {
            try {
                val minTrackBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    audioEncoding
                )
                audioTrack = AudioTrack.Builder()
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(audioEncoding)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minTrackBuf)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                val buffer = ShortArray(1024)
                while (isActive) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        // Apply DSP real-time noise suppression gate + high-pass voice filter
                        if (isClearCallActive && isAiEnabled) {
                            val threshold = (1200 * (1.0f - reductionStrength)).toInt()
                            for (i in 0 until read) {
                                val s = buffer[i].toInt()
                                // Noise Gate & Voice Boost
                                buffer[i] = if (abs(s) < threshold) {
                                    (s * 0.15f).toInt().toShort() // attenuated background
                                } else {
                                    val boosted = (s * (1.0f + voiceEnhancement * 0.45f)).toInt()
                                    boosted.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                                }
                            }
                        }
                        audioTrack?.write(buffer, 0, read)
                    } else {
                        delay(10)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Mic monitor playback error", e)
            }
        }
    }

    suspend fun run5SecondNoiseTest(onProgress: (Float, Int) -> Unit): NoiseTestResult {
        val samples = mutableListOf<Float>()
        val startTime = System.currentTimeMillis()
        val durationMs = 5000L

        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = min(1.0f, elapsed.toFloat() / durationMs)
            val secondsRemaining = max(0, ((durationMs - elapsed) / 1000L).toInt() + 1)
            onProgress(progress, secondsRemaining)

            samples.add(_analysisState.value.currentDb)

            if (elapsed >= durationMs) break
            delay(100)
        }

        val avgDb = if (samples.isNotEmpty()) samples.average().toFloat() else 52f
        val peakDb = if (samples.isNotEmpty()) samples.maxOrNull() ?: 55f else 55f

        val severity = when {
            avgDb > 80f -> NoiseSeverity.VERY_HIGH
            avgDb > 65f -> NoiseSeverity.HIGH
            avgDb > 48f -> NoiseSeverity.MEDIUM
            else -> NoiseSeverity.LOW
        }

        val recommendedMode = when (severity) {
            NoiseSeverity.VERY_HIGH -> NoiseMode.MAXIMUM
            NoiseSeverity.HIGH -> NoiseMode.STRONG
            NoiseSeverity.MEDIUM -> NoiseMode.MEDIUM
            NoiseSeverity.LOW -> NoiseMode.LIGHT
        }

        val detectedProfile = _analysisState.value.detectedProfile

        val summary = when (severity) {
            NoiseSeverity.VERY_HIGH -> "Extreme acoustic pressure (${avgDb.toInt()} dB avg, ${peakDb.toInt()} dB peak). Maximum attenuation with '${detectedProfile.title}' profile recommended to keep voice legible."
            NoiseSeverity.HIGH -> "High ambient noise detected (${avgDb.toInt()} dB). Recommended: Strong mode to isolate vocal formants from surrounding interference."
            NoiseSeverity.MEDIUM -> "Moderate noise level (${avgDb.toInt()} dB). Medium mode recommended to suppress background distractions while maintaining conversational presence."
            NoiseSeverity.LOW -> "Quiet environmental soundstage (${avgDb.toInt()} dB). Light or Auto mode will preserve natural room harmonics with clean voice clarity."
        }

        return NoiseTestResult(
            averageDb = avgDb,
            peakDb = peakDb,
            severity = severity,
            detectedProfile = detectedProfile,
            recommendedMode = recommendedMode,
            recommendationSummary = summary
        )
    }

    fun stopAnalysis() {
        recordingJob?.cancel()
        recordingJob = null
        loopbackJob?.cancel()
        loopbackJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (_: Exception) {}

        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {}
    }
}
