package com.corp.strikesimple.ui.compose

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.corp.strikesimple.R
import com.corp.strikesimple.ui.navigation.Screen
import com.corp.strikesimple.ui.vm.GameUiState
import com.corp.strikesimple.ui.vm.GameViewModel
import com.corp.strikesimple.ui.vm.MainViewModel
import com.corp.strikesimple.ui.vm.UiState
import com.corp.strikesimple.util.ScoreMapperUtil.Companion.determineTotalScore

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    gameViewModel: GameViewModel,
    navController: NavController,
    innerPaddingValues: PaddingValues,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = mainViewModel.uiState.collectAsStateWithLifecycle()
    val showGameLoading = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            mainViewModel.checkForLatestGame()
        }
    }
    AnimatedVisibility(
        visible = showGameLoading.value
    ) {
        GameLoading()
    }
    Column(
        modifier = Modifier
            .padding(innerPaddingValues)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .background(color = Color.DarkGray)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp),
                painter = painterResource(R.drawable.bowling_ball),
                tint = Color.White,
                contentDescription = "Back"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                text = stringResource(R.string.welcome_to_strikesimple),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val uiState = uiState.value) {
                is UiState.CONTENT -> {
                    showGameLoading.value = false
                    GamePreview(
                        viewModel = gameViewModel,
                        gameId = uiState.gameId,
                        onViewGame = {
                            navController.navigate("${Screen.GameMain.route}/${uiState.gameId}")
                        },
                        onStartNewGame = {
                            navController.navigate(Screen.GameEntry.route)
                        },
                    )
                }

                UiState.EMPTY -> {
                    showGameLoading.value = false
                    GameNotStarted { navController.navigate(Screen.GameEntry.route) }
                }

                UiState.ERROR -> Toast.makeText(
                    LocalContext.current,
                    "Error Loading Game.. Please Try Again.",
                    LENGTH_SHORT
                ).show()

                UiState.LOADING -> showGameLoading.value = true
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            PastGamesCarousel {
                navController.navigate(Screen.PastGames.route)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            ScoringInformation()
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Text(text = "By Brandon Fadley - 2025", fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
fun GameNotStarted(
    onStartNewGame: () -> Unit,
) {
    Column(modifier = Modifier.wrapContentSize()) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 8.dp),
            text = "Current Game",
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Currently No Game in Progress...",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 24.dp),
            onClick = { onStartNewGame() },
        ) {
            Text(text = stringResource(R.string.start_new_game))
        }
    }
}

@Composable
fun GamePreview(
    viewModel: GameViewModel,
    gameId: String,
    onViewGame: (GameUiState) -> Unit,
    onStartNewGame: () -> Unit,
) {
    val gameUiState = viewModel.gameUiState.collectAsStateWithLifecycle()

    LaunchedEffect(gameId) {
        viewModel.fetchGameProgress(gameId)
    }

    Column {
        when (val gameState = gameUiState.value) {
            is GameUiState.COMPLETED -> GameCompleteView(
                final = gameState.final,
                onViewGame = { id -> onViewGame(gameState) },
                onStartNewGame = {
                    onStartNewGame()
                },
            )

            GameUiState.ERROR -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.error_showing_game_preview))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(horizontal = 24.dp),
                    onClick = { onStartNewGame() },
                ) {
                    Text(text = stringResource(R.string.start_new_game))
                }
            }

            is GameUiState.IN_PROGRESS -> {
                Column(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.game_in_progress),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                    Row {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.player),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.score),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.frame),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    gameState.game.players.forEach { player ->
                        val frames = gameState.game.frames.filter { it.player == player }
                        val totalScore =
                            if (frames.isNotEmpty()) frames.determineTotalScore() else 0
                        HorizontalDivider(thickness = 1.dp, color = Color.Black)
                        Row(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = player,
                                fontStyle = FontStyle.Italic
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "$totalScore",
                                fontStyle = FontStyle.Italic
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "${frames.size}",
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(horizontal = 24.dp),
                    onClick = { onViewGame(gameUiState.value) },
                ) {
                    Text(text = stringResource(R.string.view_game))
                }
            }

            GameUiState.LOADING -> CircularProgressIndicator()
        }
    }
}