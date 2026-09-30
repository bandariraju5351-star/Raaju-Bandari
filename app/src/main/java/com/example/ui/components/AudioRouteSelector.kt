package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioRoute
import com.example.ui.theme.AiCyan
import com.example.ui.theme.ClearCallOutline
import com.example.ui.theme.ClearCallSurface
import com.example.ui.theme.ClearCallSurfaceVariant
import com.example.ui.theme.ClearCallTextMuted
import com.example.ui.theme.ClearCallTextPrimary

@Composable
fun AudioRouteSelector(
    selectedRoute: AudioRoute,
    onRouteChange: (AudioRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "AUDIO OUTPUT ROUTING",
            style = MaterialTheme.typography.labelSmall,
            color = ClearCallTextMuted,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AudioRoute.values().forEach { route ->
                val isSelected = route == selectedRoute
                val icon = when (route) {
                    AudioRoute.EARPIECE -> Icons.Default.Hearing
                    AudioRoute.SPEAKER -> Icons.Default.VolumeUp
                    AudioRoute.BLUETOOTH -> Icons.Default.Bluetooth
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AiCyan.copy(alpha = 0.14f) else ClearCallSurface)
                        .border(
                            1.dp,
                            if (isSelected) AiCyan else ClearCallOutline,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(role = Role.RadioButton) { onRouteChange(route) }
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = route.label,
                            tint = if (isSelected) AiCyan else ClearCallTextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = route.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ClearCallTextPrimary else ClearCallTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
