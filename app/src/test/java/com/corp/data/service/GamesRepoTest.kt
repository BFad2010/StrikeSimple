package com.corp.data.service

import com.corp.data.db.FrameDao
import com.corp.data.db.FrameEntity
import com.corp.data.db.GameDao
import com.corp.data.db.GameEntity
import com.corp.data.model.Frame
import com.corp.data.model.Game
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GamesRepoTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: GamesRepo
    private val gameDao: GameDao = mockk()
    private val frameDao: FrameDao = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = GamesRepo(gameDao, frameDao)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `given getAllGames then return games from gamesDao`() = runTest {
        val mockGames = listOf(mockGameEntity)
        coEvery { gameDao.getAllGames() } returns mockGames

        val allGames = repo.getAllGames()

        assert(allGames == mockGames)
        coVerify { gameDao.getAllGames() }
    }

    @Test
    fun `given a new game when insertNewGame then insert game and its frames`() = runTest {
        coEvery { gameDao.insertGame(any()) } just runs
        coEvery { frameDao.insertFrames(any()) } just runs

        repo.insertNewGame(mockGame)

        coVerify { gameDao.insertGame(mockGameEntity) }
        coVerify { frameDao.insertFrames(listOf(mockFrameEntity)) }
    }

    @Test
    fun `given a new frame when addFrame then should insert frame and update game`() = runTest {
        coEvery { repo.getGameById("gameId") } returns mockGameEntity
        coEvery { frameDao.insertFrame(any()) } just runs
        coEvery { gameDao.addFrame(any(), any()) } just runs

        repo.addFrame("gameId", mockFrame)

        coVerify { frameDao.insertFrame(mockFrameEntity) }
        coVerify { gameDao.addFrame("gameId", listOf(mockFrame.id, mockFrame.id)) }
    }

    @Test
    fun `given isComplete when finishGame then update game in dao`() = runTest {
        coEvery { gameDao.finishGame(any(), any()) } just runs

        repo.finishGame("gameId")

        coVerify { gameDao.finishGame("gameId", "true") }
    }

    @Test
    fun `given a game ID when getGameById then return gameEntity`() = runTest {
        coEvery { gameDao.getGameData(any()) } returns mockGameEntity

        val gameEntity = repo.getGameById(mockGame.id)

        assert(gameEntity == mockGameEntity)
        coVerify { gameDao.getGameData(mockGame.id) }
    }

    @Test
    fun `given a frame when getFrameById then return frame entity`() = runTest {
        coEvery { frameDao.getFrame(any()) } returns mockFrameEntity

        val frameEntity = repo.getFrameById(mockFrame.id)

        assert(frameEntity == mockFrameEntity)
        coVerify { frameDao.getFrame(mockFrame.id) }
    }

    @Test
    fun `given a bonus throw when updateFrameTen then update bonus throw in frame`() = runTest {
        coEvery { frameDao.updateFrameBonusThrow(any(), any()) } just runs

        repo.updateFrameTen(mockFrame.id, "10")

        coVerify { frameDao.updateFrameBonusThrow(mockFrame.id, "10") }
    }

    companion object {
        private val mockFrameEntity = FrameEntity(
            frameId = "dumb_frame_1",
            number = "1",
            player = "Dumb",
            throwOne = 5,
            throwTwo = 5,
            bonusThrow = null,
            secondBonusThrow = null,
        )
        private val mockGameEntity = GameEntity(
            gameId = "GameId",
            players = listOf("Dumb", "Dumber"),
            frames = listOf(
                "dumb_frame_1",
            ),
            bonusThrow = 0,
            isComplete = "false"
        )
        private val mockFrame = Frame(
            id = "dumb_frame_1",
            number = "1",
            player = "Dumb",
            pins = listOf(5, 5),
            bonusThrow = null,
            secondBonusThrow = null,
        )
        private val mockGame = Game(
            id = "GameId",
            players = listOf("Dumb", "Dumber"),
            frames = listOf(mockFrame),
            bonusThrow = 0,
            isComplete = false,
        )
    }
}