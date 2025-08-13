package com.corp.strikesimple.ui.vm

import app.cash.turbine.test
import com.corp.data.db.FrameEntity
import com.corp.data.db.GameEntity
import com.corp.data.model.Final
import com.corp.data.model.Frame
import com.corp.data.model.Game
import com.corp.data.service.GamesRepo
import com.corp.strikesimple.util.GameUtil
import io.mockk.awaits
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
class GameViewModelTest {
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private var repo: GamesRepo = mockk()
    private var gameUtil: GameUtil = mockk()
    private lateinit var viewModel: GameViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GameViewModel(repo, gameUtil)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `given a game in progress when fetchGameProgress then update gameUiState to IN_PROGRESS `() =
        runTest {
            val expectedFrame = Frame(
                id = "dumb_frame_1",
                number = "1",
                player = "Dumb",
                pins = listOf(5, 5),
                bonusThrow = null,
                secondBonusThrow = null,
            )
            val expectedGame = Game(
                id = "GameId",
                players = listOf("Dumb, Dumber"),
                frames = listOf(expectedFrame),
                bonusThrow = 0,
                isComplete = false,
            )
            val expectedState = GameUiState.IN_PROGRESS(game = expectedGame)

            coEvery { repo.getGameById(any()) } returns mockGameEntity
            coEvery { repo.getFrameById(any()) } returns mockFrameEntity

            viewModel.gameUiState.test {
                assert(awaitItem() is GameUiState.LOADING)
                viewModel.fetchGameProgress(mockGameEntity.gameId)
                val state = awaitItem()
                coVerify { repo.getGameById(mockGameEntity.gameId) }
                coVerify { repo.getFrameById(mockFrameEntity.frameId) }

                assert(state is GameUiState.IN_PROGRESS)
                assert(state.equals(expectedState))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given the last game is COMPLETED when fetchGameProgress then update gameUiState to COMPLETED`() =
        runTest {
            val gameEntity = mockGameEntity.copy(isComplete = "true")
            val final = Final(
                id = gameEntity.gameId,
                winner = "Dumb, Dumber",
                score = 0,
            )

            coEvery { repo.getGameById(any()) } returns gameEntity
            coEvery { repo.getFrameById(any()) } returns mockFrameEntity
            coEvery { repo.addFrame(any(), any()) } just awaits

            viewModel.gameUiState.test {
                assert(awaitItem() is GameUiState.LOADING)

                viewModel.fetchGameProgress(gameEntity.gameId)

                val state = awaitItem()
                coVerify { repo.getGameById(gameEntity.gameId) }
                coVerify { repo.getFrameById(mockFrameEntity.frameId) }
                assert(state is GameUiState.COMPLETED)
                state as GameUiState.COMPLETED
                assert(state.final.id == final.id)
                assert(state.final.winner == final.winner)
                assert(state.final.score == final.score)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given no Game exists for gameId when fetchGameProgress then update gameUiState to ERROR`() =
        runTest {
            coEvery { repo.getGameById(any()) } returns null

            viewModel.gameUiState.test {
                assert(awaitItem() is GameUiState.LOADING)
                viewModel.fetchGameProgress(mockGameEntity.gameId)
                val state = awaitItem()
                coVerify { repo.getGameById(mockGameEntity.gameId) }

                assert(state is GameUiState.ERROR)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given an Error is thrown when fetchGameProgress then update gameUiState to ERROR`() =
        runTest {
            coEvery { repo.getGameById(any()) } throws Exception("Error")

            viewModel.gameUiState.test {
                assert(awaitItem() is GameUiState.LOADING)
                viewModel.fetchGameProgress(mockGameEntity.gameId)
                val state = awaitItem()
                coVerify { repo.getGameById(mockGameEntity.gameId) }

                assert(state is GameUiState.ERROR)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given addNewFrame Then addFrame should be called in repo`() = runTest {
        val expectedFrameId = "${mockGameEntity.gameId}_Dumb_1"
        every { gameUtil.generateGameId() } returns expectedFrameId
        val expectedFrame = Frame(
            id = expectedFrameId,
            number = "1",
            player = "Dumb",
            pins = listOf(5, 5),
            bonusThrow = null,
            secondBonusThrow = null
        )
        coEvery { repo.addFrame(any(), any()) } just awaits

        viewModel.addNewFrame(
            gameId = mockGameEntity.gameId,
            player = "Dumb",
            frameNumber = 1,
            throwOne = 5,
            throwTwo = 5,
            bonusThrow = null,
            secondBonusThrow = null
        )

        coVerify {
            repo.addFrame(
                gameId = mockGameEntity.gameId,
                frame = expectedFrame,
            )
        }
    }

    @Test
    fun `given a game when finishGame then game should be updated in repo AND GameUiState COMPLETED`() =
        runTest {
            val frame = Frame(
                id = "dumb_frame_1",
                number = "1",
                player = "Dumb",
                pins = listOf(5, 5),
                bonusThrow = null,
                secondBonusThrow = null,
            )
            val game = Game(
                id = "GameId",
                players = listOf("Dumb, Dumber"),
                frames = listOf(frame),
                bonusThrow = 0,
                isComplete = false,
            )
            val final = Final(
                id = game.id,
                winner = "Dumb, Dumber",
                score = 0,
            )
            val expectedState = GameUiState.COMPLETED(final)
            every { gameUtil.generateGameId() } returns "gameId"
            coEvery { repo.finishGame(game.id) } just runs
            coEvery { repo.addFrame(any(), any()) } just awaits
            viewModel.gameUiState.test {
                assert(awaitItem() is GameUiState.LOADING)
                viewModel.finishGame(game)

                coVerify { repo.finishGame(game.id) }
                assert(awaitItem().equals(expectedState))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given a game when finishGame throws an exception then GameUiState ERROR`() = runTest {
        val game = Game(
            id = "GameId",
            players = listOf("Dumb, Dumber"),
            frames = listOf(),
            bonusThrow = 0,
            isComplete = false,
        )
        every { gameUtil.generateGameId() } returns "gameId"
        coEvery { repo.addFrame(any(), any()) } just awaits
        coEvery { repo.finishGame(mockGameEntity.gameId) } throws Exception("ERROR")
        viewModel.gameUiState.test {
            assert(awaitItem() is GameUiState.LOADING)
            viewModel.finishGame(game)

            coVerify { repo.finishGame(mockGameEntity.gameId) }
            assert(awaitItem() is GameUiState.ERROR)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given a bonus throw when updateFrameWithBonus then repo is updated`() = runTest {
        val frameId = "GameId_Dumb_1"
        val bonusThrow = 10
        coEvery { repo.updateFrameTen(any(), any()) } just runs

        viewModel.updateFrameWithBonus(mockGameEntity.gameId, frameId, bonusThrow)

        coVerify { repo.updateFrameTen(frameId, "10") }
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
        private val mockFrameEntity = FrameEntity(
            frameId = "dumb_frame_1",
            number = "1",
            player = "Dumb",
            throwOne = 5,
            throwTwo = 5,
            bonusThrow = null,
            secondBonusThrow = null,
        )
    }
}