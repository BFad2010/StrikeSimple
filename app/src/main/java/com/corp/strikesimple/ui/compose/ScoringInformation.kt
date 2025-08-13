package com.corp.strikesimple.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corp.strikesimple.R

@Composable
fun ScoringInformation() {
    val expandScoringInfo = remember { mutableStateOf(false) }
    Column(
        Modifier
            .background(color = Color.LightGray)
            .padding(top = 4.dp, bottom = 8.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = stringResource(R.string.scoring_rules),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Column {
                Text(
                    text = "Generally, 1 point per pin knocked down.",
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                )
                AnimatedVisibility(
                    visible = !expandScoringInfo.value,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                            .clickable { expandScoringInfo.value = true },
                        text = stringResource(R.string.view_more_info),
                        textDecoration = TextDecoration.Underline,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                    )
                }
                AnimatedVisibility(
                    visible = expandScoringInfo.value,
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Strike - Score of 10 (for knocking down all ten pins), plus the total of the next two rolls \n",
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = "Spare - Score of 10, plus the total number of pins knocked down on the next roll only \n",
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = "Each frame displays the cumulative score up to that point for all complete frames. If a frame has a strike or spare, the score for that frame is not displayed until sufficient subsequent rolls have been input \n",
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}