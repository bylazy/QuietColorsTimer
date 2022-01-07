package com.bylazy.quietcolorstimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bylazy.quietcolorstimer.ui.screens.HomeScreen
import com.bylazy.quietcolorstimer.ui.screens.HomeViewModel
import com.bylazy.quietcolorstimer.ui.screens.IntervalsScreen
import com.bylazy.quietcolorstimer.ui.screens.IntervalsViewModel
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme

class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homeViewModel by viewModels<HomeViewModel>()
        setContent {
            QuietColorsTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController,
                        startDestination = "home") {
                        composable("home") {
                            HomeScreen(homeViewModel = homeViewModel,
                                navController = navController)
                        }
                        composable("timer/{id}",
                            arguments = listOf(navArgument("id"){type = NavType.IntType})){
                            val intervalsViewModel = viewModel<IntervalsViewModel>()
                            IntervalsScreen(intervalsViewModel = intervalsViewModel,
                                navController = navController)
                        }
                    }
                }
            }
        }
    }
}

