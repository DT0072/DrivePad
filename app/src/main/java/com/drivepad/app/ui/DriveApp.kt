package com.drivepad.app.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepad.app.navigation.BottomNavItem
import com.drivepad.app.ui.components.DriveBottomNavBar
import com.drivepad.app.ui.components.DriveStatusBar
import com.drivepad.app.ui.screens.connectivity.ConnectivityScreen
import com.drivepad.app.ui.screens.home.HomeScreen
import com.drivepad.app.ui.screens.media.MediaScreen
import com.drivepad.app.ui.screens.navigation.NavigationScreen
import com.drivepad.app.ui.screens.projection.ProjectionScreen
import com.drivepad.app.ui.screens.radio.RadioScreen
import com.drivepad.app.ui.screens.settings.SettingsScreen

/**
 * Main scaffold that wraps status bar + content + bottom nav.
 * This is the root composable of the infotainment system.
 */
@Composable
fun DriveApp(
    viewModel: DriveViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Current tab
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }

    // Collect states
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val nowPlayingTitle by viewModel.nowPlayingTitle.collectAsStateWithLifecycle()
    val nowPlayingArtist by viewModel.nowPlayingArtist.collectAsStateWithLifecycle()
    val nowPlayingAlbum by viewModel.nowPlayingAlbum.collectAsStateWithLifecycle()
    val nowPlayingAlbumArt by viewModel.nowPlayingAlbumArt.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val playbackProgress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val totalDuration by viewModel.totalDuration.collectAsStateWithLifecycle()
    val activeMediaSource by viewModel.activeMediaSource.collectAsStateWithLifecycle()

    val radioStations by viewModel.radioStations.collectAsStateWithLifecycle()
    val currentStation by viewModel.currentStation.collectAsStateWithLifecycle()
    val currentFrequency by viewModel.currentFrequency.collectAsStateWithLifecycle()
    val isRadioPlaying by viewModel.isRadioPlaying.collectAsStateWithLifecycle()
    val radioPresets by viewModel.radioPresets.collectAsStateWithLifecycle()

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val launcherModeEnabled by viewModel.launcherModeEnabled.collectAsStateWithLifecycle()
    val autoLaunchOnPower by viewModel.autoLaunchOnPower.collectAsStateWithLifecycle()
    val autoExitOnDisconnect by viewModel.autoExitOnDisconnect.collectAsStateWithLifecycle()
    val autoLaunchOnBoot by viewModel.autoLaunchOnBoot.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Status Bar
        DriveStatusBar(
            weatherIcon = weather?.icon ?: "🌤️",
            weatherTemp = weather?.let { "${it.temperature.toInt()}°C" } ?: "--°C"
        )

        // Content area with animated transitions
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith
                        fadeOut(animationSpec = tween(200))
                },
                label = "screenTransition"
            ) { tab ->
                when (tab) {
                    BottomNavItem.HOME -> HomeScreen(
                        weatherIcon = weather?.icon ?: "🌤️",
                        weatherTemp = weather?.let { "${it.temperature.toInt()}°C" } ?: "--°C",
                        weatherDescription = weather?.description ?: "Loading...",
                        nowPlayingTitle = nowPlayingTitle,
                        nowPlayingArtist = nowPlayingArtist,
                        nowPlayingAlbumArt = nowPlayingAlbumArt,
                        isPlaying = isPlaying,
                        onPlayPause = viewModel::togglePlayPause,
                        onSkipNext = viewModel::skipNext,
                        onSkipPrevious = viewModel::skipPrevious,
                        onNavigateToScreen = { screenId ->
                            when (screenId) {
                                "navigation" -> selectedTab = BottomNavItem.NAVIGATION
                                "media" -> selectedTab = BottomNavItem.MEDIA
                                "radio" -> selectedTab = BottomNavItem.RADIO
                                "projection" -> selectedTab = BottomNavItem.PROJECTION
                                "connectivity" -> selectedTab = BottomNavItem.SETTINGS
                                "settings" -> selectedTab = BottomNavItem.SETTINGS
                            }
                        }
                    )

                    BottomNavItem.NAVIGATION -> NavigationScreen()

                    BottomNavItem.MEDIA -> MediaScreen(
                        nowPlayingTitle = nowPlayingTitle,
                        nowPlayingArtist = nowPlayingArtist,
                        nowPlayingAlbum = nowPlayingAlbum,
                        isPlaying = isPlaying,
                        playbackProgress = playbackProgress,
                        currentPosition = currentPosition,
                        totalDuration = totalDuration,
                        onPlayPause = viewModel::togglePlayPause,
                        onSkipNext = viewModel::skipNext,
                        onSkipPrevious = viewModel::skipPrevious,
                        onSeek = viewModel::seekTo,
                        activeSource = activeMediaSource,
                        onSourceSelected = viewModel::setActiveMediaSource
                    )

                    BottomNavItem.RADIO -> RadioScreen(
                        currentStation = currentStation,
                        currentFrequency = currentFrequency,
                        isPlaying = isRadioPlaying,
                        stations = radioStations,
                        presets = radioPresets,
                        onFrequencyChange = viewModel::setRadioFrequency,
                        onPlayPause = viewModel::toggleRadioPlayPause,
                        onSeekForward = viewModel::seekRadioForward,
                        onSeekBackward = viewModel::seekRadioBackward,
                        onStationSelect = viewModel::selectRadioStation,
                        onPresetSave = viewModel::savePreset,
                        onPresetLoad = viewModel::loadPreset,
                        onSearch = { /* open search dialog */ },
                        onAutoScan = viewModel::autoScanStations
                    )

                    BottomNavItem.PROJECTION -> ProjectionScreen()

                    BottomNavItem.SETTINGS -> SettingsScreen(
                        currentThemeMode = themeMode,
                        launcherModeEnabled = launcherModeEnabled,
                        autoLaunchOnPower = autoLaunchOnPower,
                        autoExitOnDisconnect = autoExitOnDisconnect,
                        autoLaunchOnBoot = autoLaunchOnBoot,
                        onThemeModeChanged = viewModel::setThemeMode,
                        onLauncherModeChanged = viewModel::setLauncherMode,
                        onAutoLaunchOnPowerChanged = viewModel::setAutoLaunchOnPower,
                        onAutoExitOnDisconnectChanged = viewModel::setAutoExitOnDisconnect,
                        onAutoLaunchOnBootChanged = viewModel::setAutoLaunchOnBoot,
                        onReturnToLauncher = {
                            // Launch the default home launcher
                            val intent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        onExitApp = {
                            (context as? Activity)?.finishAndRemoveTask()
                        }
                    )
                }
            }
        }

        // Bottom Navigation Bar
        DriveBottomNavBar(
            selectedItem = selectedTab,
            onItemSelected = { selectedTab = it }
        )
    }
}
