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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.NoiseMode
import com.example.model.NoiseProfileType
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

@Composable
fun AiNoiseControlScreen(
    analysisData: AudioAnalysisData,
    selectedMode: NoiseMode,
    selectedProfile: NoiseProfileType,
    onSelectMode: (NoiseMode) -> Unit,
    onSelectProfile: (NoiseProfileType) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClearCallBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("ai_noise_control_screen")
    ) {
        // Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AiCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = AiCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "AI NOISE CONTROL",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = ClearCallTextPrimary
                )
                Text(
                    text = "Intelligent Acoustic Profile Classifier",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClearCallTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Auto-Detection Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF0C2433),
                            Color(0xFF0F3227)
                        )
                    )
                )
                .border(1.dp, AiCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(16.dp)
                .testTag("live_detection_status_card")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AiGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE ACOUSTIC IDENTIFICATION",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = AiGreen
                        )
                    }

                    Text(
                        text = "${analysisData.currentDb.toInt()} dB SPL",
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = ClearCallTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(analysisData.detectedProfile.accentColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = analysisData.detectedProfile.icon,
                            contentDescription = null,
                            tint = analysisData.detectedProfile.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Dominant: ${analysisData.detectedProfile.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ClearCallTextPrimary
                        )
                        Text(
                            text = analysisData.detectedProfile.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = ClearCallTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Noise Mode Selection (Auto, Light, Medium, Strong, Maximum)
        Text(
            text = "NOISE REDUCTION MODES",
            style = MaterialTheme.typography.labelSmall,
            color = ClearCallTextMuted,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Mode Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(NoiseMode.values()) { mode ->
                val isSelected = mode == selectedMode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AiCyan.copy(alpha = 0.15f) else ClearCallSurface)
                        .border(
                            1.dp,
                            if (isSelected) AiCyan else ClearCallOutline,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectMode(mode) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("mode_chip_${mode.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isSelected) AiCyanBright else ClearCallTextPrimary
                        )
                        Text(
                            text = mode.dbAttenuation,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isSelected) AiGreen else ClearCallTextMuted
                        )
                    }
                }
            }
        }

        // Active Mode Description
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ClearCallSurfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Text(
                text = "${selectedMode.title} Mode: ${selectedMode.description}",
                style = MaterialTheme.typography.bodySmall,
                color = ClearCallTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: Environmental Noise Profiles (6 profiles)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ENVIRONMENTAL NOISE PROFILES",
                style = MaterialTheme.typography.labelSmall,
                color = ClearCallTextMuted,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Auto-Recognized",
                style = MaterialTheme.typography.labelSmall,
                color = AiGreen,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Profiles List
        NoiseProfileType.values().forEach { profile ->
            val isCurrentActive = profile == selectedProfile
            val isLiveDetected = profile == analysisData.detectedProfile

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isCurrentActive) ClearCallSurfaceContainer else ClearCallSurface)
                    .border(
                        1.5.dp,
                        if (isCurrentActive) profile.accentColor else ClearCallOutline,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectProfile(profile) }
                    .padding(16.dp)
                    .testTag("profile_card_${profile.name.lowercase()}")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(profile.accentColor.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = profile.icon,
                                    contentDescription = profile.title,
                                    tint = profile.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ClearCallTextPrimary
                                    )
                                    if (isLiveDetected) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AiGreen.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "DETECTED",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AiGreen
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Target SPL: ${profile.typicalDbRange}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ClearCallTextMuted
                                )
                            }
                        }

                        if (isCurrentActive) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(profile.accentColor)
                                    .size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = Color(0xFF002229),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = profile.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = ClearCallTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter: ${profile.targetFrequencies}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = profile.accentColor,
                            fontSize = 11.sp
                        )

                        Text(
                            text = "Reduction: -${(profile.defaultReductionStrength * 32).toInt()} dB",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ClearCallTextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
