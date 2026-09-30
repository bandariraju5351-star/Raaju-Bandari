package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ClearCallDarkColorScheme = darkColorScheme(
    primary = AiCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D5A),
    onPrimaryContainer = Color(0xFFB2EBF2),
    secondary = AiGreen,
    onSecondary = Color(0xFF00220F),
    secondaryContainer = Color(0xFF004D26),
    onSecondaryContainer = Color(0xFFB9F6CA),
    tertiary = AiCyanBright,
    onTertiary = Color(0xFF002026),
    background = ClearCallBackground,
    onBackground = ClearCallTextPrimary,
    surface = ClearCallSurface,
    onSurface = ClearCallTextPrimary,
    surfaceVariant = ClearCallSurfaceVariant,
    onSurfaceVariant = ClearCallTextSecondary,
    outline = ClearCallOutline,
    outlineVariant = ClearCallOutlineLight,
    error = NoiseExtreme,
    onError = Color.White
)

@Composable
fun ClearCallTheme(
    darkTheme: Boolean = true, // Default to modern dark mode as requested
    content: @Composable () -> Unit
) {
    val colorScheme = ClearCallDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ClearCallBackground.toArgb()
                window.navigationBarColor = ClearCallBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
