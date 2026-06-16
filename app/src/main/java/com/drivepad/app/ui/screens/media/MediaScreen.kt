package com.drivepad.app.ui.screens.media

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*
import coil3.compose.AsyncImage

// ============================================================
// Media Center Screen
// ============================================================

data class MediaSource(
    val id: String,
    val name: String,
    val packageName: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun MediaScreen(
    nowPlayingTitle: String,
    nowPlayingArtist: String,
    nowPlayingAlbum: String,
    nowPlayingAlbumArt: Any?,
    isPlaying: Boolean,
    playbackProgress: Float,
    currentPosition: String,
    totalDuration: String,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    activeSource: String,
    activeMediaPackage: String,
    onSourceSelected: (String) -> Unit,
    hasMediaControlAccess: Boolean,
    onRequestMediaControlAccess: () -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mediaSources = remember {
        listOf(
            MediaSource("spotify", "Spotify", "com.spotify.music", Icons.Filled.GraphicEq, EmeraldGreen),
            MediaSource("ytmusic", "YT Music", "com.google.android.apps.youtube.music", Icons.Filled.PlayCircle, CoralRed),
            MediaSource("local", "Local Music", "", Icons.Filled.FolderOpen, AmberAccent),
        )
    }
    val activePackageSource = remember(activeMediaPackage, mediaSources) {
        mediaSources.firstOrNull {
            it.packageName.isNotEmpty() && it.packageName == activeMediaPackage
        }
    }
    val externalActiveSource = remember(activeMediaPackage) {
        if (activeMediaPackage.isNotBlank() && activePackageSource == null) {
            MediaSource(
                id = "external",
                name = getAppLabel(context, activeMediaPackage),
                packageName = activeMediaPackage,
                icon = Icons.Filled.MusicNote,
                color = ElectricBlue,
            )
        } else {
            null
        }
    }
    val visibleSources = remember(activeMediaPackage, activePackageSource, externalActiveSource, mediaSources) {
        when {
            activePackageSource != null -> listOf(activePackageSource)
            externalActiveSource != null -> listOf(externalActiveSource)
            else -> mediaSources
        }
    }

    // Spinning disc animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
    ) {
        // Left: Source selector + Recently played
        Column(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
        ) {
            Text(
                text = "Sources",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (activeMediaPackage.isBlank()) {
                    "Open a player and it will appear here automatically."
                } else {
                    "Showing active media session"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            visibleSources.forEach { source ->
                val isActive = source.packageName == activeMediaPackage ||
                    (activeMediaPackage.isBlank() && source.id == activeSource)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (source.id != "external") {
                            onSourceSelected(source.id)
                        }
                        if (source.packageName.isNotEmpty()) {
                            launchApp(context, source.packageName)
                        }
                    },
                    backgroundColor = if (isActive) {
                        source.color.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                    },
                    borderColor = if (isActive) {
                        source.color.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    },
                    padding = DriveDimens.spacingMd
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingMd)
                    ) {
                        Icon(
                            imageVector = source.icon,
                            contentDescription = source.name,
                            tint = if (isActive) source.color else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(DriveDimens.iconMedium)
                        )
                        Text(
                            text = source.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isActive) source.color else MaterialTheme.colorScheme.onSurface
                        )
                        if (isActive) {
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(source.color)
                            )
                        }
                    }
                }
            }

            if (visibleSources.size == 1) {
                val source = visibleSources.first()
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = DriveDimens.spacingMd,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.45f),
                ) {
                    Text(
                        text = "${source.name} library",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Android media controls expose the current song and transport controls. Open ${source.name} to browse playlists or song lists.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (source.packageName.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                        OutlinedButton(
                            onClick = { launchApp(context, source.packageName) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Filled.LibraryMusic, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open ${source.name}")
                        }
                    }
                }
            }

            if (!hasMediaControlAccess) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = AmberAccent.copy(alpha = 0.1f),
                    borderColor = AmberAccent.copy(alpha = 0.35f),
                    padding = DriveDimens.spacingMd,
                ) {
                    Text(
                        text = "Media access required",
                        style = MaterialTheme.typography.titleSmall,
                        color = AmberAccent,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Allow DrivePad to read active playback so these controls can operate YouTube Music and other players.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(DriveDimens.spacingSm))
                    Button(
                        onClick = onRequestMediaControlAccess,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                    ) {
                        Text("Enable media access", color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Recently played
            Text(
                text = "Recently Played",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Play history will appear here",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }

        // Center + Right: Album art + Controls
        Column(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .size(DriveDimens.albumArtLarge)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            colors = if (isPlaying) {
                                listOf(
                                    ElectricBlue.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.surfaceContainerLow,
                                    MaterialTheme.colorScheme.surfaceContainer
                                )
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (nowPlayingAlbumArt != null) {
                    AsyncImage(
                        model = nowPlayingAlbumArt,
                        contentDescription = "Album artwork",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Album,
                        contentDescription = "Album Art",
                        tint = if (isPlaying) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(120.dp)
                            .then(
                                if (isPlaying) Modifier.rotate(discRotation) else Modifier
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DriveDimens.spacingXl))

            // Track info
            Text(
                text = nowPlayingTitle.ifEmpty { "No Track Playing" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nowPlayingArtist.ifEmpty { "Unknown Artist" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (nowPlayingAlbum.isNotEmpty()) {
                Text(
                    text = nowPlayingAlbum,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(DriveDimens.spacingXl))

            // Progress bar
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Slider(
                    value = playbackProgress,
                    onValueChange = onSeek,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricBlue,
                        activeTrackColor = ElectricBlue,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentPosition,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = totalDuration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(DriveDimens.spacingLg))

            // Playback controls
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* shuffle */ },
                    modifier = Modifier.size(DriveDimens.minTouchTarget)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "Shuffle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(DriveDimens.iconMedium)
                    )
                }

                Spacer(modifier = Modifier.width(DriveDimens.spacingLg))

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

                Spacer(modifier = Modifier.width(DriveDimens.spacingMd))

                // Big play/pause button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElectricBlue, ElectricBlueDark)
                            )
                        )
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(DriveDimens.spacingMd))

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

                Spacer(modifier = Modifier.width(DriveDimens.spacingLg))

                IconButton(
                    onClick = { /* repeat */ },
                    modifier = Modifier.size(DriveDimens.minTouchTarget)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = "Repeat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(DriveDimens.iconMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

            Row(
                modifier = Modifier.fillMaxWidth(0.72f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                    contentDescription = "Lower volume",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Slider(
                    value = volume.coerceIn(0f, 1f),
                    onValueChange = onVolumeChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricBlue,
                        activeTrackColor = ElectricBlue,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Raise volume",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun launchApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        // App not found
    }
}

private fun getAppLabel(context: Context, packageName: String): String {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
        context.packageManager.getApplicationLabel(appInfo).toString()
    } catch (_: Exception) {
        packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }
    }
}
