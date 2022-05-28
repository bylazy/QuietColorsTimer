package com.bylazy.quietcolorstimer.data

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval

const val MAX_INTERVAL_NAME_LENGTH = 12
const val MAX_TIMER_NAME_LENGTH = 25
const val MIN_INTERVAL_DURATION = 3
const val MAX_INTERVAL_DURATION = 600

const val NEW_TIMER_NAME = "New Timer"

const val NEW_INTERVAL_NAME = "Interval"

val NEW_TIMER = InTimer(name = NEW_TIMER_NAME,
    description = "Add description",
    link = "",
    pinned = false,
    type = TimerType.OTHER)

val NEW_INTERVAL = Interval(timerId = 0,
    position = 0,
    name = NEW_INTERVAL_NAME,
    duration = 30,
    color = Color.Green.string(),
    signal = IntervalSignal.SILENT,
    sound = IntervalSound.KNUCKLE,
    customSoundUri = "",
    type = IntervalType.DEFAULT)

val COOLDOWN_INTERVAL = Interval(timerId = 0,
    position = -1,
    name = "Get Ready!",
    duration = 5,
    color = Color.Black.string(),
    signal = IntervalSignal.SILENT,
    sound = IntervalSound.KNUCKLE,
    customSoundUri = "",
    type = IntervalType.DEFAULT)

val FINISH_INTERVAL = Interval(timerId = 0,
    position = -1,
    name = "Done!",
    duration = 1,
    color = Color.Black.string(),
    signal = IntervalSignal.SILENT,
    sound = IntervalSound.KNUCKLE,
    customSoundUri = "",
    type = IntervalType.DEFAULT)


enum class IntervalType {DEFAULT, BRIGHT, DARK, OFF}

val intervalTypeList = mapOf(
    IntervalType.DEFAULT to Pair("Default", R.drawable.ic_int_type_default),
    IntervalType.BRIGHT to Pair("Bright", R.drawable.ic_int_type_bright),
    IntervalType.DARK to Pair("Dark", R.drawable.ic_int_type_dark)
)

enum class TimerType {WORKOUT, YOGA, COOK, OTHER}

val timerTypeList = mapOf(
    TimerType.WORKOUT to Pair("Workout", R.drawable.ic_big_workout),
    TimerType.YOGA to Pair("Yoga/Relax", R.drawable.ic_big_yoga),
    TimerType.COOK to Pair("Cook", R.drawable.ic_big_cook),
    TimerType.OTHER to Pair("Common", R.drawable.ic_big_common)
)

enum class IntervalSignal {SILENT, SOUND, VIBRATION, SOUND_START}

val intervalSignalList = mapOf(
    IntervalSignal.SILENT to Pair("Silent", R.drawable.ic_int_sound_silent),
    IntervalSignal.SOUND to Pair("Sound", R.drawable.ic_int_sound_full),
    IntervalSignal.VIBRATION to Pair("Vibrate", R.drawable.ic_int_sound_vibro),
    IntervalSignal.SOUND_START to Pair("Start sound", R.drawable.ic_int_sound_start)
)

enum class IntervalSound(val i: Int) {
    CUSTOM(0),
    ALLEYESONME(R.raw.s_alleyesonme_ok),
    BELL(R.raw.s_bell_ok),
    CLEARLY(R.raw.s_clearly_ok),
    CROAK(R.raw.s_croak_ok),
    EXQUISITE(R.raw.s_exquisite_ok),
    FORSURE(R.raw.s_forsure_ok),
    HOLLOW(R.raw.s_hollow_ok),
    INTUITION(R.raw.s_intuition_ok),
    JUNTOS(R.raw.s_juntos_ok),
    KNOB(R.raw.s_knob_ok),
    KNOCK(R.raw.s_knock_ok),
    KNUCKLE(R.raw.s_knuckle_ok),
    LIGHT(R.raw.s_light_ok),
    METOO(R.raw.s_metoo_ok),
    NAILED(R.raw.s_nailed_ok),
    PERCUSSION(R.raw.s_percussion_ok),
    QUICK(R.raw.s_quick_ok),
    QUIETKNOCK(R.raw.s_quietknock_ok),
    SERVED(R.raw.s_served_ok),
    SUPPRESSED(R.raw.s_suppressed_ok),
    TICK(R.raw.s_tick_ok),
    WET(R.raw.s_wet_ok),
    WHEN(R.raw.s_when_ok),
    YOUKNOW(R.raw.s_youknow_ok)
}

val soundsList = mapOf(
    IntervalSound.CUSTOM to Pair("Custom", 0),
    IntervalSound.ALLEYESONME to Pair("On me", R.raw.s_alleyesonme_ok),
    IntervalSound.BELL to Pair("Bell", R.raw.s_bell_ok),
    IntervalSound.CLEARLY to Pair("Clearly", R.raw.s_clearly_ok),
    IntervalSound.CROAK to Pair("Croak", R.raw.s_croak_ok),
    IntervalSound.EXQUISITE to Pair("Exquisite", R.raw.s_exquisite_ok),
    IntervalSound.FORSURE to Pair("For sure", R.raw.s_forsure_ok),
    IntervalSound.HOLLOW to Pair("Hollow", R.raw.s_hollow_ok),
    IntervalSound.INTUITION to Pair("Intuition", R.raw.s_intuition_ok),
    IntervalSound.JUNTOS to Pair("Juntos", R.raw.s_juntos_ok),
    IntervalSound.KNOB to Pair("Knob", R.raw.s_knob_ok),
    IntervalSound.KNOCK to Pair("Knock", R.raw.s_knock_ok),
    IntervalSound.KNUCKLE to Pair("Knuckle", R.raw.s_knuckle_ok),
    IntervalSound.LIGHT to Pair("Light", R.raw.s_light_ok),
    IntervalSound.METOO to Pair("Me too", R.raw.s_metoo_ok),
    IntervalSound.NAILED to Pair("Nailed", R.raw.s_nailed_ok),
    IntervalSound.PERCUSSION to Pair("Percussion", R.raw.s_percussion_ok),
    IntervalSound.QUICK to Pair("Quick", R.raw.s_quick_ok),
    IntervalSound.QUIETKNOCK to Pair("Quiet knock", R.raw.s_quietknock_ok),
    IntervalSound.SERVED to Pair("Served", R.raw.s_served_ok),
    IntervalSound.SUPPRESSED to Pair("Suppressed", R.raw.s_suppressed_ok),
    IntervalSound.TICK to Pair("Tick", R.raw.s_tick_ok),
    IntervalSound.WET to Pair("Wet", R.raw.s_wet_ok),
    IntervalSound.WHEN to Pair("When", R.raw.s_when_ok),
    IntervalSound.YOUKNOW to Pair("You know", R.raw.s_youknow_ok)
)

fun Color.string() = this.value.toString()

fun String.color(): Color {
    val uLongValue = this.toULongOrNull()
    var color = Color.Transparent
    uLongValue?.let { color = Color(it) }
    return color
}

fun Color.onColor(): Color {
    return if (this.luminance() >= 0.5f) Color.Black else Color.White
}

//Default palette
val colors = listOf(Color.Green,
    Color(0xFF3D550C),
    Color(0xFF81B622),
    Color(0xFFECF87F),
    Color(0xFF59981A),
    Color(0xFF8FA01F),
    Color(0xFF8BCD50),
    Color.Red,
    Color(0xFFFFC5D0),
    Color(0xFFF7D6D0),
    Color(0xFFFB6090),
    Color(0xFF821D30),
    Color(0xFFCC5216),
    Color(0xFF910C00),
    Color.Blue,
    Color(0xFF41729F),
    Color(0xFF5885AF),
    Color(0xFF274472),
    Color(0xFFC3E0E5),
    Color.Yellow,
    Color(0xFFFEDE00),
    Color(0xFFFBB80F),
    Color(0xFFFBEE0F),
    Color.Cyan,
    Color(0xFF34DED0),
    Color(0xFF94FAF0),
    Color(0xFF3AF7F0),
    Color(0xFF31D1D0),
    Color.Magenta,
    Color(0xFFB22A80),
    Color(0xFFFF8EF9),
    Color(0xFFE930C0),
    Color(0xFFA91B60),
    Color(0xFF7E1E80),
    Color(0xFF9505E3),
    Color(0xFFB637FB),
    Color.White,
    Color.LightGray,
    Color.DarkGray,
    Color.Black
)

data class Event(val interval: String,
                 val next: String,
                 val timer: String,
                 val type: IntervalType,
                 val sound: IntervalSignal,
                 val soundUri: Uri,
                 val duration: Int,
                 val overallDuration: Int,
                 val currentSecondsLeft: Int,
                 val overallSeconds: Int,
                 val currentProgress: Float,
                 val overallProgress: Float,
                 val color: Color)

const val resPath = "android.resource://com.bylazy.quietcolorstimer/"

val START_EVENT = Event(interval = "Get Ready!",
    next = "",
    timer = "",
    type = IntervalType.DEFAULT,
    sound = IntervalSignal.SILENT,
    soundUri = Uri.parse(resPath + R.raw.s_knuckle_ok),
    duration = 5,
    overallDuration = 5,
    currentSecondsLeft = 5,
    overallSeconds = 0,
    currentProgress = 0f,
    overallProgress = 0f,
    color = Color.Black)

fun Color.shaded(): Color {
    return if (this.luminance()<=0.5)
        Color((this.red+0.15F).coerceAtMost(1F),
            (this.green+0.15F).coerceAtMost(1F),
            (this.blue+0.15F).coerceAtMost(1F))
    else
        Color((this.red-0.15F).coerceAtLeast(0F),
            (this.green-0.15F).coerceAtLeast(0F),
            (this.blue-0.15F).coerceAtLeast(0F))
}

fun Int.durationText() = if (this < 100) this.toString().padStart(2, '0')
    else (this / 60).toString()
    .padStart(2, '0')+":"+(this % 60).toString().padStart(2, '0')