package com.drivepad.app.ui.screens.radio

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepad.app.data.api.RadioStation
import com.drivepad.app.ui.components.GlassCard
import com.drivepad.app.ui.theme.*
import kotlin.math.roundToInt
import java.util.Locale

// ============================================================
// FM Radio Screen (Internet Radio with FM-style UI)
// ============================================================

@Composable
fun RadioScreen(
    currentStation: RadioStation?,
    currentFrequency: Float,
    isPlaying: Boolean,
    playbackError: String?,
    stations: List<RadioStation>,
    presets: List<RadioStation?>,
    onFrequencyChange: (Float) -> Unit,
    onPlayPause: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onStationSelect: (RadioStation) -> Unit,
    onPresetSave: (Int) -> Unit,
    onPresetLoad: (Int) -> Unit,
    onSearch: () -> Unit,
    onAutoScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(DriveDimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg)
    ) {
        // Main radio tuner
        Column(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingLg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Station info
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Radio,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(DriveDimens.iconMedium)
                            )
                            Text(
                                text = "FM RADIO",
                                style = MaterialTheme.typography.labelMedium,
                                color = AmberAccent,
                                letterSpacing = 2.sp
                            )
                        }
                        FilledTonalButton(
                            onClick = { onPresetSave(0) },
                            enabled = currentStation != null,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = AmberAccent.copy(alpha = 0.18f),
                                contentColor = AmberAccent,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save P1", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(DriveDimens.spacingMd))

                    // Frequency display
                    Text(
                        text = String.format(Locale.US, "%.1f", currentFrequency),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "MHz",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(DriveDimens.spacingSm))

                    // Station name
                    Text(
                        text = currentStation?.name ?: "Scanning...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (currentStation != null) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (currentStation != null && currentStation.tags.isNotEmpty()) {
                        Text(
                            text = currentStation.tags.split(",").take(3).joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (playbackError != null) {
                        Spacer(modifier = Modifier.height(DriveDimens.spacingXs))
                        Text(
                            text = playbackError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // Frequency dial
            FrequencyDial(
                frequency = currentFrequency,
                onFrequencyChange = onFrequencyChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { onFrequencyChange((currentFrequency - 0.1f).roundToTenth()) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("0.1")
                }
                Text(
                    text = "Drag or tap the dial to tune",
                    modifier = Modifier.weight(1.4f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(
                    onClick = { onFrequencyChange((currentFrequency + 0.1f).roundToTenth()) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("0.1")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }

            // Transport controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seek backward
                IconButton(
                    onClick = onSeekBackward,
                    modifier = Modifier.size(DriveDimens.largeTouchTarget)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Seek Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(DriveDimens.iconLarge)
                    )
                }

                Spacer(modifier = Modifier.width(DriveDimens.spacingXl))

                // Play/Stop
                Box(
                    modifier = Modifier
                        .size(DriveDimens.extraLargeTouchTarget)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AmberAccent, AmberAccentDark)
                            )
                        )
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Stop" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(DriveDimens.iconLarge)
                    )
                }

                Spacer(modifier = Modifier.width(DriveDimens.spacingXl))

                // Seek forward
                IconButton(
                    onClick = onSeekForward,
                    modifier = Modifier.size(DriveDimens.largeTouchTarget)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Seek Forward",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(DriveDimens.iconLarge)
                    )
                }
            }

        }

        // Right: Station list + actions
        Column(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
        ) {
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = onSearch,
                    padding = DriveDimens.spacingSm
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Search, "Search", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Search", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = onAutoScan,
                    padding = DriveDimens.spacingSm
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.SettingsInputAntenna, "Scan", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            FavoritePresetsPanel(
                presets = presets,
                currentStation = currentStation,
                onPresetLoad = onPresetLoad,
                onPresetSave = onPresetSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
            )

            Text(
                text = "Stations (${stations.size})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            // Station list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingXs)
            ) {
                itemsIndexed(stations) { index, station ->
                    val isActive = station.stationUuid == currentStation?.stationUuid
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onStationSelect(station) },
                        padding = DriveDimens.spacingSm,
                        backgroundColor = if (isActive) AmberAccent.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                        borderColor = if (isActive) AmberAccent.copy(alpha = 0.3f)
                        else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingSm)
                        ) {
                            if (isActive && isPlaying) {
                                // Animated equalizer indicator
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = station.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isActive) AmberAccent else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${station.tunedFrequency(index).formatFrequency()} MHz",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritePresetsPanel(
    presets: List<RadioStation?>,
    currentStation: RadioStation?,
    onPresetLoad: (Int) -> Unit,
    onPresetSave: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DriveDimens.spacingXs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingXs),
        ) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
            Text(
                text = "Favorite Presets",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
        }
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingXs),
            ) {
                for (column in 0 until 2) {
                    val index = row * 2 + column
                    val preset = presets.getOrNull(index)
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { if (preset != null) onPresetLoad(index) },
                        padding = DriveDimens.spacingSm,
                        backgroundColor = if (preset != null) {
                            AmberAccent.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.45f)
                        },
                        borderColor = if (preset != null) AmberAccent.copy(alpha = 0.3f) else Color.Transparent,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(DriveDimens.spacingXs),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "P${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (preset != null) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = preset?.name ?: "Empty",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            IconButton(
                                onClick = { onPresetSave(index) },
                                enabled = currentStation != null,
                                modifier = Modifier.size(32.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Save,
                                    contentDescription = "Save to P${index + 1}",
                                    tint = if (currentStation != null) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// Custom Frequency Dial Composable
// ============================================================

@Composable
fun FrequencyDial(
    frequency: Float,
    onFrequencyChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.outline
    val activeColor = AmberAccent
    val tickColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)

    // Normalize frequency to 0-1 range (88.0 - 108.0 MHz)
    val normalizedPos = ((frequency - 88f) / 20f).coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                fun tuneFromX(x: Float) {
                    val normalized = (x / size.width).coerceIn(0f, 1f)
                    onFrequencyChange((88f + normalized * 20f).roundToTenth())
                }
                detectTapGestures { offset ->
                    tuneFromX(offset.x)
                }
            }
            .pointerInput(Unit) {
                fun tuneFromX(x: Float) {
                    val normalized = (x / size.width).coerceIn(0f, 1f)
                    onFrequencyChange((88f + normalized * 20f).roundToTenth())
                }
                detectDragGestures(
                    onDragStart = { offset -> tuneFromX(offset.x) },
                    onDrag = { change, _ ->
                        tuneFromX(change.position.x)
                    },
                )
            }
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        // Draw track
        drawLine(
            color = trackColor,
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Draw tick marks
        val tickCount = 40 // 88 to 108 in 0.5 steps
        for (i in 0..tickCount) {
            val x = (i.toFloat() / tickCount) * width
            val isMajor = i % 4 == 0
            val tickHeight = if (isMajor) height * 0.5f else height * 0.25f
            drawLine(
                color = tickColor,
                start = Offset(x, centerY - tickHeight / 2),
                end = Offset(x, centerY + tickHeight / 2),
                strokeWidth = if (isMajor) 2f else 1f
            )
        }

        // Draw active portion
        drawLine(
            color = activeColor.copy(alpha = 0.4f),
            start = Offset(0f, centerY),
            end = Offset(normalizedPos * width, centerY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Draw indicator
        val indicatorX = normalizedPos * width
        drawCircle(
            color = activeColor,
            radius = 12f,
            center = Offset(indicatorX, centerY)
        )
        drawCircle(
            color = activeColor.copy(alpha = 0.3f),
            radius = 20f,
            center = Offset(indicatorX, centerY)
        )
    }
}

private fun Float.roundToTenth(): Float = (this * 10f).roundToInt() / 10f

private fun Float.formatFrequency(): String = String.format(Locale.US, "%.1f", this)

private fun RadioStation.tunedFrequency(index: Int): Float {
    val published = getDisplayFrequency().toFloatOrNull()
    if (published != null && published in 88f..108f) {
        return published.roundToTenth()
    }
    return (88f + ((index * 7) % 201) / 10f).coerceIn(88f, 108f).roundToTenth()
}
