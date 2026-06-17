package com.drivepad.app.ui.components

import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drivepad.app.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DriveStatusBar(
    weatherIcon: String = "",
    weatherTemp: String = "--°C",
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1_000)
        }
    }
    val wifi = remember { isWifiConnected(context) }
    val bluetooth = remember { isBluetoothEnabled(context) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(DriveDimens.statusBarHeight)
            .background(MaterialTheme.colorScheme.background),
    ) {
        val compact = maxWidth < 980.dp
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 14.dp else 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(18.dp))
            Icon(Icons.Filled.WbCloudy, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(6.dp))
            Text(weatherTemp, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)

            Spacer(Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 16.dp),
            ) {
                Icon(
                    imageVector = if (wifi) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                    contentDescription = "Wi-Fi",
                    tint = if (wifi) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
                Icon(
                    imageVector = if (bluetooth) Icons.Filled.Bluetooth else Icons.Filled.BluetoothDisabled,
                    contentDescription = "Bluetooth",
                    tint = if (bluetooth) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
                Icon(Icons.Filled.SignalCellularAlt, "Mobile signal", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp))
                if (!compact) Text("4G", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Filled.AccountCircle, "Profile", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(28.dp))
                if (!compact) Text("Driver", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

private fun isWifiConnected(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val network = manager?.activeNetwork ?: return false
    return manager.getNetworkCapabilities(network)
        ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
}

private fun isBluetoothEnabled(context: Context): Boolean {
    return try {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter?.isEnabled == true
    } catch (_: SecurityException) {
        false
    }
}
