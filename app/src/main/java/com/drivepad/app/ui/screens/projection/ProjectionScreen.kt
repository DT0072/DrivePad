package com.drivepad.app.ui.screens.projection

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*

// ============================================================
// Phone Projection Hub (Android Auto + CarPlay)
// ============================================================

@Composable
fun ProjectionScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Detect installed projection apps
    val headunitReloaded = remember { isAppInstalled(context, "gb.xxy.hr") }
    val headunitRevived = remember { isAppInstalled(context, "headunit.revived") }
    val autoKit = remember { isAppInstalled(context, "com.carlinkit.autokit") }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingXl)
    ) {
        // Android Auto
        ProjectionCard(
            title = "Android Auto",
            icon = Icons.Filled.PhoneAndroid,
            color = EmeraldGreen,
            isAvailable = headunitReloaded || headunitRevived,
            appName = when {
                headunitReloaded -> "Headunit Reloaded"
                headunitRevived -> "Headunit Revived"
                else -> "Not Installed"
            },
            description = "Connect your Android phone to use Google Maps, calls, messaging, and music through Android Auto.",
            features = listOf(
                "⚡ Wired USB connection",
                "📶 Wireless WiFi connection",
                "🗺️ Google Maps navigation",
                "📞 Phone calls & messaging",
                "🎵 Media playback"
            ),
            setupInstructions = listOf(
                "1. Install 'Headunit Reloaded' from Play Store",
                "2. Connect phone via USB cable",
                "3. Grant USB permission when prompted",
                "4. Android Auto will launch automatically"
            ),
            onLaunch = {
                when {
                    headunitReloaded -> launchApp(context, "gb.xxy.hr")
                    headunitRevived -> launchApp(context, "headunit.revived")
                }
            },
            onSetup = {
                // Open Play Store for Headunit Reloaded
                openPlayStore(context, "gb.xxy.hr")
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )

        // Apple CarPlay
        ProjectionCard(
            title = "Apple CarPlay",
            icon = Icons.Filled.PhoneIphone,
            color = ElectricBlue,
            isAvailable = autoKit,
            appName = if (autoKit) "AutoKit" else "Dongle Required",
            description = "Connect your iPhone via CarPlay dongle to use Apple Maps, calls, messages, and Apple Music.",
            features = listOf(
                "⚡ Wired via USB dongle",
                "📶 Wireless (dongle dependent)",
                "🗺️ Apple Maps navigation",
                "📞 Phone & Siri",
                "🎵 Apple Music"
            ),
            setupInstructions = listOf(
                "1. Purchase a CarPlay USB dongle (e.g., Carlinkit)",
                "2. Install 'AutoKit' APK (from dongle packaging)",
                "3. Plug dongle into tablet USB port",
                "4. Connect iPhone via Lightning/USB-C cable or wireless"
            ),
            onLaunch = {
                if (autoKit) launchApp(context, "com.carlinkit.autokit")
            },
            onSetup = {
                // Open browser for CarPlay dongle info
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.carlinkit.com"))
                context.startActivity(intent)
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun ProjectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isAvailable: Boolean,
    appName: String,
    description: String,
    features: List<String>,
    setupInstructions: List<String>,
    onLaunch: () -> Unit,
    onSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(DriveDimens.iconLarge)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingXs)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(if (isAvailable) EmeraldGreen else AmberAccent)
                    )
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isAvailable) EmeraldGreen else AmberAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

        // Features
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            features.forEach { feature ->
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
        if (isAvailable) {
            Button(
                onClick = onLaunch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DriveDimens.largeTouchTarget),
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Launch, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("LAUNCH", fontWeight = FontWeight.Bold)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)) {
                Text(
                    text = "Setup Required",
                    style = MaterialTheme.typography.titleSmall,
                    color = AmberAccent,
                    fontWeight = FontWeight.SemiBold
                )
                setupInstructions.forEach { step ->
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(DriveDimens.spacingSm))

                OutlinedButton(
                    onClick = onSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DriveDimens.largeTouchTarget),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SETUP GUIDE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun isAppInstalled(context: Context, packageName: String): Boolean {
    return try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: Exception) {
        false
    }
}

private fun launchApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } catch (e: Exception) { /* no-op */ }
}

private fun openPlayStore(context: Context, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        context.startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        context.startActivity(intent)
    }
}
