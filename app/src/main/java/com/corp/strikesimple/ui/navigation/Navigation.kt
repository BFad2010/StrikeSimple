package com.corp.strikesimple.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.corp.strikesimple.R
import com.corp.strikesimple.ui.compose.GameEntryForm
import com.corp.strikesimple.ui.compose.GameScreen
import com.corp.strikesimple.ui.compose.MainScreen
import com.corp.strikesimple.ui.compose.PastGamesList
import com.corp.strikesimple.ui.vm.GameViewModel
import com.corp.strikesimple.ui.vm.MainViewModel

@Composable
fun Navigation(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()
    val gameViewModel: GameViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(
            route = Screen.Home.route,
        ) {
            MainScreen(
                mainViewModel = viewModel,
                gameViewModel = gameViewModel,
                navController = navController,
                innerPaddingValues = innerPadding
            )
        }

        composable(
            route = "${Screen.GameMain.route}/{gameId}",
        ) {
            val gameId = it.arguments?.getString("gameId") ?: ""
            GameScreen(
                viewModel = gameViewModel,
                gameId = gameId,
                innerPadding = innerPadding,
                onBack = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        composable(
            route = Screen.GameEntry.route,
        ) {
            GameEntryForm(
                innerPadding = innerPadding,
                onBeginGame = { players ->
                    viewModel.startNewGame(players)
                    navController.navigate(Screen.Home.route)
                    Toast.makeText(
                        context,
                        context.getString(R.string.new_game_started),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(
            route = Screen.PastGames.route,
        ) {
            PastGamesList(innerPadding) {
                navController.navigateUp()
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("screen_home")
    object GameEntry : Screen("screen_game_entry")
    object GameMain : Screen("screen_game_main")
    object PastGames : Screen("screen_past_games")
}