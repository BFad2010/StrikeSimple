package com.corp.strikesimple

import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.corp.strikesimple.ui.compose.AppLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppLoadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            exitTransition = Explode()
            exitTransition.duration = 2000
        }
        lifecycleScope.launch(Dispatchers.IO) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                delay(2000)
                val mainIntent = Intent(this@AppLoadingActivity, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }
        setContent {
            SplashScreenView()
        }
    }
}

@Composable
fun SplashScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "StrikeSimple",
            fontSize = 48.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        AppLoading()
    }
}