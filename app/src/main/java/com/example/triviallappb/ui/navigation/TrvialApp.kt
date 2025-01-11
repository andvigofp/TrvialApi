package com.example.triviallappb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.triviallappb.ui.screens.GameOverScreen
import com.example.triviallappb.ui.screens.GameScreen
import com.example.triviallappb.ui.screens.HomeScreen

import com.example.triviallappb.ui.state.TrivialViewModel

@Composable
fun TrivialApp(viewModel: TrivialViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TrivialScreen.Home.name
    ) {
        composable(TrivialScreen.Home.name) {
            HomeScreen(
                viewModel = viewModel,
                onStartGame = { amount ->
                    // Navegar a GameScreen con el parámetro amount
                    navController.navigate("${TrivialScreen.Game.name}/${amount}")
                },
                onNavigateToGameOver = {
                    navController.navigate(TrivialScreen.GameOver.name)
                }
            )
        }
        composable(
            route = "${TrivialScreen.Game.name}/{amount}",
            arguments = listOf(navArgument("amount") { type = NavType.IntType })
        ) { backStackEntry ->
            // Recupera el valor de amount de los argumentos
            val amount = backStackEntry.arguments?.getInt("amount") ?: 10
            GameScreen(
                viewModel = viewModel,
                amount = amount,
                onGameOver = {
                    viewModel.updateRecordAndNavigate()
                    navController.navigate(TrivialScreen.GameOver.name)
                }
            )
        }
        composable(TrivialScreen.GameOver.name) {
            GameOverScreen(
                viewModel = viewModel,
                onHome = {
                    viewModel.updateRecordAndNavigate()
                    navController.navigate(TrivialScreen.Home.name) {
                        popUpTo(TrivialScreen.Home.name) { inclusive = true }
                    }
                },
                onReplay = {
                    viewModel.startGame(viewModel.totalQuestions)
                    navController.navigate("${TrivialScreen.Game.name}/${viewModel.totalQuestions}") {
                        popUpTo(TrivialScreen.GameOver.name) { inclusive = true }
                    }
                }
            )
        }
    }
}




