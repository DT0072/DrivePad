package com.drivepad.app.ui.screens.media

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.drivepad.app.data.api.RadioStation
import com.drivepad.app.ui.screens.radio.RadioScreen
import com.drivepad.app.ui.theme.DriveDimens

private enum class AudioMode {
    MUSIC,
    RADIO,
}

@Composable
fun AudioScreen(
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
    currentStation: RadioStation?,
    currentFrequency: Float,
    isRadioPlaying: Boolean,
    radioPlaybackError: String?,
    stations: List<RadioStation>,
    presets: List<RadioStation?>,
    onFrequencyChange: (Float) -> Unit,
    onRadioPlayPause: () -> Unit,
    onRadioSeekForward: () -> Unit,
    onRadioSeekBackward: () -> Unit,
    onStationSelect: (RadioStation) -> Unit,
    onPresetSave: (Int) -> Unit,
    onPresetLoad: (Int) -> Unit,
    onRadioSearch: () -> Unit,
    onAutoScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by remember { mutableStateOf(AudioMode.MUSIC) }

    Column(modifier = modifier.fillMaxSize()) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = DriveDimens.spacingLg,
                    end = DriveDimens.spacingLg,
                    top = DriveDimens.spacingSm,
                ),
        ) {
            AudioMode.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = mode == item,
                    onClick = { mode = item },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AudioMode.entries.size,
                    ),
                    icon = {
                        Icon(
                            imageVector = if (item == AudioMode.MUSIC) {
                                Icons.Filled.MusicNote
                            } else {
                                Icons.Filled.Radio
                            },
                            contentDescription = null,
                        )
                    },
                ) {
                    Text(if (item == AudioMode.MUSIC) "Music" else "Radio")
                }
            }
        }

        when (mode) {
            AudioMode.MUSIC -> MediaScreen(
                nowPlayingTitle = nowPlayingTitle,
                nowPlayingArtist = nowPlayingArtist,
                nowPlayingAlbum = nowPlayingAlbum,
                nowPlayingAlbumArt = nowPlayingAlbumArt,
                isPlaying = isPlaying,
                playbackProgress = playbackProgress,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                onPlayPause = onPlayPause,
                onSkipNext = onSkipNext,
                onSkipPrevious = onSkipPrevious,
                onSeek = onSeek,
                activeSource = activeSource,
                activeMediaPackage = activeMediaPackage,
                onSourceSelected = onSourceSelected,
                hasMediaControlAccess = hasMediaControlAccess,
                onRequestMediaControlAccess = onRequestMediaControlAccess,
                volume = volume,
                onVolumeChange = onVolumeChange,
                modifier = Modifier.weight(1f),
            )

            AudioMode.RADIO -> RadioScreen(
                currentStation = currentStation,
                currentFrequency = currentFrequency,
                isPlaying = isRadioPlaying,
                playbackError = radioPlaybackError,
                stations = stations,
                presets = presets,
                onFrequencyChange = onFrequencyChange,
                onPlayPause = onRadioPlayPause,
                onSeekForward = onRadioSeekForward,
                onSeekBackward = onRadioSeekBackward,
                onStationSelect = onStationSelect,
                onPresetSave = onPresetSave,
                onPresetLoad = onPresetLoad,
                onSearch = onRadioSearch,
                onAutoScan = onAutoScan,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
