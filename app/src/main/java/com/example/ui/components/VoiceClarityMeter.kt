package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiCyanBright
import com.example.ui.theme.AiGreen
import com.example.ui.theme.ClearCallOutline
import com.example.ui.theme.ClearCallSurface
import com.example.ui.theme.ClearCallTextMuted
import com.example.ui.theme.ClearCallTextPrimary
import com.example.ui.theme.ClearCallTextSecondary
import com.example.ui.theme.NoiseHigh
import com.example.ui.theme.NoiseLow

@Composable
fun VoiceClarityMeter(
    clarityPercentage: Int,
    isClearCallActive: Boolean,
    isAiEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = clarityPercentage / 100f,
        animationSpec = tween(300),
        label = "clarityAnim"
    )

    val isActive = isClearCallActive && isAiEnabled

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ClearCallSurface)
            .border(
                1.dp,
                if (isActive) AiGreen.copy(alpha = 0.5f) else ClearCallOutline,
                RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isActive) AiGreen.copy(alpha = 0.18f) else ClearCallOutline.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Voice Clarity",
                            tint = if (isActive) AiGreen else ClearCallTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "VOICE CLARITY STATUS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClearCallTextMuted,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isActive) "Clean Speech Output" else "Raw Untreated Microphone",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) AiGreen else NoiseHigh
                        )
                    }
                }

                Text(
                    text = "$clarityPercentage%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (clarityPercentage >= 90) AiGreen else if (clarityPercentage >= 70) AiCyan else NoiseHigh
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (clarityPercentage >= 90) AiGreen else if (clarityPercentage >= 70) AiCyan else NoiseHigh,
                trackColor = ClearCallOutline,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isActive) AiGreen else ClearCallTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isActive) "Vocal Formants Isolated & Amplified" else "Voice vulnerable to acoustic drown-out",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClearCallTextSecondary
                    )
                }

                Text(
                    text = if (isActive) "SN Ratio: +28 dB" else "SN Ratio: -6 dB",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) AiCyanBright else ClearCallTextMuted
                )
            }
        }
    }
}
