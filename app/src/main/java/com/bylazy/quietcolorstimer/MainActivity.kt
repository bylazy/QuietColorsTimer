package com.bylazy.quietcolorstimer

import android.content.Context
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

    private var defaultBrightness: Float = -1f
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultBrightness = window.attributes.screenBrightness
        mediaPlayer = MediaPlayer.create(this.applicationContext, R.raw.s_forsure_ok) // todo select sound
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vManager = this.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vManager.defaultVibrator
        } else this.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val homeViewModel by viewModels<HomeViewModel>()
        setContent {
            QuietColorsTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController,
                        startDestination = "home") {
                        composable("home") {
                            HomeScreenContent(viewModel = homeViewModel,
                                navController = navController)
                        }
                        composable("timer/{id}",
                            arguments = listOf(navArgument("id"){type = NavType.IntType})){
                            val intervalsViewModel = viewModel<IntervalsViewModel>()
                            IntervalScreen(intervalsViewModel = intervalsViewModel,
                                navController = navController)
                        }
                        composable("start/{id}",
                            arguments = listOf(navArgument("id"){type = NavType.IntType})){
                            val timerViewModel = viewModel<TimerViewModel>()
                            TimerScr(viewModel = timerViewModel,
                                navController = navController,
                                keepScreenOn = { keepScreenOn(it) },
                                restoreBrightness = {restoreBrightness()},
                                adjustBrightness = { adjustBrightness(it) },
                                playSound = {playSound()},
                                vibrate = {vibrate()})
                        }
                    }
                }
            }
        }
    }

    private fun playSound(){
        mediaPlayer.start()
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else vibrator.vibrate(200)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        restoreBrightness()
    }

    private fun restoreBrightness() {
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
                windowAttributes.screenBrightness = 0.1f //TODO value
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

