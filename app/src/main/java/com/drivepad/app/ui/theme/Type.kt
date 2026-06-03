package com.drivepad.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default sans-serif as fallback; Inter would be ideal with bundled font files
val DriveFont = FontFamily.SansSerif

// ============================================================
// DrivePad Typography - Automotive-optimized for readability
// ============================================================

val DriveTypography = Typography(
    // Dashboard clock - enormous for at-a-glance reading
    displayLarge = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-1.5).sp
    ),
    // Large headers
    displayMedium = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    ),
    // Section titles on dashboard
    displaySmall = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    // Screen titles
    headlineLarge = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    // Card titles
    headlineMedium = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Sub-section titles
    headlineSmall = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Large labels (nav bar, prominent controls)
    titleLarge = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Card subtitles, media info
    titleMedium = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    // Smaller labels
    titleSmall = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // Primary body text
    bodyLarge = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Secondary body text
    bodyMedium = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // Captions, timestamps
    bodySmall = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    // Button text - larger for driving safety
    labelLarge = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    // Secondary button text
    labelMedium = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    // Tiny labels (status indicators)
    labelSmall = TextStyle(
        fontFamily = DriveFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
