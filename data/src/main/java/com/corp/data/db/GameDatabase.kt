package com.corp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.corp.data.db.typeconverters.ListStringTypeConverter

@Database(
    entities = [GameEntity::class, FrameEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(ListStringTypeConverter::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun frameDao(): FrameDao
}