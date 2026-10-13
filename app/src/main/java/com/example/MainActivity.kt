package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.CleanStreakRepository
import com.example.ui.CleanStreakViewModel
import com.example.ui.components.AppLockOverlay
import com.example.ui.components.SettingsDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SosScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.CleanStreakTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CleanStreakViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CleanStreakRepository(
            habitDao = database.habitDao(),
            dailyCheckInDao = database.dailyCheckInDao(),
            relapseLogDao = database.relapseLogDao(),
            urgeLogDao = database.urgeLogDao()
        )
        CleanStreakViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDark = when (uiState.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            CleanStreakTheme(darkTheme = isDark) {
                CleanStreakApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanStreakApp(viewModel: CleanStreakViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Intercept back navigation on non-dashboard tabs
    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "CleanStreak",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedTab = 0 }) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = "App Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    actions = {
                        if (uiState.habitProfile.isAppLockEnabled) {
                            IconButton(
                                onClick = { viewModel.lockAppNow() },
                                modifier = Modifier.testTag("lock_app_now_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock Now",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier.testTag("settings_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Dashboard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("tab_dashboard")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Stats & Logs"
                            )
                        },
                        label = { Text("Stats & Logs") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.testTag("tab_stats")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = "SOS Urges"
                            )
                        },
                        label = { Text("SOS Urges") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.tertiary,
                            indicatorColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier.testTag("tab_sos")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> DashboardScreen(
                        uiState = uiState,
                        onPledgeClicked = { viewModel.pledgeToday() },
                        onSubmitCheckIn = { mood, urge, note, gratitude ->
                            viewModel.submitCheckIn(mood, urge, note, gratitude)
                        },
                        onRecordRelapse = { triggers, note, lesson ->
                            viewModel.recordRelapse(triggers, note, lesson)
                        },
                        onNavigateToSos = { selectedTab = 2 }
                    )
                    1 -> StatsScreen(
                        uiState = uiState,
                        onDeleteRelapseLog = { viewModel.deleteRelapseLog(it) }
                    )
                    2 -> SosScreen(
                        onRecordUrgeVictory = { intensity, tool ->
                            viewModel.recordUrgeOvercome(
                                trigger = "Spontaneous Urge",
                                copingTool = tool,
                                intensity = intensity
                            )
                        }
                    )
                }
            }
        }

        // App Lock Passcode Overlay (Gatekeeper)
        if (uiState.isAppLocked) {
            AppLockOverlay(
                onUnlock = { pin -> viewModel.unlockApp(pin) }
            )
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            habitProfile = uiState.habitProfile,
            themeMode = uiState.themeMode,
            onDismiss = { showSettingsDialog = false },
            onSaveHabit = { name, pledge, startTime ->
                viewModel.updateHabitSettings(name, pledge, startTime)
            },
            onSetAppLock = { enabled, pin ->
                viewModel.setAppLock(enabled, pin)
            },
            onSetThemeMode = { mode ->
                viewModel.setThemeMode(mode)
            },
            onResetAllData = {
                viewModel.resetAllData()
            }
        )
    }
}
