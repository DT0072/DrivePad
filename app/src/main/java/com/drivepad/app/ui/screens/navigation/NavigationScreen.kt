package com.drivepad.app.ui.screens.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.webkit.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.drivepad.app.ui.theme.*
import org.json.JSONObject

@Composable
fun NavigationScreen(
    location: NavigationMapLocation,
    isSearching: Boolean,
    searchError: String?,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val compact = maxWidth < 980.dp
        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(if (compact) 0.62f else 0.7f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            ) {
                InAppNavigationView(location = location, modifier = Modifier.fillMaxSize())
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface.copy(alpha = 0.96f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkDivider),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.TurnRight, null, tint = ElectricBlue, modifier = Modifier.size(34.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("250 m", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(
                                    location.label,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                )
                            }
                        }
                        if (!compact) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = DarkDivider)
                            Spacer(Modifier.height(10.dp))
                            Text("11:03 ETA   ·   21 min   ·   14 km", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(if (compact) 0.38f else 0.3f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(if (compact) 12.dp else 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Navigation, null, tint = ElectricBlue)
                    Spacer(Modifier.width(8.dp))
                    Text("Navigation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
                Text("Search stays inside DrivePad", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Where to?") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    trailingIcon = {
                        IconButton(onClick = { query.trim().takeIf { it.isNotEmpty() }?.let(onSearch) }) {
                            if (isSearching) {
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                )
                Button(
                    onClick = { query.trim().takeIf { it.isNotEmpty() }?.let(onSearch) },
                    enabled = query.isNotBlank() && !isSearching,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CockpitRed),
                ) {
                    Icon(Icons.Filled.Navigation, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Search Map")
                }

                searchError?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }

                Text("Quick destinations", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                DestinationButton("Home", "Kuala Lumpur", Icons.Filled.Home, ElectricBlue) { onSearch("Kuala Lumpur") }
                DestinationButton("Work", "Petronas Twin Towers", Icons.Filled.Work, EmeraldGreen) { onSearch("Petronas Twin Towers") }
                DestinationButton("Fuel", "Nearby petrol stations", Icons.Filled.LocalGasStation, AmberAccent) { onSearch("Petrol station Kuala Lumpur") }

                Spacer(Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(8.dp), color = DarkSurfaceVariant) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Map, null, tint = ElectricBlue)
                        Spacer(Modifier.width(8.dp))
                        Text("OpenStreetMap live view", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationButton(label: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(58.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkDivider),
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, label, tint = color)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppNavigationView(
    location: NavigationMapLocation,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val locationScript = remember(location) {
        "window.drivePadShowLocation(" +
            "${location.latitude},${location.longitude},${JSONObject.quote(location.label)}" +
            ");"
    }
    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.setGeolocationEnabled(true)
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.allowFileAccess = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                setBackgroundColor(AndroidColor.BLACK)
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        (view.tag as? String)?.let { script ->
                            view.evaluateJavascript(script, null)
                        }
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val uri = request.url
                        if (uri.scheme == "http" || uri.scheme == "https" || uri.scheme == "file") {
                            return false
                        }
                        return try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            true
                        } catch (_: Exception) {
                            true
                        }
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        callback?.invoke(origin, granted, false)
                    }
                }
                tag = locationScript
                loadUrl("file:///android_asset/drivepad_map.html")
            }
        },
        update = { view ->
            if (view.tag != locationScript) {
                view.tag = locationScript
                view.evaluateJavascript(locationScript, null)
            }
        },
    )
}
