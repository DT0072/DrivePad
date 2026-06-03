package com.drivepad.app.data.database.dao

import androidx.room.*
import com.drivepad.app.data.database.entity.RecentlyPlayedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {
    @Query("SELECT * FROM recently_played ORDER BY playedTimestamp DESC LIMIT 20")
    fun getRecentlyPlayed(): Flow<List<RecentlyPlayedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: RecentlyPlayedEntity): Long

    @Query("DELETE FROM recently_played WHERE id NOT IN (SELECT id FROM recently_played ORDER BY playedTimestamp DESC LIMIT 50)")
    suspend fun trimOldEntries()
}
