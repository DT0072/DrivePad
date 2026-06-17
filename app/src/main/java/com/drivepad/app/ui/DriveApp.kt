package com.drivepad.app.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepad.app.navigation.BottomNavItem
import com.drivepad.app.ui.components.DriveBottomNavBar
import com.drivepad.app.ui.components.DriveStatusBar
import com.drivepad.app.ui.screens.connectivity.ConnectivityScreen
import com.drivepad.app.ui.screens.home.HomeScreen
import com.drivepad.app.ui.screens.media.AudioScreen
import com.drivepad.app.ui.screens.navigation.NavigationScreen
import com.drivepad.app.ui.screens.projection.ProjectionScreen
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
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.refreshWeather()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
            )
        }
    }

    // Current tab
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }

    // Collect states
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val navigationLocation by viewModel.navigationLocation.collectAsStateWithLifecycle()
    val isNavigationSearching by viewModel.isNavigationSearching.collectAsStateWithLifecycle()
    val navigationSearchError by viewModel.navigationSearchError.collectAsStateWithLifecycle()
    val nowPlayingTitle by viewModel.nowPlayingTitle.collectAsStateWithLifecycle()
    val nowPlayingArtist by viewModel.nowPlayingArtist.collectAsStateWithLifecycle()
    val nowPlayingAlbum by viewModel.nowPlayingAlbum.collectAsStateWithLifecycle()
    val nowPlayingAlbumArt by viewModel.nowPlayingAlbumArt.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val playbackProgress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val totalDuration by viewModel.totalDuration.collectAsStateWithLifecycle()
    val activeMediaSource by viewModel.activeMediaSource.collectAsStateWithLifecycle()
    val activeMediaPackage by viewModel.activeMediaPackage.collectAsStateWithLifecycle()
    val hasMediaControlAccess by viewModel.hasMediaControlAccess.collectAsStateWithLifecycle()
    val mediaVolume by viewModel.mediaVolume.collectAsStateWithLifecycle()
    val mediaQueue by viewModel.mediaQueue.collectAsStateWithLifecycle()

    val radioStations by viewModel.radioStations.collectAsStateWithLifecycle()
    val currentStation by viewModel.currentStation.collectAsStateWithLifecycle()
    val currentFrequency by viewModel.currentFrequency.collectAsStateWithLifecycle()
    val isRadioPlaying by viewModel.isRadioPlaying.collectAsStateWithLifecycle()
    val radioPlaybackError by viewModel.radioPlaybackError.collectAsStateWithLifecycle()
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
                        weatherHumidity = weather?.let { "${it.humidity}%" } ?: "--",
                        weatherWind = weather?.let { "${it.windSpeed.toInt()} km/h" } ?: "--",
                        weatherUpdated = weather?.observedAt
                            ?.substringAfter("T", "")
                            ?.takeIf { it.isNotBlank() }
                            ?.let { "Live · $it" }
                            ?: "Updating live weather",
                        navigationLocation = navigationLocation,
                        isNavigationSearching = isNavigationSearching,
                        navigationSearchError = navigationSearchError,
                        nowPlayingTitle = nowPlayingTitle,
                        nowPlayingArtist = nowPlayingArtist,
                        nowPlayingAlbumArt = nowPlayingAlbumArt,
                        isPlaying = isPlaying,
                        currentStation = currentStation,
                        currentFrequency = currentFrequency,
                        isRadioPlaying = isRadioPlaying,
                        radioPlaybackError = radioPlaybackError,
                        radioPresets = radioPresets,
                        onPlayPause = viewModel::togglePlayPause,
                        onSkipNext = viewModel::skipNext,
                        onSkipPrevious = viewModel::skipPrevious,
                        onRadioPlayPause = viewModel::toggleRadioPlayPause,
                        onRadioSeekNext = viewModel::seekRadioForward,
                        onRadioSeekPrevious = viewModel::seekRadioBackward,
                        onRadioFrequencyChange = viewModel::setRadioFrequency,
                        onPresetLoad = viewModel::loadPreset,
                        onNavigationSearch = viewModel::searchNavigationDestination,
                        onNavigateToScreen = { screenId ->
                            when (screenId) {
                                "navigation" -> selectedTab = BottomNavItem.NAVIGATION
                                "media" -> selectedTab = BottomNavItem.MEDIA
                                "radio" -> selectedTab = BottomNavItem.MEDIA
                                "projection" -> selectedTab = BottomNavItem.PROJECTION
                                "connectivity" -> selectedTab = BottomNavItem.SETTINGS
                                "settings" -> selectedTab = BottomNavItem.SETTINGS
                            }
                        }
                    )

                    BottomNavItem.NAVIGATION -> NavigationScreen(
                        location = navigationLocation,
                        isSearching = isNavigationSearching,
                        searchError = navigationSearchError,
                        onSearch = viewModel::searchNavigationDestination,
                    )

                    BottomNavItem.MEDIA -> AudioScreen(
                        nowPlayingTitle = nowPlayingTitle,
                        nowPlayingArtist = nowPlayingArtist,
                        nowPlayingAlbum = nowPlayingAlbum,
                        nowPlayingAlbumArt = nowPlayingAlbumArt,
                        isPlaying = isPlaying,
                        playbackProgress = playbackProgress,
                        currentPosition = currentPosition,
                        totalDuration = totalDuration,
                        onPlayPause = viewModel::togglePlayPause,
                        onSkipNext = viewModel::skipNext,
                        onSkipPrevious = viewModel::skipPrevious,
                        onSeek = viewModel::seekTo,
                        activeSource = activeMediaSource,
                        activeMediaPackage = activeMediaPackage,
                        onSourceSelected = viewModel::setActiveMediaSource,
                        hasMediaControlAccess = hasMediaControlAccess,
                        onRequestMediaControlAccess = {
                            context.startActivity(
                                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
                            )
                        },
                        mediaQueue = mediaQueue,
                        onQueueItemSelected = viewModel::skipToQueueItem,
                        volume = mediaVolume,
                        onVolumeChange = viewModel::setMediaVolume,
                        currentStation = currentStation,
                        currentFrequency = currentFrequency,
                        isRadioPlaying = isRadioPlaying,
                        radioPlaybackError = radioPlaybackError,
                        stations = radioStations,
                        presets = radioPresets,
                        onFrequencyChange = viewModel::setRadioFrequency,
                        onRadioPlayPause = viewModel::toggleRadioPlayPause,
                        onRadioSeekForward = viewModel::seekRadioForward,
                        onRadioSeekBackward = viewModel::seekRadioBackward,
                        onStationSelect = viewModel::selectRadioStation,
                        onPresetSave = viewModel::savePreset,
                        onPresetLoad = viewModel::loadPreset,
                        onRadioSearch = { /* open search dialog */ },
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
            onItemSelected = { selectedTab = it },
            volume = mediaVolume,
            onVolumeChange = viewModel::setMediaVolume,
        )
    }
}
