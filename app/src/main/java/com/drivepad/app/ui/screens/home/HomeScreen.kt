package com.drivepad.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*

// ============================================================
// Home Dashboard Screen
// ============================================================

@Composable
fun HomeScreen(
    weatherIcon: String,
    weatherTemp: String,
    weatherDescription: String,
    weatherHumidity: String,
    weatherWind: String,
    weatherUpdated: String,
    nowPlayingTitle: String,
    nowPlayingArtist: String,
    nowPlayingAlbumArt: Any?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
    ) {
        // Left column: Navigation + Quick Access
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
        ) {
            // Navigation Card
            NavigationCard(
                onClick = { onNavigateToScreen("navigation") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

        }

        // Center column: Now Playing
        NowPlayingCard(
            title = nowPlayingTitle,
            artist = nowPlayingArtist,
            albumArtUrl = nowPlayingAlbumArt,
            isPlaying = isPlaying,
            onPlayPause = onPlayPause,
            onSkipNext = onSkipNext,
            onSkipPrevious = onSkipPrevious,
            onClick = { onNavigateToScreen("media") },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )

        // Right column: Weather + Info
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
        ) {
            // Weather Card
            WeatherCard(
                icon = weatherIcon,
                temperature = weatherTemp,
                description = weatherDescription,
                humidity = weatherHumidity,
                wind = weatherWind,
                updated = weatherUpdated,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            QuickAccessGrid(
                onItemClick = onNavigateToScreen,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ============================================================
// Navigation Quick-Launch Card
// ============================================================

@Composable
private fun NavigationCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        glowColor = ElectricBlueGlow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(DriveDimens.iconLarge)
                )
                Text(
                    text = "Navigation",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(DriveDimens.iconMedium)
            )
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

        // Simulated map preview placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerLow,
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to navigate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingSm))

        // Recent destinations hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Recent destinations available",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// Now Playing Card
// ============================================================

@Composable
private fun NowPlayingCard(
    title: String,
    artist: String,
    albumArtUrl: Any?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulsing glow animation for "now playing" indicator
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    GlassCard(
        modifier = modifier,
        onClick = onClick,
        glowColor = if (isPlaying) ElectricBlue.copy(alpha = glowAlpha * 0.15f) else Color.Transparent
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = if (isPlaying) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(DriveDimens.iconMedium)
                )
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Playing indicator dot
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
            }
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingLg))

        // Album art placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = if (isPlaying) {
                            listOf(
                                ElectricBlue.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.surfaceContainerLow,
                                MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (albumArtUrl != null) {
                AsyncImage(
                    model = albumArtUrl,
                    contentDescription = "Album artwork",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else if (title.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Album,
                    contentDescription = null,
                    tint = if (isPlaying) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.QueueMusic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No media playing",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

        // Track info
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title.ifEmpty { "No Track" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = artist.ifEmpty { "Unknown Artist" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier.size(DriveDimens.largeTouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(DriveDimens.iconLarge)
                )
            }

            Spacer(modifier = Modifier.width(DriveDimens.spacingLg))

            // Play/Pause button with filled background
            Box(
                modifier = Modifier
                    .size(DriveDimens.extraLargeTouchTarget)
                    .clip(CircleShape)
                    .background(ElectricBlue)
                    .clickable { onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(DriveDimens.iconLarge)
                )
            }

            Spacer(modifier = Modifier.width(DriveDimens.spacingLg))

            IconButton(
                onClick = onSkipNext,
                modifier = Modifier.size(DriveDimens.largeTouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(DriveDimens.iconLarge)
                )
            }
        }
    }
}

// ============================================================
// Weather Card
// ============================================================

@Composable
private fun WeatherCard(
    icon: String,
    temperature: String,
    description: String,
    humidity: String,
    wind: String,
    updated: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
        ) {
            Icon(
                imageVector = Icons.Outlined.WbSunny,
                contentDescription = null,
                tint = AmberAccent,
                modifier = Modifier.size(DriveDimens.iconSmall)
            )
            Text(
                text = "Weather",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingSm))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
        ) {
            Text(
                text = icon,
                fontSize = 36.sp
            )
            Column {
                Text(
                    text = temperature,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            WeatherMetric("Humidity", humidity, Icons.Filled.WaterDrop)
            WeatherMetric("Wind", wind, Icons.Filled.Air)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = updated,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun WeatherMetric(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = ElectricBlue)
        Text(value, style = MaterialTheme.typography.titleSmall)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ============================================================
// Quick Access Grid
// ============================================================

data class QuickAccessItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val tint: Color
)

@Composable
private fun QuickAccessGrid(
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        QuickAccessItem("media", "Audio", Icons.Filled.Headphones, AmberAccent),
        QuickAccessItem("projection", "Phone", Icons.Filled.PhoneAndroid, EmeraldGreen),
        QuickAccessItem("settings", "Settings", Icons.Filled.Settings, MaterialTheme.colorScheme.onSurfaceVariant),
        QuickAccessItem("connectivity", "Connect", Icons.Filled.Bluetooth, ElectricBlue),
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
    ) {
        items.forEach { item ->
            GlassCard(
                modifier = Modifier.weight(1f),
                onClick = { onItemClick(item.id) },
                padding = DriveDimens.spacingMd
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = item.tint,
                        modifier = Modifier.size(DriveDimens.iconLarge)
                    )
                    Spacer(modifier = Modifier.height(DriveDimens.spacingXs))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
