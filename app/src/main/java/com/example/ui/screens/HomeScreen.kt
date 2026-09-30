package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AudioAnalysisData
import com.example.model.NoiseMode
import com.example.model.NoiseProfileType
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
import com.example.ui.theme.GlowCyan
import com.example.ui.theme.GlowGreen
import com.example.ui.theme.NoiseExtreme
import com.example.viewmodel.ClearCallScreen

@Composable
fun HomeScreen(
    analysisData: AudioAnalysisData,
    isClearCallActive: Boolean,
    isAiEnabled: Boolean,
    activeProfile: NoiseProfileType,
    activeMode: NoiseMode,
    onToggleClearCall: () -> Unit,
    onStartClearCall: () -> Unit,
    onStartNoiseTest: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNoiseControl: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClearCallBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("home_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header with Logo, Status Chip and Settings Icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ClearCallSurfaceVariant)
                        .border(1.dp, AiCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clearcall_logo),
                        contentDescription = "CLEARCALL AI Logo",
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CLEARCALL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ClearCallTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AiCyan)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AI",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF002229),
                                fontSize = 10.sp
                            )
                        }
                    }
                    Text(
                        text = if (isClearCallActive) "Acoustic Shield Active" else "Shield Inactive",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isClearCallActive) AiGreen else ClearCallTextMuted
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .testTag("settings_button")
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ClearCallSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = ClearCallTextSecondary
                )
            }
        }

        // Active Profile Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ClearCallSurfaceVariant.copy(alpha = 0.8f))
                .border(1.dp, ClearCallOutline, RoundedCornerShape(12.dp))
                .clickable { onOpenNoiseControl() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("profile_banner")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(activeProfile.accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = activeProfile.icon,
                            contentDescription = null,
                            tint = activeProfile.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Detected Profile: ${analysisData.detectedProfile.title}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = ClearCallTextPrimary
                        )
                        Text(
                            text = "Mode: ${activeMode.title} (${activeMode.dbAttenuation})",
                            style = MaterialTheme.typography.labelSmall,
                            color = AiCyan
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ClearCallSurfaceContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Customize",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClearCallTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Large CLEARCALL ON/OFF Button with glowing ring & visual pulsation
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(210.dp)
        ) {
            // Outer glowing aura
            if (isClearCallActive) {
                Box(
                    modifier = Modifier
                        .size(195.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    GlowCyan,
                                    GlowGreen,
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Outer border ring
            Box(
                modifier = Modifier
                    .size(175.dp)
                    .clip(CircleShape)
                    .background(ClearCallSurfaceVariant)
                    .border(
                        width = if (isClearCallActive) 3.dp else 1.5.dp,
                        brush = if (isClearCallActive) {
                            Brush.sweepGradient(listOf(AiCyan, AiGreen, AiCyanBright, AiCyan))
                        } else {
                            Brush.linearGradient(listOf(ClearCallOutline, ClearCallOutline))
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Power Button
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(
                            if (isClearCallActive) {
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF003D47),
                                        Color(0xFF002228),
                                        Color(0xFF06141F)
                                    )
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        ClearCallSurface,
                                        Color(0xFF0B1017)
                                    )
                                )
                            }
                        )
                        .clickable { onToggleClearCall() }
                        .testTag("clearcall_power_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "CLEARCALL Power",
                            tint = if (isClearCallActive) AiCyanBright else ClearCallTextMuted,
                            modifier = Modifier.size(46.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isClearCallActive) "CLEARCALL ON" else "CLEARCALL OFF",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = if (isClearCallActive) AiCyanBright else ClearCallTextMuted
                        )
                        Text(
                            text = if (isClearCallActive) "AI Active" else "Tap to Enable",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isClearCallActive) AiGreenBright else ClearCallTextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Environmental Noise Level Meter
        DecibelGauge(
            currentDb = analysisData.currentDb,
            peakDb = analysisData.peakDb,
            modifier = Modifier.testTag("home_decibel_gauge")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Spectral Frequency Visualizer
        LiveNoiseVisualizer(
            frequencyBands = analysisData.frequencyBands,
            currentDb = analysisData.currentDb,
            isAiActive = isClearCallActive && isAiEnabled,
            modifier = Modifier.testTag("home_visualizer")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Voice Clarity Status Meter
        VoiceClarityMeter(
            clarityPercentage = analysisData.clarityPercentage,
            isClearCallActive = isClearCallActive,
            isAiEnabled = isAiEnabled,
            modifier = Modifier.testTag("home_voice_clarity")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row: "Start Clear Call" & "Noise Test"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onStartClearCall,
                modifier = Modifier
                    .weight(1.2f)
                    .height(54.dp)
                    .testTag("start_clear_call_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AiCyan,
                    contentColor = Color(0xFF002229)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Clear Call",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onStartNoiseTest,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("noise_test_button"),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AiGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = AiGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Noise Test",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AiGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
