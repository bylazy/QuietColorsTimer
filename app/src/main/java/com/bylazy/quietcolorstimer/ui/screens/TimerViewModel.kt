package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bylazy.quietcolorstimer.data.*
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.repo.Repo
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TimerViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val db = TimerDB.getInstance(application, viewModelScope)
    private val repo = Repo(db.timerDAO())

    private val intervals = mutableListOf(COOLDOWN_INTERVAL)
    private lateinit var timer: String
    private var overallDuration = 0
    private val _weights = mutableListOf<Pair<Float, Color>>()
    val weights: List<Pair<Float, Color>>
     get() = _weights
    lateinit var ticks: Flow<Event>

    private var skip: Boolean = false
    var pause = mutableStateOf(false)

    val loading = mutableStateOf(true)

    init {
        viewModelScope.launch {
            val currentTimer = repo.getTimerWithIntervals(savedStateHandle["id"] ?: 0)
            intervals.addAll(currentTimer.intervals)
            overallDuration = intervals.sumOf { it.duration }
            timer = currentTimer.timer.name
            _weights.addAll(intervals.map { it.duration.toFloat() / overallDuration.toFloat() to it.color.color()})

            ticks = flow<Event> {
                var overall = 1
                intervals.forEachIndexed { index, interval ->

                    for (i in 1..interval.duration) {
                        while (pause.value) {delay(100)}
                        if (skip) {
                            overall += interval.duration - i + 1
                            skip = false
                            break
                        }
                        emit(Event(interval = interval.name,
                            next = if (index == intervals.lastIndex) "Finish!" else intervals[index+1].name,
                            timer = timer,
                            type = interval.type,
                            duration = interval.duration,
                            overallDuration = overallDuration,
                            currentSecondsLeft = interval.duration - i,
                            overallSeconds = overall,
                            currentProgress = i.toFloat() / interval.duration.toFloat(),
                            overallProgress = overall.toFloat() / overallDuration.toFloat(),
                            color = if (i == interval.duration - 1 && index != intervals.lastIndex) intervals[index+1].color.color()
                            else interval.color.color()))
                        overall++
                    }
                }
            }.onEach { delay(1000) }
                .onStart { delay(1000) }
                .conflate()
                .onCompletion { emit(FINISH_EVENT) }

            loading.value = false
        }
    }

    /*
    fun startTimer(): Flow<Event> {
        var overall = 0
        return flow<Event> {
            for (interval in intervals) {
                for (i in 1..interval.duration) {
                    emit(Event(interval.name,
                        timer,
                        interval.type,
                        interval.duration,
                        overallDuration,
                        interval.duration - i,
                        overall,
                        i.toFloat() / interval.duration.toFloat(),
                        overall.toFloat() / overallDuration.toFloat(),
                        interval.color.color()))
                    delay(1000)
                    overall++
                }
            }
        }.conflate().onCompletion { emit(FINISH_EVENT) }
    }*/

    fun skipInterval() {
        skip = true
    }

    fun pauseInterval() {
        pause.value = !pause.value
        //Log.d("pause", pause.value.toString())
    }
}