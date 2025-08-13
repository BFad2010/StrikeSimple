package com.corp.strikesimple.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PastGamesCarousel(
    onViewAll: () -> Unit,
) {
    Column(
        Modifier
            .background(color = Color.Gray)
            .padding(top = 4.dp, bottom = 8.dp)
    ) {
        var pastGames = listOf("Game one", "Game Two", "Game Three", "Game Four", "Game Five")
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                text = "Past Games",
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .clickable { onViewAll() },
                text = "View All >",
                textDecoration = TextDecoration.Underline,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.End,
                fontSize = 16.sp,
            )
        }
        LazyRow(modifier = Modifier.padding(top = 8.dp, start = 8.dp)) {
            items(pastGames) {
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .padding(4.dp)
                        .clickable { },
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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