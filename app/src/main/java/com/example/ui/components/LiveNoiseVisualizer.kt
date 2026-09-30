package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiCyanBright
import com.example.ui.theme.AiGreen
import com.example.ui.theme.ClearCallOutline
import com.example.ui.theme.ClearCallSurfaceVariant
import com.example.ui.theme.ClearCallTextMuted
import com.example.ui.theme.ClearCallTextSecondary
import com.example.ui.theme.NoiseExtreme
import com.example.ui.theme.NoiseHigh
import com.example.ui.theme.NoiseLow
import com.example.ui.theme.NoiseMedium
import kotlin.math.sin

@Composable
fun LiveNoiseVisualizer(
    frequencyBands: List<Float>,
    currentDb: Float,
    isAiActive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ClearCallSurfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, ClearCallOutline, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                            .background(if (isAiActive) AiGreen else AiCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE ACOUSTIC SPECTRUM",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClearCallTextMuted,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${currentDb.toInt()} dB SPL",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        currentDb > 85f -> NoiseExtreme
                        currentDb > 70f -> NoiseHigh
                        currentDb > 55f -> NoiseMedium
                        else -> NoiseLow
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barCount = frequencyBands.size.coerceAtLeast(1)
                val spacing = 4.dp.toPx()
                val totalSpacing = spacing * (barCount - 1)
                val barWidth = (canvasWidth - totalSpacing) / barCount

                frequencyBands.forEachIndexed { index, energy ->
                    val animatedHeight = (energy * canvasHeight * (if (isAiActive) 0.9f else 1.1f))
                        .coerceIn(4.dp.toPx(), canvasHeight)
                    val left = index * (barWidth + spacing)
                    val top = canvasHeight - animatedHeight

                    val gradientBrush = Brush.verticalGradient(
                        colors = listOf(
                            if (index < 4) AiCyanBright else if (index < 10) AiGreen else AiCyan,
                            ClearCallSurfaceVariant
                        ),
                        startY = top,
                        endY = canvasHeight
                    )

                    drawRoundRect(
                        brush = gradientBrush,
                        topLeft = Offset(left, top),
                        size = Size(barWidth, animatedHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Draw peak dot on top of each bar
                    drawCircle(
                        color = if (isAiActive) AiCyanBright else Color.White,
                        radius = (barWidth * 0.35f).coerceIn(1.5f, 3.5f),
                        center = Offset(left + barWidth / 2f, top)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("20Hz (Sub-Bass)", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted, fontSize = 9.sp)
                Text("1kHz (Voice)", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted, fontSize = 9.sp)
                Text("8kHz (High)", style = MaterialTheme.typography.labelSmall, color = ClearCallTextMuted, fontSize = 9.sp)
            }
        }
    }
}
