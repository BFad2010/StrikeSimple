package com.corp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FrameEntity(
    @PrimaryKey val frameId: String,
    @ColumnInfo(name = "player") val player: String,
    @ColumnInfo(name = "frame") val number: String,
    @ColumnInfo(name = "throw_one") val throwOne: Int,
    @ColumnInfo(name = "throw_two") val throwTwo: Int,
    @ColumnInfo(name = "bonus_throw") val bonusThrow: Int?,
    @ColumnInfo(name = "second_bonus_throw") val secondBonusThrow: Int?,
)