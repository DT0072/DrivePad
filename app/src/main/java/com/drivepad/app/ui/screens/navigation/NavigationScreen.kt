package com.drivepad.app.ui.screens.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.webkit.GeolocationPermissions
import android.webkit.WebSettings
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*

// ============================================================
// Navigation Hub Screen
// ============================================================

@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var destination by rememberSaveable { mutableStateOf<String?>(null) }
    var destinationInput by rememberSaveable { mutableStateOf("") }
    fun searchDestination(value: String) {
        if (value.isNotBlank()) {
            destination = value.trim()
        }
    }
    fun setQuickDestination(value: String) {
        destinationInput = value
        destination = value
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
    ) {
        // Left: Embedded navigation controls
        Column(
            modifier = Modifier
                .weight(0.32f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm),
                ) {
                    Icon(Icons.Filled.Map, null, tint = ElectricBlue)
                    Column {
                        Text(
                            "Google Maps",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            "Embedded in DrivePad",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldGreen,
                        )
                    }
                }
            }

            OutlinedTextField(
                value = destinationInput,
                onValueChange = { destinationInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Where to?") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    IconButton(
                        onClick = { searchDestination(destinationInput) },
                    ) {
                        Icon(Icons.Filled.Directions, "Start route")
                    }
                },
                singleLine = true,
            )

            Button(
                onClick = { searchDestination(destinationInput) },
                modifier = Modifier.fillMaxWidth(),
                enabled = destinationInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search in Maps")
            }

            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
            ) {
                QuickNavButton(
                    icon = Icons.Filled.Home,
                    label = "Home",
                    color = ElectricBlue,
                    onClick = { setQuickDestination("Home") },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.Work,
                    label = "Work",
                    color = EmeraldGreen,
                    onClick = { setQuickDestination("Work") },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.LocalGasStation,
                    label = "Fuel",
                    color = AmberAccent,
                    onClick = { setQuickDestination("Fuel near me") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Google Maps is loaded in embedded map mode for searching. Turn-by-turn navigation and Waze still open best in their native apps when needed.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Right: live embedded map
        Column(
            modifier = Modifier
                .weight(0.68f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
        ) {
            // In-app map. Explicit app buttons on the left still open native map apps.
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                padding = 0.dp,
            ) {
                InAppNavigationView(
                    destination = destination,
                    modifier = Modifier.fillMaxSize(),
                )
            }

        }
    }
}

@Composable
private fun QuickNavButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        padding = DriveDimens.spacingMd
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(DriveDimens.iconMedium)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun InAppNavigationView(
    destination: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val targetUrl = remember(destination) { buildNavigationUrl(destination) }

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.setGeolocationEnabled(true)
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        val uri = request.url
                        if (uri.scheme == "http" || uri.scheme == "https") {
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
                    override fun onGeolocationPermissionsShowPrompt(
                        origin: String?,
                        callback: GeolocationPermissions.Callback?,
                    ) {
                        val permissionGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                        ) == PackageManager.PERMISSION_GRANTED
                        callback?.invoke(origin, permissionGranted, false)
                    }
                }
                tag = targetUrl
                loadUrl(targetUrl)
            }
        },
        update = { webView ->
            if (webView.tag != targetUrl) {
                webView.tag = targetUrl
                webView.loadUrl(targetUrl)
            }
        },
    )
}
