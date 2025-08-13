package com.corp.strikesimple.ui.compose

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.corp.data.model.Game
import com.corp.strikesimple.R
import com.corp.strikesimple.ui.vm.GameUiState
import com.corp.strikesimple.ui.vm.GameViewModel
import com.corp.strikesimple.util.GameUtil.Companion.onTenthFrame
import com.corp.strikesimple.util.ScoreMapperUtil.Companion.determineScores
import com.corp.strikesimple.util.ScoreMapperUtil.Companion.formatThrows
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    gameId: String,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val gameUiState = viewModel.gameUiState.collectAsStateWithLifecycle()
    val showAddFrameDialog = remember { mutableStateOf(false) }
    val showBonusFrameDialog = remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.fetchGameProgress(gameId)
        }
    }

    Dialog(
        onDismissRequest = { onBack() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .background(color = Color.DarkGray)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .padding(start = 8.dp)
                            .clickable { onBack() },
                        painter = painterResource(R.drawable.back_arrow),
                        tint = Color.White,
                        contentDescription = stringResource(R.string.back)
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        text = stringResource(R.string.game),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                }
                when (val gameState = gameUiState.value) {
                    is GameUiState.COMPLETED -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        GameCompleteView(
                            final = gameState.final,
                            onViewGame = {},
                            onStartNewGame = {},
                            isFromDetails = true,
                        )
                    }

                    is GameUiState.IN_PROGRESS -> {
                        AnimatedVisibility(
                            visible = showAddFrameDialog.value,
                        ) {
                            AddFrameDialog(
                                viewModel = viewModel,
                                gameState = gameState,
                                onDismiss = {
                                    showAddFrameDialog.value = false
                                },
                                isBonusThrow = false,
                            )
                        }
                        AnimatedVisibility(
                            visible = showBonusFrameDialog.value,
                        ) {
                            AddFrameDialog(
                                viewModel = viewModel,
                                gameState = gameState,
                                onDismiss = {
                                    showBonusFrameDialog.value = false
                                },
                                isBonusThrow = true,
                            )
                        }
                        GameInProgress(
                            gameState.game,
                            onAddFrameClick = {
                                showAddFrameDialog.value = true
                            },
                            onAddBonusFrameClick = {
                                showBonusFrameDialog.value = true
                            },
                            onFinishGame = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.finishGame(gameState.game)
                                }
                                onBack()
                            }
                        )
                    }

                    GameUiState.LOADING -> GameLoading()
                    GameUiState.ERROR -> Toast.makeText(
                        LocalContext.current,
                        "Error Loading Game.. Please Try Again.",
                        LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
private fun GameInProgress(
    game: Game,
    onAddFrameClick: () -> Unit,
    onAddBonusFrameClick: () -> Unit,
    onFinishGame: () -> Unit,
) {
    val gameTotalFrames = 10
    val showFinishGameDialog = remember { mutableStateOf(false) }
    AnimatedVisibility(
        visible = showFinishGameDialog.value,
    ) {
        Dialog(onDismissRequest = { showFinishGameDialog.value = false }) {
            Column(
                modifier = Modifier
                    .background(color = Color.Gray)
                    .padding(top = 8.dp, bottom = 16.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black,
                    text = stringResource(R.string.are_you_sure),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp)
                            .padding(horizontal = 24.dp),
                        onClick = {
                            onFinishGame()
                            showFinishGameDialog.value = false
                        },
                    ) {
                        Text(text = stringResource(R.string.yes))
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp)
                            .padding(horizontal = 24.dp),
                        onClick = { showFinishGameDialog.value = false },
                        border = BorderStroke(1.dp, Color.Blue),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = stringResource(R.string.no),
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
            ) {
                Text(
                    modifier = Modifier
                        .weight(2f), text = "Player Name"
                )
                for (frame in 1..gameTotalFrames) {
                    Text(
                        modifier = Modifier
                            .border(1.dp, Color.Black)
                            .weight(1f), text = "$frame"
                    )
                }
            }
        }
        items(game.players) { player ->
            val frames = game.frames.filter { it.player == player }
            var frameScores = frames.determineScores()
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.LightGray)
                ) {
                    Text(
                        modifier = Modifier
                            .weight(2f), text = player
                    )
                    for (frame in 1..gameTotalFrames) {
                        val throws = if (frame <= frames.size) {
                            val currentFrame = frames[frame - 1]
                            currentFrame.formatThrows()
                        } else Triple("", "", null)

                        Box(modifier = Modifier.weight(1f)) {
                            Row {
                                Text(modifier = Modifier.weight(0.3f), text = throws.first)
                                Text(modifier = Modifier.weight(0.3f), text = throws.second)
                                if (frame == 10) {
                                    Text(
                                        modifier = Modifier.weight(0.3f),
                                        text = throws.third ?: ""
                                    )
                                }
                                VerticalDivider(
                                    modifier = Modifier.height(16.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                Row {
                    Box(modifier = Modifier.weight(2f))
                    for (frame in 1..gameTotalFrames) {
                        val frameScore = when {
                            frame <= frames.size -> frameScores[frame - 1]
                            else -> "-"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(color = Color.LightGray)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = if (frame == 10) {
                                            2.dp
                                        } else -1.dp,
                                        color = Color.Blue,
                                    ),
                                text = frameScore,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color.Black)
            }
        }
        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
                onClick = { onAddFrameClick() },
            ) {
                Text(text = stringResource(R.string.add_frame))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
                onClick = { showFinishGameDialog.value = true },
                border = BorderStroke(1.dp, Color.Blue),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = stringResource(R.string.finish_game),
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold
                )
            }
            AnimatedVisibility(
                visible = game.frames.onTenthFrame(),
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(horizontal = 24.dp),
                    onClick = { onAddBonusFrameClick() },
                    border = BorderStroke(1.dp, Color.Blue),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = stringResource(R.string.add_bonus_throw),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AddFrameDialog(
    viewModel: GameViewModel,
    gameState: GameUiState.IN_PROGRESS,
    onDismiss: () -> Unit,
    isBonusThrow: Boolean = false,
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        val firstThrow = remember { mutableStateOf("0") }
        val secondThrow = remember { mutableStateOf("0") }
        val expanded = remember { mutableStateOf(false) }
        val selectedPlayer =
            remember { mutableStateOf(gameState.game.players.first()) }
        Column(modifier = Modifier.background(Color.LightGray)) {
            Row(
                modifier = Modifier
                    .background(Color.Gray)
                    .padding(start = 4.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text(modifier = Modifier.weight(3f), text = stringResource(R.string.add_new_frame))
                Image(
                    modifier = Modifier
                        .weight(1f)
                        .size(24.dp)
                        .clickable { onDismiss() },
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close"
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
            ) {
                OutlinedButton(
                    onClick = { expanded.value = true }
                ) {
                    Text(selectedPlayer.value.ifEmpty { gameState.game.players.first() })
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    gameState.game.players.forEach { player ->
                        DropdownMenuItem(
                            text = { Text(player) },
                            onClick = {
                                selectedPlayer.value = player
                                expanded.value = false
                            }
                        )
                    }
                }
            }

            Row {
                OutlinedTextField(
                    value = firstThrow.value,
                    onValueChange = {
                        if (it.isNotEmpty()) {
                            firstThrow.value = when (it.toInt()) {
                                in 0..10 -> it
                                else -> "0"
                            }
                        } else firstThrow.value = ""
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    label = {
                        Text(text = "Pins")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions.Default,
                    maxLines = 1,
                )
                if (!isBonusThrow) {
                    OutlinedTextField(
                        value = secondThrow.value,
                        onValueChange = {
                            if (it.isNotEmpty()) {
                                secondThrow.value = when (it.toInt()) {
                                    in 0..10 -> it
                                    else -> "0"
                                }
                            } else secondThrow.value = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        label = {
                            Text(text = stringResource(R.string.pins))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions.Default,
                        maxLines = 1,
                    )
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
                enabled = firstThrow.value.isNotEmpty() && secondThrow.value.isNotEmpty() && (firstThrow.value.toInt() + secondThrow.value.toInt() < 11),
                onClick = {
                    val frameNumber = gameState.game.frames.size + 1
                    CoroutineScope(Dispatchers.IO).launch {
                        if (isBonusThrow) {
                            viewModel.updateFrameWithBonus(
                                gameId = gameState.game.id,
                                frameId = gameState.game.frames.last().id,
                                firstThrow.value.toInt(),
                            )
                        } else {
                            viewModel.addNewFrame(
                                gameId = gameState.game.id,
                                player = selectedPlayer.value,
                                frameNumber = frameNumber,
                                throwOne = firstThrow.value.toInt(),
                                throwTwo = secondThrow.value.toInt(),
                                bonusThrow = null,
                                secondBonusThrow = null,
                            )
                        }
                        viewModel.fetchGameProgress(gameState.game.id)
                        onDismiss()
                    }
                },
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}