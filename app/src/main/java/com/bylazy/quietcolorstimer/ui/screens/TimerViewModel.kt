package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bylazy.quietcolorstimer.data.COOLDOWN_INTERVAL
import com.bylazy.quietcolorstimer.data.Event
import com.bylazy.quietcolorstimer.data.FINISH_INTERVAL
import com.bylazy.quietcolorstimer.data.color
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.repo.Repo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class TimerViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val db = TimerDB.getInstance(application, viewModelScope)
    private val repo = Repo(db.timerDAO())

    //new logic
    private var skip: Boolean = false
    var pause = mutableStateOf(false)
    private var timerName = ""
    private var timerDuration = 0
    private var overall = 1
    private var lastIndex = 0
    val colorProgress = mutableListOf<Pair<Float, Color>>()
    val intervalsState = repo.getTimerWithIntervalsFlow(savedStateHandle["id"] ?: 0)
        .map { timer -> (listOf(COOLDOWN_INTERVAL) + timer.intervals + listOf(FINISH_INTERVAL))
            .also { list ->
                timerName = timer.timer.name
                timerDuration = list.sumOf { it.duration }
                lastIndex = list.lastIndex
                colorProgress.addAll(list.map { interval -> interval.duration.toFloat() / timerDuration.toFloat() to interval.color.color() })
        } }
        .transform { list -> list.forEachIndexed{ index, interval ->
            for (i in 1..interval.duration) {
                while (pause.value) {delay(100)}
                if (skip) {
                    overall += interval.duration - i + 1
                    skip = false
                    break
                }
                emit(Event(interval = interval.name,
                    next = if (index == lastIndex) "-" else list[index+1].name,
                    timer = timerName,
                    type = interval.type,
                    sound = interval.signal,
                    duration = interval.duration,
                    overallDuration = timerDuration,
                    currentSecondsLeft = interval.duration - i,
                    overallSeconds = overall,
                    currentProgress = i.toFloat() / interval.duration.toFloat(),
                    overallProgress = overall.toFloat() / timerDuration.toFloat(),
                    color = if (i == interval.duration - 1
                        && index != lastIndex-1
                        && index != 0) list[index+1].color.color()
                    else interval.color.color()))
                overall++
                delay(1000)
            }
        } }
        //.onEach { delay(1000) }
        .conflate()
        .onStart { delay(1000) }
        .shareIn(viewModelScope, started = SharingStarted.Lazily)

    fun skipInterval() {
        skip = true
    }

    fun pauseInterval() {
        pause.value = !pause.value
    }

    //new logic ends

}