package com.corp.strikesimple.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.corp.strikesimple.R

@Composable
fun AppLoading() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bowler))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = true,
        restartOnPlay = false,
        iterations = 1,
        speed = 1.5f,
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
    )
}