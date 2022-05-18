package com.bylazy.quietcolorstimer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
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
import com.bylazy.quietcolorstimer.data.IntervalSound
import com.bylazy.quietcolorstimer.data.IntervalType
import com.bylazy.quietcolorstimer.data.resPath
import com.bylazy.quietcolorstimer.ui.screens.*
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme

class MainActivity : ComponentActivity() {



    private var defaultBrightness: Float = -1f
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator
    private var currentSoundUri: Uri = Uri.parse(resPath + IntervalSound.KNUCKLE.i)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultBrightness = window.attributes.screenBrightness
        mediaPlayer = MediaPlayer.create(this.applicationContext, currentSoundUri)
        //mediaPlayer.setOnPreparedListener {  }
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
                                loadSound = ::loadSound,
                                playSound = ::playSound,
                                playOrStop = ::stopSound,
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
                                loadSound = ::loadSound,
                                playSound = ::playSound,
                                stopSound = ::stopSound,
                                vibrate = ::vibrate)
                        }
                    }
                }
            }
        }
    }

    private fun playSound(){
        mediaPlayer.start()
    }

    private fun stopSound() {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
    }

    private fun loadSound(uri: Uri) {
        currentSoundUri = uri
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(this.applicationContext, uri)
            mediaPlayer.prepare()
        } catch (e: Exception) {
            //Log.d("load sound", e.message?:"unknown")
            //todo do something
        }
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

