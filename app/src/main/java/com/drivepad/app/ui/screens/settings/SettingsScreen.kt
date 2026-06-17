package com.drivepad.app.ui.screens.settings

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepad.app.data.preferences.ThemeMode
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*

// ============================================================
// Settings Screen
// ============================================================

@Composable
fun SettingsScreen(
    currentThemeMode: ThemeMode,
    launcherModeEnabled: Boolean,
    autoLaunchOnPower: Boolean,
    autoExitOnDisconnect: Boolean,
    autoLaunchOnBoot: Boolean,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onLauncherModeChanged: (Boolean) -> Unit,
    onAutoLaunchOnPowerChanged: (Boolean) -> Unit,
    onAutoExitOnDisconnectChanged: (Boolean) -> Unit,
    onAutoLaunchOnBootChanged: (Boolean) -> Unit,
    onReturnToLauncher: () -> Unit,
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
    ) {
        // Left: Appearance + Theme
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            SectionHeader("Appearance", Icons.Filled.Palette, VioletAccent)

            // Theme Mode
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Theme Mode",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

                ThemeMode.entries.forEach { mode ->
                    val isSelected = mode == currentThemeMode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else Color.Transparent
                            )
                            .padding(DriveDimens.spacingSm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onThemeModeChanged(mode) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Column {
                            Text(
                                text = when (mode) {
                                    ThemeMode.AUTO -> "Automatic"
                                    ThemeMode.DARK -> "Dark Mode (Night)"
                                    ThemeMode.LIGHT -> "Light Mode (Day)"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (mode) {
                                    ThemeMode.AUTO -> "Follow system theme"
                                    ThemeMode.DARK -> "Optimized for night driving"
                                    ThemeMode.LIGHT -> "Optimized for day driving"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Center: Launcher + Automation
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            SectionHeader("Launcher", Icons.Filled.Dashboard, ElectricBlue)

            SettingsToggle(
                title = "Launcher Mode",
                description = "Allow DrivePad to appear as a home screen option",
                icon = Icons.Filled.Home,
                iconColor = ElectricBlue,
                isEnabled = launcherModeEnabled,
                onToggle = onLauncherModeChanged
            )

            SectionHeader("Automation", Icons.Filled.AutoMode, EmeraldGreen)

            SettingsToggle(
                title = "Auto-launch on Power",
                description = "Start DrivePad when vehicle power is connected",
                icon = Icons.Filled.Power,
                iconColor = EmeraldGreen,
                isEnabled = autoLaunchOnPower,
                onToggle = onAutoLaunchOnPowerChanged
            )

            SettingsToggle(
                title = "Auto-exit on Disconnect",
                description = "Minimize when vehicle power is disconnected",
                icon = Icons.Filled.PowerOff,
                iconColor = AmberAccent,
                isEnabled = autoExitOnDisconnect,
                onToggle = onAutoExitOnDisconnectChanged
            )

            SettingsToggle(
                title = "Auto-launch on Boot",
                description = "Start DrivePad when tablet powers on",
                icon = Icons.Filled.PowerSettingsNew,
                iconColor = VioletAccent,
                isEnabled = autoLaunchOnBoot,
                onToggle = onAutoLaunchOnBootChanged
            )
        }

        // Right: About + Exit
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            SectionHeader("About", Icons.Filled.Info, MaterialTheme.colorScheme.onSurfaceVariant)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsCar,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                    Text(
                        text = "DrivePad",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "v1.2.1",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                    Text(
                        text = "Single Canvas Infotainment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Responsive 10.95-12.5 inch tablets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Quick actions
            Button(
                onClick = onReturnToLauncher,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DriveDimens.largeTouchTarget),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Return to Huawei Launcher", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onExitApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DriveDimens.largeTouchTarget),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed)
            ) {
                Icon(Icons.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exit DrivePad", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(DriveDimens.iconSmall)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onToggle(!isEnabled) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = iconColor,
                    checkedThumbColor = Color.White
                )
            )
        }
    }
}
