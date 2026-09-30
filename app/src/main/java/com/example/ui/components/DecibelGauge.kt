package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiCyan
import com.example.ui.theme.ClearCallOutline
import com.example.ui.theme.ClearCallSurface
import com.example.ui.theme.ClearCallTextMuted
import com.example.ui.theme.ClearCallTextPrimary
import com.example.ui.theme.ClearCallTextSecondary
import com.example.ui.theme.NoiseExtreme
import com.example.ui.theme.NoiseHigh
import com.example.ui.theme.NoiseLow
import com.example.ui.theme.NoiseMedium

@Composable
fun DecibelGauge(
    currentDb: Float,
    peakDb: Float,
    modifier: Modifier = Modifier
) {
    val animatedDb by animateFloatAsState(
        targetValue = currentDb,
        animationSpec = tween(durationMillis = 150),
        label = "dbAnim"
    )

    val dbFraction = ((animatedDb - 30f) / 80f).coerceIn(0f, 1f)

    val statusColor by animateColorAsState(
        targetValue = when {
            animatedDb > 85f -> NoiseExtreme
            animatedDb > 70f -> NoiseHigh
            animatedDb > 54f -> NoiseMedium
            else -> NoiseLow
        },
        label = "statusColor"
    )

    val statusText = when {
        animatedDb > 88f -> "EXTREME (DJ / CONCERT)"
        animatedDb > 78f -> "VERY HIGH (CONSTRUCTION)"
        animatedDb > 68f -> "HIGH (HEAVY TRAFFIC)"
        animatedDb > 54f -> "MODERATE (CROWD / CAFE)"
        else -> "LOW (QUIET ROOM)"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ClearCallSurface)
            .border(1.dp, ClearCallOutline, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ENVIRONMENTAL NOISE LEVEL",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClearCallTextMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arc Meter Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    val arcSize = size.width - strokeWidth

                    // Background track (240 degrees arc)
                    drawArc(
                        color = ClearCallOutline.copy(alpha = 0.5f),
                        startAngle = 150f,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active gradient arc
                    val sweep = 240f * dbFraction
                    if (sweep > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to NoiseLow,
                                0.35f to NoiseMedium,
                                0.7f to NoiseHigh,
                                1.0f to NoiseExtreme
                            ),
                            startAngle = 150f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedDb.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ClearCallTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "DECIBELS (dB)",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClearCallTextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Peak and Floor Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Peak Level", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                    Text(
                        "${peakDb.toInt()} dB",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = NoiseExtreme
                    )
                }
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(ClearCallOutline)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Baseline Noise", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                    Text("34 dB SPL", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = NoiseLow)
                }
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(ClearCallOutline)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Safety Limit", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted)
                    Text("85 dB (OSHA)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = NoiseHigh)
                }
            }
        }
    }
}
