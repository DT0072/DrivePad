package com.drivepad.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val DriveShapes = Shapes(
    // Small buttons, chips
    extraSmall = RoundedCornerShape(8.dp),
    // Standard cards, input fields
    small = RoundedCornerShape(8.dp),
    // Dashboard cards, panels
    medium = RoundedCornerShape(10.dp),
    // Large modal cards, dialogs
    large = RoundedCornerShape(12.dp),
    // Full-width bottom sheets, navigation bar
    extraLarge = RoundedCornerShape(14.dp)
)

// Automotive-specific dimensions
object DriveDimens {
    // Touch targets (minimum 48dp for driving safety)
    val minTouchTarget = 48.dp
    val largeTouchTarget = 56.dp
    val extraLargeTouchTarget = 64.dp

    // Spacing
    val spacingXs = 4.dp
    val spacingSm = 8.dp
    val spacingMd = 12.dp
    val spacingLg = 16.dp
    val spacingXl = 24.dp
    val spacingXxl = 32.dp
    val spacingHuge = 48.dp

    // Card dimensions
    val cardElevation = 0.dp  // We use border/background instead of elevation for automotive look
    val cardPadding = 16.dp
    val cardBorderWidth = 1.dp

    // Bottom navigation
    val bottomNavHeight = 76.dp
    val bottomNavIconSize = 26.dp

    // Status bar
    val statusBarHeight = 50.dp

    // Icon sizes
    val iconSmall = 20.dp
    val iconMedium = 24.dp
    val iconLarge = 32.dp
    val iconXLarge = 48.dp

    // Album art
    val albumArtSmall = 56.dp
    val albumArtMedium = 120.dp
    val albumArtLarge = 200.dp

    // Quick access grid
    val quickAccessItemSize = 80.dp
}
