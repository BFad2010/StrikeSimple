package com.corp.strikesimple.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corp.data.model.Game
import com.corp.data.service.GamesRepo
import com.corp.strikesimple.util.GameUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gamesRepo: GamesRepo,
    private val gameUtil: GameUtil,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<UiState>(UiState.EMPTY)
    val uiState: StateFlow<UiState> = mutableUiState.asStateFlow()

    fun checkForLatestGame() = viewModelScope.launch(Dispatchers.IO) {
        mutableUiState.update { UiState.LOADING }
        try {
            val games = gamesRepo.getAllGames()
            if (games.isNotEmpty()) {
                val id = games.last().gameId
                mutableUiState.update { UiState.CONTENT(id) }
            } else {
                mutableUiState.update { UiState.EMPTY }
            }
        } catch (e: Exception) {
            mutableUiState.update { UiState.EMPTY }
        }
    }

    fun startNewGame(players: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        val gameId = gameUtil.generateGameId()
        val newGame = Game(
            id = gameId,
            players = players,
            frames = emptyList(),
            bonusThrow = null,
            isComplete = false,
        )
        try {
            gamesRepo.insertNewGame(newGame)
            mutableUiState.update { UiState.CONTENT(gameId) }
        } catch (e: Exception) {
            mutableUiState.update { UiState.ERROR }
        }
    }
}

sealed class UiState {
    data object LOADING : UiState()
    data class CONTENT(val gameId: String) : UiState()
    data object EMPTY : UiState()
    data object ERROR : UiState()
}