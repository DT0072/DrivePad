package com.drivepad.app.ui.screens.connectivity

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*

// ============================================================
// Connectivity Hub Screen
// ============================================================

@Composable
fun ConnectivityScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
    ) {
        // Bluetooth section
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            ConnectivitySection(
                title = "Bluetooth",
                icon = Icons.Filled.Bluetooth,
                color = ElectricBlue,
                statusText = "Connected",
                isConnected = true,
                onOpenSettings = {
                    context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                },
                modifier = Modifier.weight(1f)
            ) {
                // Paired devices list
                ConnectivityDevice("Car Audio System", "Connected", EmeraldGreen, Icons.Filled.Speaker)
                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                ConnectivityDevice("Phone (Galaxy S24)", "Paired", ElectricBlue, Icons.Filled.PhoneAndroid)
                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                ConnectivityDevice("AirPods Pro", "Available", MaterialTheme.colorScheme.onSurfaceVariant, Icons.Filled.Headphones)
            }
        }

        // WiFi section
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            ConnectivitySection(
                title = "WiFi",
                icon = Icons.Filled.Wifi,
                color = EmeraldGreen,
                statusText = "Connected",
                isConnected = true,
                onOpenSettings = {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                },
                modifier = Modifier.weight(1f)
            ) {
                ConnectivityDevice("MyHomeWiFi", "Connected · 5GHz", EmeraldGreen, Icons.Filled.SignalWifi4Bar)
                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                ConnectivityDevice("CarHotspot", "Saved", MaterialTheme.colorScheme.onSurfaceVariant, Icons.Filled.Wifi)
            }

            // Hotspot
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    } catch (e: Exception) { }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VioletAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WifiTethering,
                            contentDescription = "Hotspot",
                            tint = VioletAccent,
                            modifier = Modifier.size(DriveDimens.iconMedium)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mobile Hotspot",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tap to configure",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(DriveDimens.iconSmall)
                    )
                }
            }
        }

        // Audio output + USB section
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
                ) {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(DriveDimens.iconMedium)
                    )
                    Text(
                        text = "Audio Output",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

                AudioOutputItem("Tablet Speakers", true, Icons.Filled.TabletAndroid)
                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                AudioOutputItem("Bluetooth Audio", false, Icons.Filled.BluetoothAudio)
                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                AudioOutputItem("USB Audio", false, Icons.Filled.Usb)
                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                AudioOutputItem("AUX Output", false, Icons.Filled.Cable)
            }

            // Voice Assistant
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VOICE_COMMAND)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            val intent = Intent(Intent.ACTION_ASSIST)
                            context.startActivity(intent)
                        } catch (e2: Exception) { }
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Voice",
                            tint = ElectricBlue,
                            modifier = Modifier.size(DriveDimens.iconMedium)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Voice Assistant",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tap to activate voice commands",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectivitySection(
    title: String,
    icon: ImageVector,
    color: Color,
    statusText: String,
    isConnected: Boolean,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(DriveDimens.iconMedium))
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (isConnected) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant))
                Text(text = statusText, style = MaterialTheme.typography.bodySmall, color = if (isConnected) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

        content()

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open System Settings")
        }
    }
}

@Composable
private fun ConnectivityDevice(
    name: String,
    status: String,
    statusColor: Color,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f))
            .padding(DriveDimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(DriveDimens.iconMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = status, style = MaterialTheme.typography.bodySmall, color = statusColor)
        }
    }
}

@Composable
private fun AudioOutputItem(
    name: String,
    isActive: Boolean,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActive) AmberAccent.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.3f)
            )
            .padding(DriveDimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive) AmberAccent else MaterialTheme.colorScheme.onSurface
        )
        if (isActive) {
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AmberAccent))
        }
    }
}
