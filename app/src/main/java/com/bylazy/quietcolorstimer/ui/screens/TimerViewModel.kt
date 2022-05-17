package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bylazy.quietcolorstimer.data.*
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

            val currentUri = if (interval.sound == IntervalSound.CUSTOM)
                Uri.parse(interval.customSoundUri) else
                Uri.parse(resPath + interval.sound.i)

            val nextUri = if (index == lastIndex) currentUri
            else if (list[index+1].sound == IntervalSound.CUSTOM)
                Uri.parse(list[index+1].customSoundUri) else
                Uri.parse(resPath + list[index + 1].sound.i)

            var i = 1

            while (i <= interval.duration) {

                while (pause.value) {delay(100)}

                if (skip) {
                    overall += interval.duration - i
                    skip = false
                    i = interval.duration
                }

                emit(Event(interval = interval.name,
                    next = if (index == lastIndex) "-" else list[index+1].name,
                    timer = timerName,
                    type = interval.type,
                    sound = interval.signal,
                    soundUri = nextUri,
                    duration = interval.duration,
                    overallDuration = timerDuration,
                    currentSecondsLeft = interval.duration - i,
                    overallSeconds = overall,
                    currentProgress = i.toFloat() / interval.duration.toFloat(),
                    overallProgress = overall.toFloat() / timerDuration.toFloat(),
                    color = if (i == interval.duration - 1
                        && interval.duration >= 10
                        && index != lastIndex-1
                        && index != 0) list[index+1].color.color()
                    else interval.color.color()))
                i++
                overall++

            }
        } }
        .onEach { delay(1000) }
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