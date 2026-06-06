package com.drivepad.app.ui.screens.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
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

data class NavAppInfo(
    val name: String,
    val packageName: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var destination by rememberSaveable { mutableStateOf<String?>(null) }
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

    // Available navigation apps
    val navApps = remember {
        listOf(
            NavAppInfo("Google Maps", "com.google.android.apps.maps", Icons.Filled.Map, ElectricBlue),
            NavAppInfo("Waze", "com.waze", Icons.Filled.Traffic, EmeraldGreen),
            NavAppInfo("Petal Maps", "com.huawei.maps.app", Icons.Filled.Explore, AmberAccent),
        )
    }

    // Check which apps are installed
    val installedApps = remember {
        navApps.filter { app ->
            try {
                context.packageManager.getPackageInfo(app.packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
    ) {
        // Left: Nav app selector + Quick actions
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
        ) {
            // Navigation apps
            Text(
                text = "Navigation Apps",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            navApps.forEach { app ->
                val isInstalled = installedApps.contains(app)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (isInstalled) {
                            launchNavApp(context, app.packageName)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
                    ) {
                        Icon(
                            imageVector = app.icon,
                            contentDescription = app.name,
                            tint = if (isInstalled) app.color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(DriveDimens.iconLarge)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isInstalled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = if (isInstalled) "Open externally" else "Not installed",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isInstalled) app.color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                        if (isInstalled) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Launch,
                                contentDescription = "Open",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(DriveDimens.iconSmall)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Quick actions
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
                    onClick = { destination = "Home" },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.Work,
                    label = "Work",
                    color = EmeraldGreen,
                    onClick = { destination = "Work" },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.LocalGasStation,
                    label = "Fuel",
                    color = AmberAccent,
                    onClick = { destination = "Gas station near me" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Right: Map placeholder & favorites
        Column(
            modifier = Modifier
                .weight(0.6f)
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

            // Favorite destinations placeholder
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Favorite Destinations",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(DriveDimens.iconSmall)
                    )
                }
                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                Text(
                    text = "Save your frequent destinations for quick access",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

private fun launchNavApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        // App not found
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
