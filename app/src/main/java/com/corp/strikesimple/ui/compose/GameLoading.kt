package com.corp.strikesimple.ui.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corp.strikesimple.R
import kotlinx.coroutines.delay

@Composable
fun GameLoading() {
    val rotation = remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            animate(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = tween(1000, easing = LinearEasing)
            ) { value: Float, _: Float ->
                rotation.value = value
            }
            delay(150)
        }
    }
    Dialog(
        onDismissRequest = {}
    ) {
        Image(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    rotationZ = rotation.value
                },
            painter = painterResource(R.drawable.bowling_ball),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "",
        )
    }
}