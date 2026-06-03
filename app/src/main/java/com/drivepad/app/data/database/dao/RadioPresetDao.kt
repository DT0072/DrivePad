package com.drivepad.app.data.database.dao

import androidx.room.*
import com.drivepad.app.data.database.entity.RadioPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RadioPresetDao {
    @Query("SELECT * FROM radio_presets ORDER BY slotNumber ASC")
    fun getAllPresets(): Flow<List<RadioPresetEntity>>

    @Query("SELECT * FROM radio_presets WHERE isFavorite = 1 ORDER BY stationName ASC")
    fun getFavoriteStations(): Flow<List<RadioPresetEntity>>

    @Query("SELECT * FROM radio_presets WHERE slotNumber = :slot LIMIT 1")
    suspend fun getPresetBySlot(slot: Int): RadioPresetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: RadioPresetEntity): Long

    @Update
    suspend fun update(preset: RadioPresetEntity)

    @Delete
    suspend fun delete(preset: RadioPresetEntity)

    @Query("UPDATE radio_presets SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM radio_presets")
    suspend fun getCount(): Int
}
