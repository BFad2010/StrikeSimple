package com.corp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM GameEntity WHERE gameId = :gameId")
    fun getGameData(gameId: String): GameEntity?

    @Query("SELECT * FROM GameEntity")
    fun getAllGames(): List<GameEntity>

    @Insert
    fun insertGame(gameEntity: GameEntity)

    @Query("UPDATE GameEntity SET frames=:frames WHERE gameId = :gameId")
    fun addFrame(gameId: String, frames: List<String>)

    @Query("UPDATE GameEntity SET isComplete = :isComplete WHERE gameId = :gameId")
    fun finishGame(gameId: String, isComplete: String)

    @Delete
    fun delete(gameEntity: GameEntity)

    @Query("DELETE FROM GameEntity")
    fun deleteAll()
}