package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AiNoiseControlScreen
import com.example.ui.screens.ClearCallActiveScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NoiseTestScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGreen
import com.example.ui.theme.ClearCallBackground
import com.example.ui.theme.ClearCallOutline
import com.example.ui.theme.ClearCallSurface
import com.example.ui.theme.ClearCallSurfaceVariant
import com.example.ui.theme.ClearCallTextMuted
import com.example.ui.theme.ClearCallTextPrimary
import com.example.ui.theme.ClearCallTheme
import com.example.viewmodel.ClearCallScreen
import com.example.viewmodel.ClearCallViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ClearCallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()

            ClearCallTheme(darkTheme = true) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

data class NavTabItem(
    val screen: ClearCallScreen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun MainAppContent(viewModel: ClearCallViewModel) {
    val context = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        viewModel.onMicPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            viewModel.onMicPermissionResult(true)
        }
    }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val analysisData by viewModel.analysisData.collectAsStateWithLifecycle()
    val isClearCallActive by viewModel.isClearCallActive.collectAsStateWithLifecycle()
    val isAiEnabled by viewModel.isAiEnabled.collectAsStateWithLifecycle()
    val selectedMode by viewModel.selectedNoiseMode.collectAsStateWithLifecycle()
    val selectedProfile by viewModel.selectedProfile.collectAsStateWithLifecycle()
    val noiseReductionStrength by viewModel.noiseReductionStrength.collectAsStateWithLifecycle()
    val voiceEnhancementLevel by viewModel.voiceEnhancementLevel.collectAsStateWithLifecycle()
    val audioRoute by viewModel.audioRoute.collectAsStateWithLifecycle()
    val isLiveMicMonitorActive by viewModel.isLiveMicMonitorActive.collectAsStateWithLifecycle()
    val callDurationSeconds by viewModel.callSessionSeconds.collectAsStateWithLifecycle()
    val noiseTestState by viewModel.noiseTestState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    // Handle back button: return to Home screen if on another screen
    BackHandler(enabled = currentScreen != ClearCallScreen.HOME) {
        viewModel.navigateTo(ClearCallScreen.HOME)
    }

    val navItems = listOf(
        NavTabItem(
            screen = ClearCallScreen.HOME,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = "Home"
        ),
        NavTabItem(
            screen = ClearCallScreen.CLEAR_CALL,
            selectedIcon = Icons.Filled.PhoneInTalk,
            unselectedIcon = Icons.Outlined.PhoneInTalk,
            label = "Clear Call"
        ),
        NavTabItem(
            screen = ClearCallScreen.AI_NOISE_CONTROL,
            selectedIcon = Icons.Filled.Tune,
            unselectedIcon = Icons.Outlined.Tune,
            label = "Profiles"
        ),
        NavTabItem(
            screen = ClearCallScreen.NOISE_TEST,
            selectedIcon = Icons.Filled.Speed,
            unselectedIcon = Icons.Outlined.Speed,
            label = "Test"
        ),
        NavTabItem(
            screen = ClearCallScreen.SETTINGS,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            label = "Settings"
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ClearCallBackground,
        bottomBar = {
            NavigationBar(
                containerColor = ClearCallSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(
                        width = 0.8.dp,
                        color = ClearCallOutline,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AiCyan,
                            selectedTextColor = AiCyan,
                            unselectedIconColor = ClearCallTextMuted,
                            unselectedTextColor = ClearCallTextMuted,
                            indicatorColor = AiCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_${item.screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Permission rationale banner if mic permission is missing
                AnimatedVisibility(visible = !hasMicPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ClearCallSurfaceVariant)
                            .border(1.dp, AiCyan.copy(alpha = 0.3f))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("permission_banner")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = AiCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Enable mic access for real-time acoustic analysis. (Running simulated acoustics)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ClearCallTextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AiCyan,
                                    contentColor = Color(0xFF002229)
                                ),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("grant_permission_button")
                            ) {
                                Text("Grant", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Screen Switcher
                when (currentScreen) {
                    ClearCallScreen.HOME -> {
                        HomeScreen(
                            analysisData = analysisData,
                            isClearCallActive = isClearCallActive,
                            isAiEnabled = isAiEnabled,
                            activeProfile = selectedProfile,
                            activeMode = selectedMode,
                            onToggleClearCall = { viewModel.toggleClearCall() },
                            onStartClearCall = { viewModel.navigateTo(ClearCallScreen.CLEAR_CALL) },
                            onStartNoiseTest = { viewModel.navigateTo(ClearCallScreen.NOISE_TEST) },
                            onOpenSettings = { viewModel.navigateTo(ClearCallScreen.SETTINGS) },
                            onOpenNoiseControl = { viewModel.navigateTo(ClearCallScreen.AI_NOISE_CONTROL) }
                        )
                    }

                    ClearCallScreen.CLEAR_CALL -> {
                        ClearCallActiveScreen(
                            analysisData = analysisData,
                            isClearCallActive = isClearCallActive,
                            isAiEnabled = isAiEnabled,
                            noiseReductionStrength = noiseReductionStrength,
                            voiceEnhancementLevel = voiceEnhancementLevel,
                            selectedAudioRoute = audioRoute,
                            isLiveMicMonitorActive = isLiveMicMonitorActive,
                            callDurationSeconds = callDurationSeconds,
                            activeProfile = selectedProfile,
                            onToggleAi = { viewModel.toggleAi() },
                            onNoiseReductionChange = { viewModel.setNoiseReduction(it) },
                            onVoiceEnhancementChange = { viewModel.setVoiceEnhancement(it) },
                            onAudioRouteChange = { viewModel.setAudioRoute(it) },
                            onToggleMicMonitor = { viewModel.toggleLiveMicMonitor() },
                            onBackToHome = { viewModel.navigateTo(ClearCallScreen.HOME) }
                        )
                    }

                    ClearCallScreen.AI_NOISE_CONTROL -> {
                        AiNoiseControlScreen(
                            analysisData = analysisData,
                            selectedMode = selectedMode,
                            selectedProfile = selectedProfile,
                            onSelectMode = { viewModel.selectNoiseMode(it) },
                            onSelectProfile = { viewModel.selectProfile(it) }
                        )
                    }

                    ClearCallScreen.NOISE_TEST -> {
                        NoiseTestScreen(
                            analysisData = analysisData,
                            testState = noiseTestState,
                            onStartTest = { viewModel.startNoiseTest() },
                            onResetTest = { viewModel.resetNoiseTest() },
                            onApplyRecommendation = { result ->
                                viewModel.applyTestRecommendation(result)
                            }
                        )
                    }

                    ClearCallScreen.SETTINGS -> {
                        SettingsScreen(
                            settings = settings,
                            onUpdateSettings = { viewModel.updateSettings(it) }
                        )
                    }
                }
            }
        }
    }
}
