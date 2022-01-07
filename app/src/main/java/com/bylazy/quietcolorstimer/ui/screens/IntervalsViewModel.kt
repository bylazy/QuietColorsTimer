package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bylazy.quietcolorstimer.data.NEW_INTERVAL
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.db.test_timer_1
import com.bylazy.quietcolorstimer.repo.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class IntervalsViewModel(application: Application,
                         savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val db = TimerDB.getInstance(application)
    private val repo = Repo(db.timerDAO())

    private var intervals = mutableListOf<Interval>()
    val intervalsState = MutableStateFlow(intervals.toList())

    val timer = mutableStateOf(test_timer_1) // TODO - refactor

    var currentInterval = mutableStateOf<Interval?>(null)

    init {
        viewModelScope.launch {
            val currentTimer = repo.getTimerWithIntervals(savedStateHandle.get<Int>("id")?:0)
            timer.value = currentTimer.timer
            intervals = currentTimer.intervals.toMutableList()
            intervalsState.tryEmit(intervals)
        }
    }

    fun selectInterval(interval: Interval) {
        currentInterval.value = interval
    }

    fun addInterval(){
        val newIntervals = mutableListOf<Interval>()
        newIntervals.addAll(intervals)
        val maxId = intervals.maxByOrNull { it.id }?.id?:0
        newIntervals.add(NEW_INTERVAL.copy(id = maxId + 1))
        intervals = newIntervals
        intervalsState.tryEmit(intervals)
        selectInterval(intervals.last())
    }

    fun deleteInterval(interval: Interval){

    }

    fun cancelEdit(){
        currentInterval.value = null
    }

    fun doneEdit(interval: Interval){

    }

    fun copyInterval(interval: Interval) {

    }

    fun upInterval(interval: Interval) {

    }

    fun downInterval(interval: Interval) {

    }
}