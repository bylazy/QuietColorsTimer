package com.bylazy.quietcolorstimer.db

import androidx.compose.ui.graphics.Color
import com.bylazy.quietcolorstimer.data.IntervalSignal
import com.bylazy.quietcolorstimer.data.IntervalType
import com.bylazy.quietcolorstimer.data.TimerType
import com.bylazy.quietcolorstimer.data.string

val test_timer_1 = InTimer(name = "Test timer 1",
    description = "Short description", pinned = false, type = TimerType.WORKOUT)

val test_timer_2 = InTimer(name = "Test timer 2",
    description = "Some description", pinned = false, type = TimerType.OTHER)

val test_timer_1_intervals = listOf(Interval(timerId = 0,
    position = 1,
    name = "Interval 1",
    duration = 33,
    color = Color.Green.string(),
    signal = IntervalSignal.SILENT,
    type = IntervalType.BRIGHT), Interval(timerId = 0,
    position = 2,
    name = "Interval 2",
    duration = 55,
    color = Color.Blue.string(),
    signal = IntervalSignal.SILENT,
    type = IntervalType.DARK)
)

val test_timer_2_intervals = listOf(Interval(timerId = 0,
    position = 1,
    name = "Interval 3",
    duration = 120,
    color = Color.Red.string(),
    signal = IntervalSignal.SILENT,
    type = IntervalType.BRIGHT), Interval(timerId = 0,
    position = 2,
    name = "Interval 4",
    duration = 380,
    color = Color.Yellow.string(),
    signal = IntervalSignal.SILENT,
    type = IntervalType.DARK)
)