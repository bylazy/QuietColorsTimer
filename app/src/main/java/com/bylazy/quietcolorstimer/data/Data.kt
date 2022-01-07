package com.bylazy.quietcolorstimer.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval

const val MAX_INTERVAL_NAME_LENGTH = 12

const val MIN_INTERVAL_DURATION = 5
const val MAX_INTERVAL_DURATION = 600

const val NEW_TIMER_NAME = "New Timer"

const val NEW_INTERVAL_NAME = "Interval"

val NEW_TIMER = InTimer(name = NEW_TIMER_NAME,
    description = "...description...",
    pinned = false,
    type = TimerType.OTHER)

val NEW_INTERVAL = Interval(timerId = 0,
    position = 0,
    name = NEW_INTERVAL_NAME,
    duration = 30,
    color = Color.Green.string(),
    type = IntervalType.DEFAULT)

enum class IntervalType {DEFAULT, BRIGHT, DARK, OFF}

enum class TimerType {WORKOUT, YOGA, COOK, OTHER}

fun Color.string() = this.value.toString()

fun String.color(): Color {
    val uLongValue = this.toULongOrNull()
    var color = Color.Transparent
    uLongValue?.let { color = Color(it) }
    return color
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
    else (this / 60).toString().padStart(2, '0')+":"+(this % 60).toString().padStart(2, '0')