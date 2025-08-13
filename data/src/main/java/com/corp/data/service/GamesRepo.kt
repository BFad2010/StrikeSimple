package com.corp.data.service

import com.corp.data.db.FrameDao
import com.corp.data.db.FrameEntity
import com.corp.data.db.GameDao
import com.corp.data.db.GameEntity
import com.corp.data.model.Frame
import com.corp.data.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamesRepo @Inject constructor(
    private val gameDao: GameDao,
    private val frameDao: FrameDao,
) {
    suspend fun getAllGames(): List<GameEntity> {
        return gameDao.getAllGames()
    }

    suspend fun insertNewGame(game: Game) = withContext(Dispatchers.IO) {
        val gameEntity = GameEntity(
            gameId = game.id,
            players = game.players,
            frames = game.frames.map { it.id },
            bonusThrow = game.bonusThrow,
            isComplete = "false",
        )
        val frameEntities = game.frames.map { frame ->
            FrameEntity(
                frameId = frame.id,
                player = frame.player,
                number = frame.number,
                throwOne = frame.pins.first(),
                throwTwo = frame.pins.last(),
                bonusThrow = null,
                secondBonusThrow = null,
            )
        }
        try {
            gameDao.insertGame(gameEntity)
            frameDao.insertFrames(frameEntities)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addFrame(gameId: String, frame: Frame) = withContext(Dispatchers.IO) {
        val game = getGameById(gameId)
        game?.let {
            val frames = it.frames.toMutableList()
            val frameEntity = FrameEntity(
                frameId = frame.id,
                player = frame.player,
                number = frame.number,
                throwOne = frame.pins.first(),
                throwTwo = frame.pins.last(),
                bonusThrow = frame.bonusThrow,
                secondBonusThrow = frame.secondBonusThrow,
            )
            frames.add(frame.id)
            frameDao.insertFrame(frameEntity)
            gameDao.addFrame(gameId, frames.toList())
        }
    }

    suspend fun finishGame(gameId: String) = withContext(Dispatchers.IO) {
        gameDao.finishGame(gameId, "true")
    }

    suspend fun getGameById(gameId: String): GameEntity? {
        return gameDao.getGameData(gameId)
    }

    suspend fun getFrameById(frameId: String): FrameEntity {
        return frameDao.getFrame(frameId)
    }

    suspend fun updateFrameTen(frameId: String, bonusThrow: String) {
        frameDao.updateFrameBonusThrow(frameId, bonusThrow)
    }
}