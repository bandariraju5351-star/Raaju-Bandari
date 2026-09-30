package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppSettings
import com.example.model.NoiseMode
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiCyanBright
import com.example.ui.theme.AiGreen
import com.example.ui.theme.ClearCallBackground
import com.example.ui.theme.ClearCallOutline
import com.example.ui.theme.ClearCallSurface
import com.example.ui.theme.ClearCallSurfaceContainer
import com.example.ui.theme.ClearCallSurfaceVariant
import com.example.ui.theme.ClearCallTextMuted
import com.example.ui.theme.ClearCallTextPrimary
import com.example.ui.theme.ClearCallTextSecondary

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNoiseModeDialog by remember { mutableStateOf(false) }

    val languages = listOf(
        "English (US)",
        "Spanish (Español)",
        "French (Français)",
        "German (Deutsch)",
        "Japanese (日本語)",
        "Hindi (हिन्दी)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClearCallBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("settings_screen")
    ) {
        // Top Header
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
                    .background(ClearCallSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = AiCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = ClearCallTextPrimary
                )
                Text(
                    text = "System, DSP Engine & Privacy Preferences",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClearCallTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: AUTOMATION & AUDIO
        SettingsSectionHeader(title = "AUTOMATION & DETECTION")

        SettingsCard {
            SettingsToggleItem(
                icon = Icons.Default.PlayCircle,
                title = "Auto Start",
                subtitle = "Automatically engage ClearCall AI when high environmental noise or incoming call is detected",
                checked = settings.autoStartOnCall,
                onCheckedChange = { value ->
                    onUpdateSettings { it.copy(autoStartOnCall = value) }
                },
                testTag = "toggle_auto_start"
            )

            SettingsDivider()

            SettingsToggleItem(
                icon = Icons.Default.Waves,
                title = "Auto Noise Detection",
                subtitle = "Continuously classify environment (DJ, traffic, crowd, etc.) and adapt filter parameters",
                checked = settings.autoNoiseDetection,
                onCheckedChange = { value ->
                    onUpdateSettings { it.copy(autoNoiseDetection = value) }
                },
                testTag = "toggle_auto_detection"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: AUDIO PROCESSING
        SettingsSectionHeader(title = "AUDIO ENHANCEMENT & FILTERING")

        SettingsCard {
            SettingsToggleItem(
                icon = Icons.Default.RecordVoiceOver,
                title = "Voice Enhancement",
                subtitle = "Master switch for vocal formant boosting and intelligible presence lift",
                checked = settings.voiceEnhancement,
                onCheckedChange = { value ->
                    onUpdateSettings { it.copy(voiceEnhancement = value) }
                },
                testTag = "toggle_voice_enhancement"
            )

            if (settings.voiceEnhancement) {
                SettingsDivider()

                SettingsToggleItem(
                    icon = Icons.Default.VolumeUp,
                    title = "Deep Voice Boost",
                    subtitle = "Warm low-mid vocal compensation for loud background bleed",
                    checked = settings.deepVoiceBoost,
                    onCheckedChange = { value ->
                        onUpdateSettings { it.copy(deepVoiceBoost = value) }
                    },
                    testTag = "toggle_deep_voice_boost"
                )

                SettingsDivider()

                SettingsToggleItem(
                    icon = Icons.Default.VolumeDown,
                    title = "Formant Speech Clarity",
                    subtitle = "Sharpen consonant intelligibility in babble & crowd environments",
                    checked = settings.formantClarity,
                    onCheckedChange = { value ->
                        onUpdateSettings { it.copy(formantClarity = value) }
                    },
                    testTag = "toggle_formant_clarity"
                )
            }

            SettingsDivider()

            SettingsNavigationItem(
                icon = Icons.Default.Waves,
                title = "Default Noise Reduction Mode",
                currentValue = "${settings.defaultNoiseMode.title} (${settings.defaultNoiseMode.dbAttenuation})",
                onClick = { showNoiseModeDialog = true },
                testTag = "item_default_noise_mode"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: DISPLAY & LOCALIZATION
        SettingsSectionHeader(title = "INTERFACE & LOCALIZATION")

        SettingsCard {
            SettingsToggleItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode / Pure OLED",
                subtitle = "Ultra-high contrast deep black theme optimized for battery saving",
                checked = settings.pureOledDark,
                onCheckedChange = { value ->
                    onUpdateSettings { it.copy(pureOledDark = value) }
                },
                testTag = "toggle_pure_oled"
            )

            SettingsDivider()

            SettingsNavigationItem(
                icon = Icons.Default.Language,
                title = "Language",
                currentValue = settings.selectedLanguage,
                onClick = { showLanguageDialog = true },
                testTag = "item_language"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: ABOUT & ARCHITECTURE
        SettingsSectionHeader(title = "ABOUT & TECHNICAL DISCLOSURES")

        SettingsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AiCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AiCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CLEARCALL AI v1.0.0",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ClearCallTextPrimary
                        )
                        Text(
                            text = "Modular Neural Audio Architecture",
                            style = MaterialTheme.typography.labelSmall,
                            color = AiGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Acoustic Engine Latency: ~12ms Ultra-Low Latency DSP Pipeline\n" +
                            "Classification Models: Spectral Energy, Crest Factor & Transient Harmonic Analysis\n" +
                            "Privacy Guarantee: 100% On-Device Real-Time Processing. Zero audio data is recorded or transmitted to cloud servers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ClearCallTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ClearCallSurfaceContainer)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AiGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Technical System Notice",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ClearCallTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Android OS security architecture restricts third-party applications from directly intercepting baseband cellular voice streams during phone calls. CLEARCALL AI serves as an active acoustic companion: monitoring ambient pressure, classifying noise hazards, calibrating optimal isolation profiles, and processing live microphone audio through hardware-compatible communication routes.",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClearCallTextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ClearCallTextPrimary
                )
            },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings { it.copy(selectedLanguage = lang) }
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = lang == settings.selectedLanguage,
                                onClick = {
                                    onUpdateSettings { it.copy(selectedLanguage = lang) }
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AiCyan,
                                    unselectedColor = ClearCallOutline
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = lang,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ClearCallTextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = AiCyan)
                }
            },
            containerColor = ClearCallSurfaceVariant,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Default Noise Mode Dialog
    if (showNoiseModeDialog) {
        AlertDialog(
            onDismissRequest = { showNoiseModeDialog = false },
            title = {
                Text(
                    text = "Default Noise Reduction Mode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ClearCallTextPrimary
                )
            },
            text = {
                Column {
                    NoiseMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings { it.copy(defaultNoiseMode = mode) }
                                    showNoiseModeDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = mode == settings.defaultNoiseMode,
                                onClick = {
                                    onUpdateSettings { it.copy(defaultNoiseMode = mode) }
                                    showNoiseModeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AiCyan,
                                    unselectedColor = ClearCallOutline
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ClearCallTextPrimary
                                )
                                Text(
                                    text = mode.dbAttenuation,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AiGreen
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNoiseModeDialog = false }) {
                    Text("Close", color = AiCyan)
                }
            },
            containerColor = ClearCallSurfaceVariant,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = ClearCallTextMuted,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ClearCallSurface)
            .border(1.dp, ClearCallOutline, RoundedCornerShape(18.dp))
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ClearCallSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) AiCyan else ClearCallTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ClearCallTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = ClearCallTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF002229),
                checkedTrackColor = AiCyan,
                uncheckedThumbColor = ClearCallTextMuted,
                uncheckedTrackColor = ClearCallSurfaceVariant
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    currentValue: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ClearCallSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AiCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = ClearCallTextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = currentValue,
                style = MaterialTheme.typography.labelMedium,
                color = AiGreen
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = ClearCallTextMuted,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    Divider(
        color = ClearCallOutline.copy(alpha = 0.5f),
        thickness = 0.8.dp,
        modifier = Modifier.padding(start = 64.dp)
    )
}
