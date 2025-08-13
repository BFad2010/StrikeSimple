package com.corp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FrameDao {
    @Query("SELECT * FROM FrameEntity WHERE frameId = :frameId")
    fun getFrame(frameId: String): FrameEntity

    @Insert
    fun insertFrame(frameEntity: FrameEntity)

    @Insert
    fun insertFrames(frames: List<FrameEntity>)

    @Query("UPDATE FrameEntity SET bonus_throw = :bonusThrow WHERE frameId = :frameId")
    fun updateFrameBonusThrow(frameId: String, bonusThrow: String)

    @Delete
    fun delete(frameEntity: FrameEntity)

    @Query("DELETE FROM FrameEntity")
    fun deleteAll()
}