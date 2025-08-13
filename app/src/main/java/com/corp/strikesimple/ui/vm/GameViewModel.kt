package com.corp.strikesimple.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corp.data.model.Final
import com.corp.data.model.Frame
import com.corp.data.model.Game
import com.corp.data.service.GamesRepo
import com.corp.strikesimple.util.GameUtil
import com.corp.strikesimple.util.ScoreMapperUtil.Companion.determineTotalScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    val repo: GamesRepo,
    val gameUtil: GameUtil,
) : ViewModel() {
    private val _gameUiState = MutableStateFlow<GameUiState>(GameUiState.LOADING)
    val gameUiState: StateFlow<GameUiState> = _gameUiState.asStateFlow()

    fun fetchGameProgress(gameId: String) = viewModelScope.launch(Dispatchers.IO) {
        _gameUiState.update { GameUiState.LOADING }
        delay(250)
        try {
            val gameEntity = repo.getGameById(gameId)
            val frames = gameEntity?.frames?.map { frame ->
                repo.getFrameById(frame).let {
                    Frame(
                        id = it.frameId,
                        number = it.number,
                        player = it.player,
                        pins = listOf(it.throwOne, it.throwTwo),
                        bonusThrow = it.bonusThrow,
                        secondBonusThrow = it.secondBonusThrow,
                    )
                }
            }
            gameEntity?.let { entity ->
                val bonusThrow = if (!frames.isNullOrEmpty()) frames.last().bonusThrow ?: 0 else 0
                val game = Game(
                    id = gameId,
                    players = entity.players,
                    frames = frames.orEmpty(),
                    bonusThrow = bonusThrow,
                    isComplete = entity.isComplete.toBoolean(),
                )
                if (game.isComplete) {
                    val final = buildFinalStats(game)
                    _gameUiState.update { GameUiState.COMPLETED(final) }
                } else {
                    _gameUiState.update { GameUiState.IN_PROGRESS(game) }
                }
            } ?: _gameUiState.update { GameUiState.ERROR }
        } catch (e: Exception) {
            _gameUiState.update { GameUiState.ERROR }
        }
    }

    suspend fun addNewFrame(
        gameId: String,
        player: String,
        frameNumber: Int,
        throwOne: Int,
        throwTwo: Int,
        bonusThrow: Int? = null,
        secondBonusThrow: Int? = null,
    ) = viewModelScope.launch(Dispatchers.IO) {
        val frameId = gameUtil.generateGameId()
        repo.addFrame(
            gameId = gameId,
            frame = Frame(
                id = frameId,
                number = frameNumber.toString(),
                player = player,
                pins = listOf(throwOne, throwTwo),
                bonusThrow = bonusThrow,
                secondBonusThrow = secondBonusThrow,
            )
        )
    }

    suspend fun finishGame(game: Game) = viewModelScope.launch(Dispatchers.IO) {
        _gameUiState.update { GameUiState.LOADING }
        try {
            addMissingFrames(game)
            val final = buildFinalStats(game)
            repo.finishGame(game.id)
            _gameUiState.update { GameUiState.COMPLETED(final) }
        } catch (e: Exception) {
            _gameUiState.update { GameUiState.ERROR }
        }
    }

    suspend fun updateFrameWithBonus(gameId: String, frameId: String, bonusThrow: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFrameTen(frameId, bonusThrow.toString())
            fetchGameProgress(gameId)
        }

    private suspend fun addMissingFrames(game: Game) = viewModelScope.launch(Dispatchers.IO) {
        val totalFrames = 10
        game.players.map { player ->
            val frames = game.frames.filter { it.player == player }
            val framesLeft = (totalFrames - frames.size)
            if (framesLeft > 0) {
                for (frame in 1..framesLeft) {
                    addNewFrame(
                        gameId = game.id,
                        player = player,
                        frameNumber = frames.size + frame,
                        throwOne = 0,
                        throwTwo = 0,
                        bonusThrow = null,
                    )
                }
            }
        }
    }

    private fun buildFinalStats(game: Game): Final {
        val playerScores = game.players.map { player ->
            val frames = game.frames.filter { it.player == player }
            player to frames.determineTotalScore()
        }
        val winnerScore = playerScores.maxOf { it.second }
        val winnerPlayer = playerScores.find { it.second == winnerScore }?.first ?: "No Name"
        return Final(
            id = game.id,
            winner = winnerPlayer,
            score = winnerScore,
        )
    }
}

sealed class GameUiState {
    data object LOADING : GameUiState()
    data class IN_PROGRESS(val game: Game) : GameUiState()
    data class COMPLETED(val final: Final) : GameUiState()
    data object ERROR : GameUiState()
}