package com.drivepad.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.drivepad.app.data.api.RadioStation
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*
import java.util.Locale

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
    currentStation: RadioStation?,
    currentFrequency: Float,
    isRadioPlaying: Boolean,
    radioPlaybackError: String?,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onRadioPlayPause: () -> Unit,
    onRadioSeekNext: () -> Unit,
    onRadioSeekPrevious: () -> Unit,
    onRadioFrequencyChange: (Float) -> Unit,
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
                .weight(0.95f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
        ) {
            NavigationCard(
                onClick = { onNavigateToScreen("navigation") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            QuickAccessGrid(
                onItemClick = onNavigateToScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
            )
        }

        // Center column: Now Playing
        NowPlayingCard(
            title = nowPlayingTitle,
            artist = nowPlayingArtist,
            albumArtUrl = nowPlayingAlbumArt,
            isPlaying = isPlaying,
            currentStation = currentStation,
            currentFrequency = currentFrequency,
            isRadioPlaying = isRadioPlaying,
            radioPlaybackError = radioPlaybackError,
            onPlayPause = onPlayPause,
            onSkipNext = onSkipNext,
            onSkipPrevious = onSkipPrevious,
            onRadioPlayPause = onRadioPlayPause,
            onRadioSeekNext = onRadioSeekNext,
            onRadioSeekPrevious = onRadioSeekPrevious,
            onRadioFrequencyChange = onRadioFrequencyChange,
            onClick = { onNavigateToScreen("media") },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )

        // Right column: Weather + Info
        Column(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
        ) {
            WeatherCard(
                icon = weatherIcon,
                temperature = weatherTemp,
                description = weatherDescription,
                humidity = weatherHumidity,
                wind = weatherWind,
                updated = weatherUpdated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
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
    currentStation: RadioStation?,
    currentFrequency: Float,
    isRadioPlaying: Boolean,
    radioPlaybackError: String?,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onRadioPlayPause: () -> Unit,
    onRadioSeekNext: () -> Unit,
    onRadioSeekPrevious: () -> Unit,
    onRadioFrequencyChange: (Float) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showingRadio = isRadioPlaying || currentStation != null
    val active = if (showingRadio) isRadioPlaying else isPlaying
    val accentColor = if (showingRadio) AmberAccent else ElectricBlue

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
        glowColor = if (active) accentColor.copy(alpha = glowAlpha * 0.15f) else Color.Transparent
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
                    imageVector = if (showingRadio) Icons.Filled.Radio else Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = if (active) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(DriveDimens.iconMedium)
                )
                Text(
                    text = if (showingRadio) "FM Live" else "Now Playing",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Playing indicator dot
            if (active) {
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
                        colors = if (active) {
                            listOf(
                                accentColor.copy(alpha = 0.15f),
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
            if (showingRadio) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Radio,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                    Text(
                        text = String.format(Locale.US, "%.1f MHz", currentFrequency),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else if (albumArtUrl != null) {
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
                text = if (showingRadio) {
                    currentStation?.name ?: "No FM station"
                } else {
                    title.ifEmpty { "No Track" }
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (showingRadio && currentStation != null) AmberAccent else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (showingRadio) {
                    radioPlaybackError
                        ?: currentStation?.tags
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.filter { it.isNotBlank() }
                            ?.take(3)
                            ?.joinToString(" · ")
                            .orEmpty()
                            .ifBlank { "Internet FM radio" }
                } else {
                    artist.ifEmpty { "Unknown Artist" }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (showingRadio && radioPlaybackError != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

        if (showingRadio) {
            Slider(
                value = currentFrequency.coerceIn(88f, 108f),
                onValueChange = onRadioFrequencyChange,
                valueRange = 88f..108f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = AmberAccent,
                    activeTrackColor = AmberAccent,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("88.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Tune FM", style = MaterialTheme.typography.labelSmall, color = AmberAccent)
                Text("108.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
        }

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = if (showingRadio) onRadioSeekPrevious else onSkipPrevious,
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
                    .background(if (showingRadio) AmberAccent else ElectricBlue)
                    .clickable { if (showingRadio) onRadioPlayPause() else onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (showingRadio && isRadioPlaying) {
                        Icons.Filled.Stop
                    } else if (!showingRadio && isPlaying) {
                        Icons.Filled.Pause
                    } else {
                        Icons.Filled.PlayArrow
                    },
                    contentDescription = if (active) "Pause" else "Play",
                    tint = if (showingRadio) Color.Black else Color.White,
                    modifier = Modifier.size(DriveDimens.iconLarge)
                )
            }

            Spacer(modifier = Modifier.width(DriveDimens.spacingLg))

            IconButton(
                onClick = if (showingRadio) onRadioSeekNext else onSkipNext,
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
            modifier = Modifier.fillMaxWidth(),
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = updated,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                    text = temperature.takeIf { it.isNotBlank() } ?: "--°C",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description.takeIf { it.isNotBlank() } ?: "Updating...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            WeatherMetric("Humidity", humidity, Icons.Filled.WaterDrop)
            WeatherMetric("Wind", wind, Icons.Filled.Air)
        }
    }
}

@Composable
private fun WeatherMetric(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
        Text(
            value.takeIf { it.isNotBlank() } ?: "--",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
            ) {
                rowItems.forEach { item ->
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onItemClick(item.id) },
                        padding = DriveDimens.spacingMd
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = item.tint,
                                modifier = Modifier.size(DriveDimens.iconMedium)
                            )
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
