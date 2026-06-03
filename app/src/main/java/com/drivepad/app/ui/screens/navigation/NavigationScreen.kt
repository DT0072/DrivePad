package com.drivepad.app.ui.screens.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                                text = if (isInstalled) "Tap to open" else "Not installed",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isInstalled) app.color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                        if (isInstalled) {
                            Icon(
                                imageVector = Icons.Filled.Launch,
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
                    onClick = { searchInMaps(context, "Home") },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.Work,
                    label = "Work",
                    color = EmeraldGreen,
                    onClick = { searchInMaps(context, "Work") },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.LocalGasStation,
                    label = "Fuel",
                    color = AmberAccent,
                    onClick = { searchInMaps(context, "Gas station near me") },
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
            // Map placeholder
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = {
                    if (installedApps.isNotEmpty()) {
                        launchNavApp(context, installedApps.first().packageName)
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ElectricBlue.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = null,
                            tint = ElectricBlue.copy(alpha = 0.4f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))
                        Text(
                            text = "Tap to open navigation",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Full-screen navigation experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
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

private fun searchInMaps(context: Context, query: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // No maps app available
    }
}
