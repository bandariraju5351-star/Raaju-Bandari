package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioAnalysisData
import com.example.audio.AudioEnvironmentAnalyzer
import com.example.model.AppSettings
import com.example.model.AudioRoute
import com.example.model.NoiseMode
import com.example.model.NoiseProfileType
import com.example.model.NoiseTestResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface NoiseTestUiState {
    object Idle : NoiseTestUiState
    data class Running(val progress: Float, val secondsRemaining: Int) : NoiseTestUiState
    data class Completed(val result: NoiseTestResult) : NoiseTestUiState
}

enum class ClearCallScreen(val title: String) {
    HOME("Home"),
    CLEAR_CALL("Clear Call"),
    AI_NOISE_CONTROL("Noise Control"),
    NOISE_TEST("Noise Test"),
    SETTINGS("Settings")
}

class ClearCallViewModel(application: Application) : AndroidViewModel(application) {

    val audioAnalyzer = AudioEnvironmentAnalyzer(application.applicationContext, viewModelScope)

    val analysisData: StateFlow<AudioAnalysisData> = audioAnalyzer.analysisState

    private val _currentScreen = MutableStateFlow(ClearCallScreen.HOME)
    val currentScreen: StateFlow<ClearCallScreen> = _currentScreen.asStateFlow()

    private val _isClearCallActive = MutableStateFlow(true)
    val isClearCallActive: StateFlow<Boolean> = _isClearCallActive.asStateFlow()

    private val _isAiEnabled = MutableStateFlow(true)
    val isAiEnabled: StateFlow<Boolean> = _isAiEnabled.asStateFlow()

    private val _selectedNoiseMode = MutableStateFlow(NoiseMode.AUTO)
    val selectedNoiseMode: StateFlow<NoiseMode> = _selectedNoiseMode.asStateFlow()

    private val _selectedProfile = MutableStateFlow(NoiseProfileType.DJ_LOUDSPEAKER)
    val selectedProfile: StateFlow<NoiseProfileType> = _selectedProfile.asStateFlow()

    private val _noiseReductionStrength = MutableStateFlow(0.85f)
    val noiseReductionStrength: StateFlow<Float> = _noiseReductionStrength.asStateFlow()

    private val _voiceEnhancementLevel = MutableStateFlow(0.75f)
    val voiceEnhancementLevel: StateFlow<Float> = _voiceEnhancementLevel.asStateFlow()

    private val _audioRoute = MutableStateFlow(AudioRoute.EARPIECE)
    val audioRoute: StateFlow<AudioRoute> = _audioRoute.asStateFlow()

    private val _isLiveMicMonitorActive = MutableStateFlow(false)
    val isLiveMicMonitorActive: StateFlow<Boolean> = _isLiveMicMonitorActive.asStateFlow()

    private val _callSessionSeconds = MutableStateFlow(0L)
    val callSessionSeconds: StateFlow<Long> = _callSessionSeconds.asStateFlow()

    private val _noiseTestState = MutableStateFlow<NoiseTestUiState>(NoiseTestUiState.Idle)
    val noiseTestState: StateFlow<NoiseTestUiState> = _noiseTestState.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private var callTimerJob: Job? = null
    private var testJob: Job? = null

    init {
        // Start default call session timer
        startSessionTimer()
    }

    fun onMicPermissionResult(granted: Boolean) {
        audioAnalyzer.startAnalysis(granted)
    }

    fun navigateTo(screen: ClearCallScreen) {
        _currentScreen.value = screen
    }

    fun toggleClearCall() {
        val next = !_isClearCallActive.value
        _isClearCallActive.value = next
        audioAnalyzer.isClearCallActive = next
        if (next) {
            startSessionTimer()
        } else {
            stopSessionTimer()
        }
    }

    fun toggleAi() {
        val next = !_isAiEnabled.value
        _isAiEnabled.value = next
        audioAnalyzer.isAiEnabled = next
    }

    fun selectNoiseMode(mode: NoiseMode) {
        _selectedNoiseMode.value = mode
        audioAnalyzer.activeMode = mode
        _noiseReductionStrength.value = mode.filterLevel
        audioAnalyzer.reductionStrength = mode.filterLevel
    }

    fun selectProfile(profile: NoiseProfileType) {
        _selectedProfile.value = profile
        _noiseReductionStrength.value = profile.defaultReductionStrength
        audioAnalyzer.reductionStrength = profile.defaultReductionStrength
    }

    fun setNoiseReduction(value: Float) {
        _noiseReductionStrength.value = value
        audioAnalyzer.reductionStrength = value
    }

    fun setVoiceEnhancement(value: Float) {
        _voiceEnhancementLevel.value = value
        audioAnalyzer.voiceEnhancement = value
    }

    fun setAudioRoute(route: AudioRoute) {
        _audioRoute.value = route
        audioAnalyzer.setAudioRoute(route)
    }

    fun toggleLiveMicMonitor() {
        val next = !_isLiveMicMonitorActive.value
        _isLiveMicMonitorActive.value = next
        audioAnalyzer.toggleLiveMicMonitor(next, analysisData.value.micPermissionGranted)
    }

    fun startNoiseTest() {
        testJob?.cancel()
        testJob = viewModelScope.launch {
            _noiseTestState.value = NoiseTestUiState.Running(0f, 5)
            val result = audioAnalyzer.run5SecondNoiseTest { progress, secondsLeft ->
                _noiseTestState.value = NoiseTestUiState.Running(progress, secondsLeft)
            }
            _noiseTestState.value = NoiseTestUiState.Completed(result)
        }
    }

    fun resetNoiseTest() {
        testJob?.cancel()
        _noiseTestState.value = NoiseTestUiState.Idle
    }

    fun applyTestRecommendation(result: NoiseTestResult) {
        selectProfile(result.detectedProfile)
        selectNoiseMode(result.recommendedMode)
        _currentScreen.value = ClearCallScreen.CLEAR_CALL
    }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        val updated = transform(_settings.value)
        _settings.value = updated
        audioAnalyzer.autoDetectionEnabled = updated.autoNoiseDetection
    }

    private fun startSessionTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _callSessionSeconds.value += 1
            }
        }
    }

    private fun stopSessionTimer() {
        callTimerJob?.cancel()
        callTimerJob = null
        _callSessionSeconds.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        audioAnalyzer.stopAnalysis()
        callTimerJob?.cancel()
        testJob?.cancel()
    }
}
