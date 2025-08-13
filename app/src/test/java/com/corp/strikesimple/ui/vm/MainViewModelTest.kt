package com.corp.strikesimple.ui.vm

import app.cash.turbine.test
import com.corp.data.db.GameEntity
import com.corp.data.model.Game
import com.corp.data.service.GamesRepo
import com.corp.strikesimple.util.GameUtil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private var repo: GamesRepo = mockk()
    private var gameUtil: GameUtil = mockk()
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(repo, gameUtil)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `given a game exists when checkforlatestgame then update uiState with Content`() = runTest {
        coEvery { repo.getAllGames() } returns listOf(mockGameEntity)
        val expectedState = UiState.CONTENT(mockGameEntity.gameId)
        viewModel.uiState.test {
            assert(awaitItem() is UiState.EMPTY)

            viewModel.checkForLatestGame()

            assert(awaitItem() is UiState.LOADING)
            coVerify { repo.getAllGames() }
            assert(awaitItem().equals(expectedState))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given no game exists when checkforlatestgame then update uiState with EMPTY`() = runTest {
        coEvery { repo.getAllGames() } returns listOf()
        viewModel.uiState.test {
            assert(awaitItem() is UiState.EMPTY)

            viewModel.checkForLatestGame()

            assert(awaitItem() is UiState.LOADING)
            coVerify { repo.getAllGames() }
            assert(awaitItem() is UiState.EMPTY)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given an exception when checkforlatestgame then update uiState with EMPTY`() = runTest {
        coEvery { repo.getAllGames() } throws Exception("Error")
        viewModel.uiState.test {
            assert(awaitItem() is UiState.EMPTY)

            viewModel.checkForLatestGame()

            assert(awaitItem() is UiState.LOADING)
            coVerify { repo.getAllGames() }
            assert(awaitItem() is UiState.EMPTY)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given a set of players when startNewGame then insert game AND update uiState with CONTENT`() =
        runTest {
            every { gameUtil.generateGameId() } returns "gameId"
            val players = listOf("Dumb", "Dumber")
            val newGame = Game(
                id = "gameId",
                players = players,
                frames = emptyList(),
                bonusThrow = null,
                isComplete = false,
            )
            coEvery { repo.insertNewGame(any()) } just runs
            val expectedState = UiState.CONTENT("gameId")

            viewModel.uiState.test {
                assert(awaitItem() is UiState.EMPTY)

                viewModel.startNewGame(players)

                coVerify { repo.insertNewGame(newGame) }
                assert(awaitItem().equals(expectedState))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given an exception from repo when startNewGame then update uiState with ERROR`() =
        runTest {
            every { gameUtil.generateGameId() } returns "gameId"
            val players = listOf("Dumb", "Dumber")
            val newGame = Game(
                id = "gameId",
                players = players,
                frames = emptyList(),
                bonusThrow = null,
                isComplete = false,
            )
            coEvery { repo.insertNewGame(any()) } throws Exception("Error")

            viewModel.uiState.test {
                assert(awaitItem() is UiState.EMPTY)

                viewModel.startNewGame(players)

                coVerify { repo.insertNewGame(newGame) }
                assert(awaitItem() is UiState.ERROR)
                cancelAndIgnoreRemainingEvents()
            }
        }

    companion object {
        private val mockGameEntity = GameEntity(
            gameId = "GameId",
            players = listOf("Dumb, Dumber"),
            frames = listOf(
                "dumb_frame_1",
            ),
            bonusThrow = null,
            isComplete = "false"
        )
    }
}