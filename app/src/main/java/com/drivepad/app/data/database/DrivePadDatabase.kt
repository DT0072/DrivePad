package com.drivepad.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drivepad.app.data.database.dao.DestinationDao
import com.drivepad.app.data.database.dao.RadioPresetDao
import com.drivepad.app.data.database.dao.RecentlyPlayedDao
import com.drivepad.app.data.database.entity.DestinationEntity
import com.drivepad.app.data.database.entity.RadioPresetEntity
import com.drivepad.app.data.database.entity.RecentlyPlayedEntity

@Database(
    entities = [
        DestinationEntity::class,
        RadioPresetEntity::class,
        RecentlyPlayedEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class DrivePadDatabase : RoomDatabase() {
    abstract fun destinationDao(): DestinationDao
    abstract fun radioPresetDao(): RadioPresetDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
}
