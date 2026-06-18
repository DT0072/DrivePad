package com.drivepad.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.drivepad.app.data.api.RadioStation
import com.drivepad.app.ui.screens.navigation.InAppNavigationView
import com.drivepad.app.ui.screens.navigation.NavigationMapLocation
import com.drivepad.app.ui.theme.*
import java.util.Locale

@Composable
fun HomeScreen(
    weatherIcon: String,
    weatherTemp: String,
    weatherDescription: String,
    weatherHumidity: String,
    weatherWind: String,
    weatherUpdated: String,
    navigationLocation: NavigationMapLocation,
    isNavigationSearching: Boolean,
    navigationSearchError: String?,
    nowPlayingTitle: String,
    nowPlayingArtist: String,
    nowPlayingAlbumArt: Any?,
    isPlaying: Boolean,
    currentStation: RadioStation?,
    currentFrequency: Float,
    isRadioPlaying: Boolean,
    radioPlaybackError: String?,
    radioPresets: List<RadioStation?>,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onRadioPlayPause: () -> Unit,
    onRadioSeekNext: () -> Unit,
    onRadioSeekPrevious: () -> Unit,
    onRadioFrequencyChange: (Float) -> Unit,
    onPresetLoad: (Int) -> Unit,
    onPresetSave: (Int) -> Unit,
    onNavigationSearch: (String) -> Unit,
    onNavigateToScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val compact = maxWidth < 980.dp
        var destinationInput by rememberSaveable { mutableStateOf("") }
        val mapWeight = if (compact) 0.57f else 0.64f
        val panelWeight = 1f - mapWeight

        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(mapWeight)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            ) {
                InAppNavigationView(location = navigationLocation, modifier = Modifier.fillMaxSize())

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(if (compact) 12.dp else 18.dp)
                        .fillMaxWidth(if (compact) 0.78f else 0.64f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = destinationInput,
                        onValueChange = { destinationInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search destination") },
                        leadingIcon = { Icon(Icons.Filled.Search, null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                destinationInput.trim().takeIf { it.isNotBlank() }?.let(onNavigationSearch)
                            }) {
                                if (isNavigationSearching) {
                                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface.copy(alpha = 0.96f),
                            unfocusedContainerColor = DarkSurface.copy(alpha = 0.92f),
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkDivider,
                        ),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MapShortcut("Home", Icons.Filled.Home, Modifier.weight(1f)) { onNavigationSearch("Kuala Lumpur") }
                        MapShortcut("Work", Icons.Filled.Work, Modifier.weight(1f)) { onNavigationSearch("Petronas Twin Towers") }
                        MapShortcut("Fuel", Icons.Filled.LocalGasStation, Modifier.weight(1f)) { onNavigationSearch("Petrol station Kuala Lumpur") }
                    }
                    navigationSearchError?.let { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurface.copy(alpha = 0.94f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(if (compact) 12.dp else 18.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface.copy(alpha = 0.95f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.TurnRight, null, tint = ElectricBlue, modifier = Modifier.size(30.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("250 m", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                navigationLocation.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            ContextPanel(
                title = nowPlayingTitle,
                artist = nowPlayingArtist,
                albumArt = nowPlayingAlbumArt,
                isPlaying = isPlaying,
                station = currentStation,
                frequency = currentFrequency,
                isRadioPlaying = isRadioPlaying,
                radioError = radioPlaybackError,
                presets = radioPresets,
                onPlayPause = onPlayPause,
                onNext = onSkipNext,
                onPrevious = onSkipPrevious,
                onRadioPlayPause = onRadioPlayPause,
                onRadioNext = onRadioSeekNext,
                onRadioPrevious = onRadioSeekPrevious,
                onFrequencyChange = onRadioFrequencyChange,
                onPresetLoad = onPresetLoad,
                onPresetSave = onPresetSave,
                onOpenAudio = { onNavigateToScreen("media") },
                modifier = Modifier
                    .weight(panelWeight)
                    .fillMaxHeight(),
                compact = compact,
            )
        }
    }
}

@Composable
private fun MapShortcut(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(54.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = DarkSurface.copy(alpha = 0.94f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkDivider),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(icon, label, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(7.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

@Composable
private fun ContextPanel(
    title: String,
    artist: String,
    albumArt: Any?,
    isPlaying: Boolean,
    station: RadioStation?,
    frequency: Float,
    isRadioPlaying: Boolean,
    radioError: String?,
    presets: List<RadioStation?>,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRadioPlayPause: () -> Unit,
    onRadioNext: () -> Unit,
    onRadioPrevious: () -> Unit,
    onFrequencyChange: (Float) -> Unit,
    onPresetLoad: (Int) -> Unit,
    onPresetSave: (Int) -> Unit,
    onOpenAudio: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean,
) {
    var showRadio by remember(isRadioPlaying, isPlaying) { mutableStateOf(isRadioPlaying || !isPlaying) }
    val activePresets = remember(presets) {
        presets.withIndex().filter { it.value != null }
    }
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = if (compact) 12.dp else 18.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ContextTab("FM", showRadio) { showRadio = true }
            ContextTab("Music", !showRadio) { showRadio = false }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onOpenAudio) { Icon(Icons.Filled.Tune, "Open audio") }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        if (showRadio) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        String.format(Locale.US, "%.1f", frequency),
                        style = if (compact) MaterialTheme.typography.displayMedium else MaterialTheme.typography.displayLarge,
                        color = AmberAccent,
                        fontWeight = FontWeight.Medium,
                    )
                    Text("MHz", style = MaterialTheme.typography.titleMedium, color = AmberAccent)
                    Spacer(Modifier.height(8.dp))
                    Text(station?.name ?: "FM Radio", style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        radioError ?: station?.tags?.split(",")?.firstOrNull().orEmpty().ifBlank { "Live radio" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (radioError == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        maxLines = 1,
                    )
                }
                Column(Modifier.fillMaxWidth()) {
                    Slider(
                        value = frequency.coerceIn(88f, 108f),
                        onValueChange = onFrequencyChange,
                        valueRange = 88f..108f,
                        colors = SliderDefaults.colors(
                            thumbColor = AmberAccent,
                            activeTrackColor = AmberAccent,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline,
                        ),
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("88.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("108.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                MediaControls(
                    playing = isRadioPlaying,
                    stopWhenPlaying = true,
                    accent = AmberAccent,
                    onPrevious = onRadioPrevious,
                    onPlayPause = onRadioPlayPause,
                    onNext = onRadioNext,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilledTonalButton(
                        onClick = { onPresetSave(0) },
                        enabled = station != null,
                        modifier = Modifier.weight(0.9f).height(if (compact) 44.dp else 48.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AmberAccent.copy(alpha = 0.15f),
                            contentColor = AmberAccent,
                        ),
                    ) {
                        Icon(Icons.Filled.Star, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save favorite")
                    }
                    OutlinedButton(
                        onClick = { activePresets.firstOrNull()?.let { onPresetLoad(it.index) } },
                        enabled = activePresets.isNotEmpty(),
                        modifier = Modifier.weight(0.9f).height(if (compact) 44.dp else 48.dp),
                    ) {
                        Text("Load favorite")
                    }
                }
                if (activePresets.isNotEmpty()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        activePresets.forEach { (index, preset) ->
                            Surface(
                                modifier = Modifier.weight(1f).height(if (compact) 52.dp else 60.dp),
                                onClick = { onPresetLoad(index) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (preset?.stationUuid == station?.stationUuid) AmberAccent.copy(alpha = 0.12f) else DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (preset?.stationUuid == station?.stationUuid) AmberAccent else DarkDivider,
                                ),
                            ) {
                                Column(
                                    Modifier.fillMaxSize().padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text("P${index + 1}", style = MaterialTheme.typography.labelMedium, color = if (preset != null) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (!compact) Text(preset?.getDisplayFrequency().orEmpty(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkDivider),
                    ) {
                        Text(
                            text = "No saved favorites yet. Save the current station to see it here.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 150.dp else 210.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    if (albumArt != null) {
                        AsyncImage(albumArt, "Album art", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Filled.Album, null, tint = ElectricBlue, modifier = Modifier.size(80.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(title.ifBlank { "No music playing" }, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(artist.ifBlank { "Select a media source" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Spacer(Modifier.height(18.dp))
                MediaControls(isPlaying, false, ElectricBlue, onPrevious, onPlayPause, onNext)
            }
        }
    }
}

@Composable
private fun ContextTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = if (selected) CockpitRed else MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(7.dp))
        Box(Modifier.width(44.dp).height(2.dp).background(if (selected) CockpitRed else Color.Transparent))
    }
}

@Composable
private fun MediaControls(
    playing: Boolean,
    stopWhenPlaying: Boolean,
    accent: Color,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        IconButton(onClick = onPrevious, modifier = Modifier.size(52.dp)) { Icon(Icons.Filled.SkipPrevious, "Previous", modifier = Modifier.size(30.dp)) }
        Box(
            modifier = Modifier.size(62.dp).clip(RoundedCornerShape(8.dp)).background(accent).clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (playing && stopWhenPlaying) Icons.Filled.Stop else if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Play or pause",
                tint = if (accent == AmberAccent) Color.Black else Color.White,
                modifier = Modifier.size(34.dp),
            )
        }
        IconButton(onClick = onNext, modifier = Modifier.size(52.dp)) { Icon(Icons.Filled.SkipNext, "Next", modifier = Modifier.size(30.dp)) }
    }
}
