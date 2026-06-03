package com.drivepad.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val albumArtUri: String = "",
    val sourceApp: String = "",       // Package name of source app
    val sourceLabel: String = "",     // Display name (e.g., "Spotify")
    val playedTimestamp: Long = System.currentTimeMillis()
)
