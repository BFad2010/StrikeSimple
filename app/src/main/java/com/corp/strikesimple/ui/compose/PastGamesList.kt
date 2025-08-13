package com.corp.strikesimple.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corp.strikesimple.R

@Composable
fun PastGamesList(
    innerPadding: PaddingValues,
    onBack: () -> Unit,
) {
    var pastGames = listOf("Game one", "Game Two", "Game Three", "Game Four", "Game Five")
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
                text = "Past Games",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }
        LazyColumn {
            items(pastGames) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                        .clickable { }
                ) {
                    Column(verticalArrangement = Arrangement.Top) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = it,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}