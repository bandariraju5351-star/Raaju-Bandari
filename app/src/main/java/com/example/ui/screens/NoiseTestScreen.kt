package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioAnalysisData
import com.example.model.NoiseSeverity
import com.example.model.NoiseTestResult
import com.example.ui.components.LiveNoiseVisualizer
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
import com.example.ui.theme.NoiseLow
import com.example.ui.theme.NoiseMedium
import com.example.viewmodel.NoiseTestUiState

@Composable
fun NoiseTestScreen(
    analysisData: AudioAnalysisData,
    testState: NoiseTestUiState,
    onStartTest: () -> Unit,
    onResetTest: () -> Unit,
    onApplyRecommendation: (NoiseTestResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClearCallBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("noise_test_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    .background(AiGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = AiGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "NOISE TEST",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = ClearCallTextPrimary
                )
                Text(
                    text = "5-Second High-Precision Acoustic Audit",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClearCallTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Countdown / Progress / Status Graphic
        when (testState) {
            is NoiseTestUiState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ClearCallSurface)
                        .border(1.dp, ClearCallOutline, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(AiCyan.copy(alpha = 0.12f))
                                .border(1.5.dp, AiCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = AiCyan,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Calibrate Ambient Acoustics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ClearCallTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Record 5 seconds of ambient audio to measure acoustic energy, classify noise, and calculate the optimal AI isolation profile.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClearCallTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onStartTest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("start_test_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AiCyan,
                                contentColor = Color(0xFF002229)
                            )
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start 5-Second Test",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            is NoiseTestUiState.Running -> {
                val animatedProgress by animateFloatAsState(
                    targetValue = testState.progress,
                    animationSpec = tween(100),
                    label = "testProgress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ClearCallSurface)
                        .border(1.dp, AiCyan, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SAMPLING ENVIRONMENT...",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = AiCyan
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Circular Countdown Meter
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(160.dp)
                        ) {
                            Canvas(modifier = Modifier.size(150.dp)) {
                                val strokeWidth = 10.dp.toPx()
                                val arcSize = size.width - strokeWidth

                                // Background track
                                drawArc(
                                    color = ClearCallOutline,
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                                    size = Size(arcSize, arcSize),
                                    style = Stroke(width = strokeWidth)
                                )

                                // Active progress arc
                                drawArc(
                                    brush = Brush.sweepGradient(listOf(AiCyan, AiGreen, AiCyanBright)),
                                    startAngle = -90f,
                                    sweepAngle = 360f * animatedProgress,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                                    size = Size(arcSize, arcSize),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${testState.secondsRemaining}s",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = ClearCallTextPrimary
                                )
                                Text(
                                    text = "${analysisData.currentDb.toInt()} dB",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = AiGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Keep microphone exposed to surrounding sound",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClearCallTextMuted
                        )
                    }
                }
            }

            is NoiseTestUiState.Completed -> {
                val result = testState.result
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ClearCallSurface)
                        .border(
                            1.5.dp,
                            result.severity.color,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                        .testTag("test_result_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = result.severity.color,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ANALYSIS COMPLETE",
                                    style = MaterialTheme.typography.labelMedium,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClearCallTextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(result.severity.color.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${result.severity.label} Noise",
                                    color = result.severity.color,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ClearCallSurfaceVariant)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Average Level", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                                Text(
                                    "${result.averageDb.toInt()} dB",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ClearCallTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ClearCallSurfaceVariant)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Peak Level", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                                Text(
                                    "${result.peakDb.toInt()} dB",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = NoiseExtreme
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ClearCallSurfaceVariant)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Acoustic Type", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                                Text(
                                    result.detectedProfile.title.split("/").first().trim(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = result.detectedProfile.accentColor,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Recommendation Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(ClearCallSurfaceContainer)
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AiCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RECOMMENDED SETTINGS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AiCyan
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = result.recommendationSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ClearCallTextSecondary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Suggested Mode: ${result.recommendedMode.title}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AiGreen
                                    )
                                    Text(
                                        text = "Profile: ${result.detectedProfile.title}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ClearCallTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onApplyRecommendation(result) },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .testTag("apply_recommendation_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AiGreen,
                                    contentColor = Color(0xFF00220F)
                                )
                            ) {
                                Text("Apply Profile", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }

                            OutlinedButton(
                                onClick = onResetTest,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(48.dp)
                                    .testTag("retest_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ClearCallOutline)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = ClearCallTextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Retest", color = ClearCallTextPrimary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Spectrum Visualizer
        LiveNoiseVisualizer(
            frequencyBands = analysisData.frequencyBands,
            currentDb = analysisData.currentDb,
            isAiActive = true,
            modifier = Modifier.testTag("test_spectrum_visualizer")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Severity Guide Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ClearCallSurface)
                .border(1.dp, ClearCallOutline, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACOUSTIC CLASSIFICATION CRITERIA",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClearCallTextMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                SeverityRow(severity = NoiseSeverity.LOW, dbRange = "< 48 dB", example = "Quiet home, office, library")
                SeverityRow(severity = NoiseSeverity.MEDIUM, dbRange = "48 - 65 dB", example = "Cafe, suburban street, air conditioning")
                SeverityRow(severity = NoiseSeverity.HIGH, dbRange = "65 - 80 dB", example = "Heavy city traffic, crowded transit, restaurants")
                SeverityRow(severity = NoiseSeverity.VERY_HIGH, dbRange = "> 80 dB", example = "DJ concerts, club loudspeakers, construction sites")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SeverityRow(severity: NoiseSeverity, dbRange: String, example: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(severity.color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = severity.label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = severity.color
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "($dbRange)",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = ClearCallTextMuted
            )
        }
        Text(
            text = example,
            style = MaterialTheme.typography.labelSmall,
            color = ClearCallTextSecondary,
            fontSize = 10.sp
        )
    }
}
