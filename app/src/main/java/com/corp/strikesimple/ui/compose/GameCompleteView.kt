package com.corp.strikesimple.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corp.data.model.Final
import com.corp.strikesimple.R

@Composable
fun GameCompleteView(
    final: Final,
    onViewGame: (id: String) -> Unit,
    onStartNewGame: () -> Unit,
    isFromDetails: Boolean = false,
) {
    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.game_results),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.wins, final.winner, final.score),
            textAlign = TextAlign.Center,
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        AnimatedVisibility(
            visible = !isFromDetails
        ) {
            Column {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(horizontal = 24.dp),
                    onClick = { onViewGame(final.id) },
                ) {
                    Text(text = stringResource(R.string.view_game))
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .padding(horizontal = 24.dp),
                    onClick = { onStartNewGame() },
                    border = BorderStroke(1.dp, Color.Blue),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = stringResource(R.string.start_new_game),
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}