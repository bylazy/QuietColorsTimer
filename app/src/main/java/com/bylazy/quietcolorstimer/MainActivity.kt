package com.bylazy.quietcolorstimer

import android.os.Bundle
import android.view.WindowManager
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
import com.bylazy.quietcolorstimer.data.IntervalType
import com.bylazy.quietcolorstimer.ui.screens.*
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme

class MainActivity : ComponentActivity() {

    var defaultBrightness: Float = -1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultBrightness = window.attributes.screenBrightness
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
                        composable("start/{id}",
                            arguments = listOf(navArgument("id"){type = NavType.IntType})){
                            val timerViewModel = viewModel<TimerViewModel>()
                            TimerScreen(viewModel = timerViewModel,
                                navController = navController,
                                keepScreenOn = { keepScreenOn(it) },
                                adjustBrightness = { adjustBrightness(it) })
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val windowAttributes = window.attributes
        windowAttributes.screenBrightness = defaultBrightness
        window.attributes = windowAttributes
    }

    private fun keepScreenOn(keep: Boolean){
        if (keep) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun adjustBrightness(type: IntervalType){
        when (type) {
            IntervalType.BRIGHT -> {
                val windowAttributes = window.attributes
                windowAttributes.screenBrightness = 1f
                window.attributes = windowAttributes
            }
            IntervalType.DARK -> {
                val windowAttributes = window.attributes
                windowAttributes.screenBrightness = 0.2f //TODO value
                window.attributes = windowAttributes
            }
            else -> {
                val windowAttributes = window.attributes
                windowAttributes.screenBrightness = defaultBrightness
                window.attributes = windowAttributes
            }
        }
    }
}

