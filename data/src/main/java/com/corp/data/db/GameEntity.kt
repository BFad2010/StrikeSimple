package com.corp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameEntity(
    @PrimaryKey val gameId: String,
    @ColumnInfo(name = "players") val players: List<String>,
    @ColumnInfo(name = "frames") val frames: List<String>,
    @ColumnInfo(name = "bonusThrow") val bonusThrow: Int?,
    @ColumnInfo(name = "isComplete") val isComplete: String,
)