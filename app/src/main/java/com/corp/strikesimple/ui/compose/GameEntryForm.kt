package com.corp.strikesimple.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corp.strikesimple.R

@Composable
fun GameEntryForm(
    innerPadding: PaddingValues,
    onBeginGame: (players: List<String>) -> Unit,
    onBack: () -> Unit,
) {
    val playerOne = remember { mutableStateOf("") }
    val playerTwo = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(innerPadding)) {
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
                contentDescription = "Back"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                text = "Who will be playing today?",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 4.dp)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            text = "Enter Player Names",
            textAlign = TextAlign.Start,
            fontSize = 20.sp,
        )
        OutlinedTextField(
            value = playerOne.value,
            onValueChange = {
                playerOne.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            label = {
                Text(text = "Player Name")
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            maxLines = 1,
        )
        OutlinedTextField(
            value = playerTwo.value,
            onValueChange = {
                playerTwo.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            label = {
                Text(text = "Player Name")
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            maxLines = 1,
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            enabled = playerOne.value.isNotEmpty() && playerTwo.value.isNotEmpty(),
            onClick = {
                val players = listOf(playerOne.value, playerTwo.value)
                onBeginGame(players)
            },
        ) {
            Text(text = stringResource(R.string.begin_game))
        }
    }
}