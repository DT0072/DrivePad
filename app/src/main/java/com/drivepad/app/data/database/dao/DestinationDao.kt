package com.drivepad.app.data.database.dao

import androidx.room.*
import com.drivepad.app.data.database.entity.DestinationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destinations ORDER BY lastUsedTimestamp DESC LIMIT 10")
    fun getRecentDestinations(): Flow<List<DestinationEntity>>

    @Query("SELECT * FROM destinations WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteDestinations(): Flow<List<DestinationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(destination: DestinationEntity): Long

    @Update
    suspend fun update(destination: DestinationEntity)

    @Delete
    suspend fun delete(destination: DestinationEntity)

    @Query("UPDATE destinations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}
