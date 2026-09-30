package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioAnalysisData
import com.example.model.AudioRoute
import com.example.model.NoiseProfileType
import com.example.ui.components.AudioRouteSelector
import com.example.ui.components.DecibelGauge
import com.example.ui.components.LiveNoiseVisualizer
import com.example.ui.components.VoiceClarityMeter
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiCyanBright
import com.example.ui.theme.AiGreen
import com.example.ui.theme.AiGreenBright
import com.example.ui.theme.ClearCallBackground
import com.example.ui.theme.ClearCallOutline
import com.example.ui.theme.ClearCallSurface
import com.example.ui.theme.ClearCallSurfaceContainer
import com.example.ui.theme.ClearCallSurfaceVariant
import com.example.ui.theme.ClearCallTextMuted
import com.example.ui.theme.ClearCallTextPrimary
import com.example.ui.theme.ClearCallTextSecondary
import com.example.ui.theme.NoiseExtreme
import com.example.ui.theme.NoiseHigh

@Composable
fun ClearCallActiveScreen(
    analysisData: AudioAnalysisData,
    isClearCallActive: Boolean,
    isAiEnabled: Boolean,
    noiseReductionStrength: Float,
    voiceEnhancementLevel: Float,
    selectedAudioRoute: AudioRoute,
    isLiveMicMonitorActive: Boolean,
    callDurationSeconds: Long,
    activeProfile: NoiseProfileType,
    onToggleAi: () -> Unit,
    onNoiseReductionChange: (Float) -> Unit,
    onVoiceEnhancementChange: (Float) -> Unit,
    onAudioRouteChange: (AudioRoute) -> Unit,
    onToggleMicMonitor: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val formattedDuration = String.format(
        "%02d:%02d",
        callDurationSeconds / 60,
        callDurationSeconds % 60
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClearCallBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("clear_call_active_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ClearCallSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = ClearCallTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "CLEAR CALL",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = ClearCallTextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Active Acoustic Companion",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClearCallTextMuted
                    )
                }
            }

            // Duration Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(ClearCallSurfaceVariant)
                    .border(1.dp, ClearCallOutline, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isClearCallActive) AiGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedDuration,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ClearCallTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Banner: "CLEARCALL ACTIVE"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isClearCallActive && isAiEnabled) {
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF003D47),
                                Color(0xFF004D26)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(
                                ClearCallSurfaceVariant,
                                ClearCallSurface
                            )
                        )
                    }
                )
                .border(
                    1.dp,
                    if (isClearCallActive && isAiEnabled) AiGreen.copy(alpha = 0.6f) else ClearCallOutline,
                    RoundedCornerShape(20.dp)
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isClearCallActive && isAiEnabled) AiGreenBright else ClearCallTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isClearCallActive && isAiEnabled) "CLEARCALL ACTIVE" else "CLEARCALL BYPASS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = if (isClearCallActive && isAiEnabled) AiGreenBright else ClearCallTextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isClearCallActive && isAiEnabled)
                            "Noise Shield: ${activeProfile.title} Suppression"
                        else
                            "AI Engine Suspended (Raw Audio Passthrough)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ClearCallTextPrimary.copy(alpha = 0.85f)
                    )
                }

                // AI Master Switch
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AI ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isAiEnabled) AiCyanBright else ClearCallTextMuted,
                        fontSize = 10.sp
                    )
                    Switch(
                        checked = isAiEnabled,
                        onCheckedChange = { onToggleAi() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF002229),
                            checkedTrackColor = AiCyan,
                            uncheckedThumbColor = ClearCallTextMuted,
                            uncheckedTrackColor = ClearCallSurfaceContainer
                        ),
                        modifier = Modifier.testTag("ai_toggle_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Noise Level Meter
        DecibelGauge(
            currentDb = analysisData.currentDb,
            peakDb = analysisData.peakDb,
            modifier = Modifier.testTag("active_decibel_gauge")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Audio Spectrum Visualizer
        LiveNoiseVisualizer(
            frequencyBands = analysisData.frequencyBands,
            currentDb = analysisData.currentDb,
            isAiActive = isClearCallActive && isAiEnabled,
            modifier = Modifier.testTag("active_spectrum_visualizer")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Voice Clarity Percentage Display
        VoiceClarityMeter(
            clarityPercentage = analysisData.clarityPercentage,
            isClearCallActive = isClearCallActive,
            isAiEnabled = isAiEnabled,
            modifier = Modifier.testTag("active_voice_clarity_meter")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Noise Reduction Control Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ClearCallSurface)
                .border(1.dp, ClearCallOutline, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NOISE REDUCTION CONTROL",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClearCallTextMuted,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Attenuates surrounding noise & loud background",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClearCallTextSecondary
                        )
                    }

                    Text(
                        text = "${(noiseReductionStrength * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AiCyan
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = noiseReductionStrength,
                    onValueChange = onNoiseReductionChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = AiCyan,
                        activeTrackColor = AiCyan,
                        inactiveTrackColor = ClearCallOutline
                    ),
                    modifier = Modifier.testTag("noise_reduction_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0% (Bypass)", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                    Text("50% (Standard)", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                    Text("100% (Maximum)", style = MaterialTheme.typography.labelSmall, color = AiCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Voice Enhancement Control Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ClearCallSurface)
                .border(1.dp, ClearCallOutline, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VOICE ENHANCEMENT CONTROL",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClearCallTextMuted,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Isolates vocal harmonics & boosts speech intelligibility",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClearCallTextSecondary
                        )
                    }

                    Text(
                        text = "${(voiceEnhancementLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AiGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = voiceEnhancementLevel,
                    onValueChange = onVoiceEnhancementChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = AiGreen,
                        activeTrackColor = AiGreen,
                        inactiveTrackColor = ClearCallOutline
                    ),
                    modifier = Modifier.testTag("voice_enhancement_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Natural", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                    Text("Balanced", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                    Text("Hyper-Intelligible", style = MaterialTheme.typography.labelSmall, color = AiGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Audio Route Selector (Speaker / Earpiece / Bluetooth)
        AudioRouteSelector(
            selectedRoute = selectedAudioRoute,
            onRouteChange = onAudioRouteChange,
            modifier = Modifier.testTag("audio_route_selector")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Audio Test Monitor (Hear your voice with filter)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ClearCallSurface)
                .border(
                    1.dp,
                    if (isLiveMicMonitorActive) AiCyan.copy(alpha = 0.7f) else ClearCallOutline,
                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isLiveMicMonitorActive) AiCyan.copy(alpha = 0.2f) else ClearCallSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = if (isLiveMicMonitorActive) AiCyan else ClearCallTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Live Audio Mic Monitor",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ClearCallTextPrimary
                        )
                        Text(
                            text = "Hear your voice through earpiece with filter applied",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClearCallTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isLiveMicMonitorActive,
                    onCheckedChange = { onToggleMicMonitor() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF002229),
                        checkedTrackColor = AiCyan
                    ),
                    modifier = Modifier.testTag("mic_monitor_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
