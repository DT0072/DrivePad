package com.drivepad.app.ui.components

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepad.app.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Automotive-style status bar showing time, date, battery, connectivity, and weather.
 * Sits at the top of every screen.
 */
@Composable
fun DriveStatusBar(
    weatherIcon: String = "☀️",
    weatherTemp: String = "--°C",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Live clock
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Calendar.getInstance()
            currentTime = SimpleDateFormat("h:mm", Locale.getDefault()).format(now.time)
            currentDate = SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(now.time)
            delay(1000)
        }
    }

    // Battery level
    val batteryLevel = remember { getBatteryLevel(context) }

    // Connectivity
    val isWifiConnected = remember { isWifiConnected(context) }
    val isBluetoothOn = remember { isBluetoothEnabled(context) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(DriveDimens.statusBarHeight)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .padding(horizontal = DriveDimens.spacingLg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: App branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsCar,
                contentDescription = "DrivePad",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DriveDimens.iconMedium)
            )
            Text(
                text = "DrivePad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Center: Status indicators
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
        ) {
            // Battery
            StatusIndicator(
                icon = when {
                    batteryLevel > 80 -> Icons.Filled.BatteryFull
                    batteryLevel > 50 -> Icons.Filled.Battery5Bar
                    batteryLevel > 20 -> Icons.Filled.Battery3Bar
                    else -> Icons.Filled.Battery1Bar
                },
                label = "${batteryLevel}%",
                tint = if (batteryLevel <= 20) CoralRed else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // WiFi
            StatusIndicator(
                icon = if (isWifiConnected) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                label = if (isWifiConnected) "WiFi" else "Off",
                tint = if (isWifiConnected) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Bluetooth
            StatusIndicator(
                icon = if (isBluetoothOn) Icons.Filled.Bluetooth else Icons.Filled.BluetoothDisabled,
                label = if (isBluetoothOn) "BT" else "Off",
                tint = if (isBluetoothOn) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Weather
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = weatherIcon, fontSize = 18.sp)
                Text(
                    text = weatherTemp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Right: Time & Date
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 28.sp
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(DriveDimens.iconSmall)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}

private fun getBatteryLevel(context: Context): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
        context.registerReceiver(null, filter)
    }
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    return if (level >= 0 && scale > 0) (level * 100 / scale) else 100
}

private fun isWifiConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val network = cm?.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}

private fun isBluetoothEnabled(context: Context): Boolean {
    return try {
        val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        btManager?.adapter?.isEnabled == true
    } catch (e: SecurityException) {
        false
    }
}
