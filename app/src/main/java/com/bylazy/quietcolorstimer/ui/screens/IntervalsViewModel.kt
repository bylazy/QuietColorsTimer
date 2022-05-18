package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bylazy.quietcolorstimer.data.NEW_INTERVAL
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.db.test_timer_1
import com.bylazy.quietcolorstimer.repo.Repo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class IntervalsViewModel(application: Application,
                         savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val db = TimerDB.getInstance(application, viewModelScope)
    private val repo = Repo(db.timerDAO())

    private var intervals = mutableListOf<Interval>()
    val intervalsState = MutableStateFlow(intervals.toList())

    val timer = mutableStateOf(test_timer_1)

    var currentInterval = mutableStateOf<Interval?>(null)

    val scrollToPos = MutableStateFlow(0)

    val state = repo.getTimerWithIntervalsFlow(savedStateHandle.get<Int>("id")?:0)
        .onEach {
            timer.value = it.timer
            intervals = it.intervals.toMutableList()
            intervalsState.tryEmit(intervals)
        }

    /*
    init {
        viewModelScope.launch {
            val currentTimer = repo.getTimerWithIntervals(savedStateHandle.get<Int>("id")?:0)
            timer.value = currentTimer.timer
            intervals = currentTimer.intervals.toMutableList()
            intervalsState.tryEmit(intervals)
        }
    }*/

    private fun scrollTo(interval: Interval) {
        scrollToPos.tryEmit(intervals.indexOf(interval))
    }

    fun updateTimer(newTimer: InTimer) {
        timer.value = newTimer
    }

    fun selectInterval(interval: Interval) {
        currentInterval.value = interval
        scrollTo(interval)
    }

    fun addInterval(){
        val newIntervals = mutableListOf<Interval>()
        newIntervals.addAll(intervals)
        val maxId = intervals.maxByOrNull { it.id }?.id?:0
        newIntervals.add(NEW_INTERVAL.copy(id = maxId + 1))
        intervals = newIntervals
        intervalsState.tryEmit(intervals)
        viewModelScope.launch {
            delay(50)
            selectInterval(intervals.last())
        }
    }

    fun deleteInterval(interval: Interval){
        currentInterval.value = null
        val newIntervals = mutableListOf<Interval>()
        newIntervals.addAll(intervals)
        newIntervals.removeAll { it.id == interval.id }
        intervals = newIntervals
        intervalsState.tryEmit(intervals)
    }

    fun cancelEdit(){
        currentInterval.value = null
    }

    fun doneEdit(interval: Interval){
        currentInterval.value = null
        val newIntervals = mutableListOf<Interval>()
        newIntervals.addAll(intervals.map{if (it.id == interval.id) interval else it})
        intervals = newIntervals
        intervalsState.tryEmit(intervals)
    }

    fun copyInterval(interval: Interval) {
        currentInterval.value = null
        val newIntervals = mutableListOf<Interval>()
        newIntervals.addAll(intervals)
        val maxId = intervals.maxByOrNull { it.id }?.id?:0
        newIntervals.add(interval.copy(id = maxId + 1))
        intervals = newIntervals
        intervalsState.tryEmit(intervals)
    }

    fun upInterval(interval: Interval) {
        val index = intervals.indexOf(interval)
        if (index == 0) return
        currentInterval.value = null
        val newIntervals = mutableListOf<Interval>()
        newIntervals.addAll(intervals)
        newIntervals.removeAt(index)
        newIntervals.add(index-1, interval)
        intervals = newIntervals
        intervalsState.tryEmit(intervals)
    }

    fun downInterval(interval: Interval) {
        val index = intervals.indexOf(interval)
        if (index == intervals.lastIndex) return
        currentInterval.value = null
        val newIntervals = mutableListOf<Interval>()
        newIntervals.addAll(intervals)
        newIntervals.removeAt(index)
        newIntervals.add(index+1, interval)
        intervals = newIntervals
        intervalsState.tryEmit(intervals)
    }

    fun doneAll() {
        viewModelScope.launch {
            repo.updateTimerWithIntervals(timer.value, intervals.mapIndexed { i,v ->
                v.copy(id = 0, position = i+1) })
        }
    }



}