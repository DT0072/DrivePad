package com.drivepad.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "radio_presets")
data class RadioPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val slotNumber: Int,           // 1-6 for preset buttons
    val stationName: String,
    val stationUrl: String,        // Stream URL
    val stationFavicon: String = "",
    val frequency: String = "",    // Display frequency (e.g., "98.8")
    val country: String = "",
    val tags: String = "",         // Genre tags
    val isFavorite: Boolean = false,
    val lastPlayedTimestamp: Long = 0
)
