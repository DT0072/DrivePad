package com.drivepad.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ============================================================
// Navigation Keys for DrivePad screens
// ============================================================

@Serializable data object HomeKey : NavKey
@Serializable data object NavigationKey : NavKey
@Serializable data object MediaKey : NavKey
@Serializable data object ProjectionKey : NavKey
@Serializable data object ConnectivityKey : NavKey
@Serializable data object SettingsKey : NavKey

@Serializable
data class NavigationSearchResult(
    val latitude: Double,
    val longitude: Double,
    val label: String,
    val subtitle: String = "",
)

// Bottom nav items
enum class BottomNavItem(
    val key: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        key = HomeKey,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    NAVIGATION(
        key = NavigationKey,
        label = "Navigate",
        selectedIcon = Icons.Filled.Navigation,
        unselectedIcon = Icons.Outlined.Navigation
    ),
    MEDIA(
        key = MediaKey,
        label = "Media",
        selectedIcon = Icons.Filled.MusicNote,
        unselectedIcon = Icons.Outlined.MusicNote
    ),
    PROJECTION(
        key = ProjectionKey,
        label = "Phone",
        selectedIcon = Icons.Filled.PhoneAndroid,
        unselectedIcon = Icons.Outlined.PhoneAndroid
    ),
    SETTINGS(
        key = SettingsKey,
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    );
}
