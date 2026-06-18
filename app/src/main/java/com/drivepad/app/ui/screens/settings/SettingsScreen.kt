package com.drivepad.app.ui.screens.settings

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.drivepad.app.BuildConfig
import com.drivepad.app.data.preferences.ThemeMode
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*

@Composable
fun SettingsScreen(
    currentThemeMode: ThemeMode,
    launcherModeEnabled: Boolean,
    autoLaunchOnPower: Boolean,
    autoExitOnDisconnect: Boolean,
    autoLaunchOnBoot: Boolean,
    driverName: String,
    isDriverSignedIn: Boolean,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onLauncherModeChanged: (Boolean) -> Unit,
    onAutoLaunchOnPowerChanged: (Boolean) -> Unit,
    onAutoExitOnDisconnectChanged: (Boolean) -> Unit,
    onAutoLaunchOnBootChanged: (Boolean) -> Unit,
    onDriverNameChanged: (String) -> Unit,
    onDriverSignedInChanged: (Boolean) -> Unit,
    onReturnToLauncher: () -> Unit,
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var loginPin by rememberSaveable { mutableStateOf("") }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(DriveDimens.spacingLg),
    ) {
        val narrow = maxWidth < 980.dp

        if (narrow) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)) {
                DriverProfileCard(
                    driverName = driverName,
                    isSignedIn = isDriverSignedIn,
                    loginPin = loginPin,
                    onLoginPinChanged = { loginPin = it },
                    onDriverNameChanged = onDriverNameChanged,
                    onDriverSignedInChanged = onDriverSignedInChanged,
                )
                SystemControlsCard(
                    currentThemeMode = currentThemeMode,
                    launcherModeEnabled = launcherModeEnabled,
                    autoLaunchOnPower = autoLaunchOnPower,
                    autoExitOnDisconnect = autoExitOnDisconnect,
                    autoLaunchOnBoot = autoLaunchOnBoot,
                    onThemeModeChanged = onThemeModeChanged,
                    onLauncherModeChanged = onLauncherModeChanged,
                    onAutoLaunchOnPowerChanged = onAutoLaunchOnPowerChanged,
                    onAutoExitOnDisconnectChanged = onAutoExitOnDisconnectChanged,
                    onAutoLaunchOnBootChanged = onAutoLaunchOnBootChanged,
                )
                AboutCard(
                    driverName = driverName,
                    onReturnToLauncher = onReturnToLauncher,
                    onExitApp = onExitApp,
                )
            }
        } else {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)) {
                Column(
                    modifier = Modifier.weight(1.1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd),
                ) {
                    DriverProfileCard(
                        driverName = driverName,
                        isSignedIn = isDriverSignedIn,
                        loginPin = loginPin,
                        onLoginPinChanged = { loginPin = it },
                        onDriverNameChanged = onDriverNameChanged,
                        onDriverSignedInChanged = onDriverSignedInChanged,
                    )
                    SystemControlsCard(
                        currentThemeMode = currentThemeMode,
                        launcherModeEnabled = launcherModeEnabled,
                        autoLaunchOnPower = autoLaunchOnPower,
                        autoExitOnDisconnect = autoExitOnDisconnect,
                        autoLaunchOnBoot = autoLaunchOnBoot,
                        onThemeModeChanged = onThemeModeChanged,
                        onLauncherModeChanged = onLauncherModeChanged,
                        onAutoLaunchOnPowerChanged = onAutoLaunchOnPowerChanged,
                        onAutoExitOnDisconnectChanged = onAutoExitOnDisconnectChanged,
                        onAutoLaunchOnBootChanged = onAutoLaunchOnBootChanged,
                    )
                }

                Column(
                    modifier = Modifier.weight(0.9f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd),
                ) {
                    AboutCard(
                        driverName = driverName,
                        onReturnToLauncher = onReturnToLauncher,
                        onExitApp = onExitApp,
                    )
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkSurface.copy(alpha = 0.94f),
                        borderColor = DarkDivider,
                    ) {
                        Text("DrivePad", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Single-canvas infotainment with a Proton-inspired cockpit profile and responsive tablet layout.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverProfileCard(
    driverName: String,
    isSignedIn: Boolean,
    loginPin: String,
    onLoginPinChanged: (String) -> Unit,
    onDriverNameChanged: (String) -> Unit,
    onDriverSignedInChanged: (Boolean) -> Unit,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = DarkSurface.copy(alpha = 0.94f),
        borderColor = DarkDivider,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(ElectricBlue.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Person, null, tint = ElectricBlue, modifier = Modifier.size(44.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Driver profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    AssistChip(
                        onClick = { },
                        label = { Text(if (isSignedIn) "Signed in" else "Guest") },
                        leadingIcon = { Icon(Icons.Filled.Badge, null, modifier = Modifier.size(16.dp)) },
                    )
                }
                Text(
                    "A clean login surface for the active driver, styled like a modern Proton eMas / X50 profile panel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = driverName,
                        onValueChange = onDriverNameChanged,
                        modifier = Modifier.weight(1f),
                        label = { Text("Driver name") },
                        leadingIcon = { Icon(Icons.Filled.Badge, null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                    )
                    OutlinedTextField(
                        value = loginPin,
                        onValueChange = onLoginPinChanged,
                        modifier = Modifier.weight(0.7f),
                        label = { Text("PIN") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onDriverSignedInChanged(true) },
                        enabled = driverName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(Icons.Filled.Login, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sign in")
                    }
                    OutlinedButton(
                        onClick = { onDriverSignedInChanged(false) },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(Icons.Filled.Logout, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sign out")
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemControlsCard(
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
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = DarkSurface.copy(alpha = 0.94f),
        borderColor = DarkDivider,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)) {
            SectionHeader("System behavior", Icons.Filled.Settings, AmberAccent)
            ThemeRow(currentThemeMode = currentThemeMode, onThemeModeChanged = onThemeModeChanged)
            SettingsToggle("Launcher mode", "Allow DrivePad to appear as a home screen option", Icons.Filled.Home, ElectricBlue, launcherModeEnabled, onLauncherModeChanged)
            SettingsToggle("Auto-launch on power", "Start DrivePad when vehicle power is connected", Icons.Filled.Power, EmeraldGreen, autoLaunchOnPower, onAutoLaunchOnPowerChanged)
            SettingsToggle("Auto-exit on disconnect", "Minimize when vehicle power is disconnected", Icons.Filled.PowerOff, AmberAccent, autoExitOnDisconnect, onAutoExitOnDisconnectChanged)
            SettingsToggle("Auto-launch on boot", "Start DrivePad when tablet powers on", Icons.Filled.PowerSettingsNew, VioletAccent, autoLaunchOnBoot, onAutoLaunchOnBootChanged)
        }
    }
}

@Composable
private fun ThemeRow(
    currentThemeMode: ThemeMode,
    onThemeModeChanged: (ThemeMode) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader("Appearance", Icons.Filled.Palette, VioletAccent)
        ThemeMode.entries.forEach { mode ->
            val selected = mode == currentThemeMode
            Surface(
                onClick = { onThemeModeChanged(mode) },
                shape = RoundedCornerShape(14.dp),
                color = if (selected) ElectricBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, if (selected) ElectricBlue.copy(alpha = 0.4f) else DarkDivider),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = { onThemeModeChanged(mode) },
                        colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (mode) {
                                ThemeMode.AUTO -> "Automatic"
                                ThemeMode.DARK -> "Dark Mode"
                                ThemeMode.LIGHT -> "Light Mode"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = when (mode) {
                                ThemeMode.AUTO -> "Follow system theme"
                                ThemeMode.DARK -> "Optimized for night driving"
                                ThemeMode.LIGHT -> "Optimized for day driving"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutCard(
    driverName: String,
    onReturnToLauncher: () -> Unit,
    onExitApp: () -> Unit,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = DarkSurface.copy(alpha = 0.94f),
        borderColor = DarkDivider,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)) {
            SectionHeader("About", Icons.Filled.Info, MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CockpitRed.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.DirectionsCar, null, tint = CockpitRed, modifier = Modifier.size(30.dp))
                }
                Column {
                    Text("DrivePad", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Signed in as ${if (driverName.isBlank()) "Driver" else driverName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Button(
                onClick = onReturnToLauncher,
                modifier = Modifier.fillMaxWidth().height(DriveDimens.largeTouchTarget),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Return to launcher", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onExitApp,
                modifier = Modifier.fillMaxWidth().height(DriveDimens.largeTouchTarget),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed),
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
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm),
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(DriveDimens.iconSmall))
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Surface(
        onClick = { onToggle(!isEnabled) },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, DarkDivider),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd),
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedTrackColor = iconColor, checkedThumbColor = Color.White),
            )
        }
    }
}
