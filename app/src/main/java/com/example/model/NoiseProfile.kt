package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGreen
import com.example.ui.theme.NoiseExtreme
import com.example.ui.theme.NoiseHigh
import com.example.ui.theme.NoiseMedium

enum class NoiseProfileType(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val targetFrequencies: String,
    val accentColor: Color,
    val typicalDbRange: String,
    val defaultReductionStrength: Float
) {
    DJ_LOUDSPEAKER(
        title = "DJ / Loudspeaker",
        description = "Sub-bass rumble suppression & high SPL speaker bleed dampening",
        icon = Icons.Default.MusicNote,
        targetFrequencies = "20 Hz - 250 Hz & 4 kHz - 8 kHz",
        accentColor = NoiseExtreme,
        typicalDbRange = "85 - 105 dB",
        defaultReductionStrength = 0.95f
    ),
    TRAFFIC(
        title = "Traffic & Engine",
        description = "Vehicle drone filtering, exhaust rumble & horn transient dampening",
        icon = Icons.Default.DirectionsCar,
        targetFrequencies = "60 Hz - 400 Hz",
        accentColor = NoiseHigh,
        typicalDbRange = "70 - 85 dB",
        defaultReductionStrength = 0.80f
    ),
    CROWD(
        title = "Crowd & Babble",
        description = "Multi-talker babble isolation & voice formant notch separation",
        icon = Icons.Default.Groups,
        targetFrequencies = "300 Hz - 3.4 kHz",
        accentColor = AiCyan,
        typicalDbRange = "65 - 82 dB",
        defaultReductionStrength = 0.85f
    ),
    CONSTRUCTION(
        title = "Construction",
        description = "Impulsive hammer strikes, drilling harmonics & heavy machinery gating",
        icon = Icons.Default.Construction,
        targetFrequencies = "100 Hz - 6 kHz (Impulsive)",
        accentColor = NoiseHigh,
        typicalDbRange = "80 - 98 dB",
        defaultReductionStrength = 0.90f
    ),
    WIND(
        title = "Wind & Airflow",
        description = "Turbulent microphone buffeting reduction & high-pass 120Hz cutoff",
        icon = Icons.Default.Waves,
        targetFrequencies = "10 Hz - 180 Hz",
        accentColor = AiGreen,
        typicalDbRange = "55 - 75 dB",
        defaultReductionStrength = 0.75f
    ),
    HOME_NOISE(
        title = "Home & Office",
        description = "HVAC hum, refrigerator drone, ceiling fan & indoor echo cancellation",
        icon = Icons.Default.Home,
        targetFrequencies = "50 Hz - 1 kHz",
        accentColor = AiCyan,
        typicalDbRange = "38 - 58 dB",
        defaultReductionStrength = 0.60f
    )
}

enum class NoiseMode(
    val title: String,
    val dbAttenuation: String,
    val description: String,
    val filterLevel: Float
) {
    AUTO(
        title = "Auto",
        dbAttenuation = "Adaptive 12-32 dB",
        description = "Neural model continuously adapts attenuation to measured acoustic levels",
        filterLevel = 0.75f
    ),
    LIGHT(
        title = "Light",
        dbAttenuation = "6 - 10 dB",
        description = "Preserves ambient room atmosphere with gentle voice clarity lift",
        filterLevel = 0.40f
    ),
    MEDIUM(
        title = "Medium",
        dbAttenuation = "12 - 18 dB",
        description = "Balanced background suppression for standard cafe & street noise",
        filterLevel = 0.65f
    ),
    STRONG(
        title = "Strong",
        dbAttenuation = "20 - 26 dB",
        description = "Aggressive noise gate for loud traffic, subway and lively venues",
        filterLevel = 0.85f
    ),
    MAXIMUM(
        title = "Maximum",
        dbAttenuation = "28 - 34 dB",
        description = "Extreme isolation for concerts, stadiums, club DJs and construction",
        filterLevel = 1.0f
    )
}

enum class NoiseSeverity(val label: String, val color: Color) {
    LOW("Low", AiGreen),
    MEDIUM("Medium", NoiseMedium),
    HIGH("High", NoiseHigh),
    VERY_HIGH("Very High", NoiseExtreme)
}

data class NoiseTestResult(
    val timestamp: Long = System.currentTimeMillis(),
    val averageDb: Float,
    val peakDb: Float,
    val severity: NoiseSeverity,
    val detectedProfile: NoiseProfileType,
    val recommendedMode: NoiseMode,
    val recommendationSummary: String
)

enum class AudioRoute(val label: String, val description: String) {
    EARPIECE("Earpiece", "Private handheld receiver"),
    SPEAKER("Speaker", "Hands-free loudspeaker with acoustic echo guard"),
    BLUETOOTH("Bluetooth", "Connected wireless headset / earbuds")
}

data class AppSettings(
    val autoStartOnCall: Boolean = true,
    val autoNoiseDetection: Boolean = true,
    val voiceEnhancement: Boolean = true,
    val deepVoiceBoost: Boolean = true,
    val formantClarity: Boolean = true,
    val deReverberation: Boolean = true,
    val defaultNoiseMode: NoiseMode = NoiseMode.AUTO,
    val pureOledDark: Boolean = false,
    val selectedLanguage: String = "English (US)",
    val showTechnicalCallNotice: Boolean = true
)
