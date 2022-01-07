package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bylazy.quietcolorstimer.data.NEW_TIMER
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.db.TimerWithIntervals
import com.bylazy.quietcolorstimer.repo.Repo
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TimerDB.getInstance(application)
    private val repo = Repo(db.timerDAO())

    private val filter = mutableStateOf("")

    val timers = repo.getAllTimersWithIntervals().map { list ->
        list.map { element -> TimerWithIntervals(element.timer, element.intervals.sortedBy { it.position }) }
    }.also { flow ->
        if (filter.value != "") flow.map {list ->
            list.filter { it.timer.name.contains(filter.value, true) }} }

    val selectedTimer = mutableStateOf<InTimer?>(null)

    fun applyFilter(newFilter: String) {
        filter.value = newFilter
    }

    fun addTimer() {
        selectedTimer.value = null
        viewModelScope.launch {
            repo.insertTimer(NEW_TIMER)
        }
    }

    fun selectTimer(timer: InTimer) {
        selectedTimer.value = timer
    }

    fun pinTimer(timer: InTimer) {
        selectedTimer.value = null
        viewModelScope.launch {
            repo.updateTimer(timer = timer.copy(pinned = !timer.pinned))
        }
    }

    fun deleteTimer(timer: InTimer) {
        selectedTimer.value = null
        viewModelScope.launch {
            repo.deleteTimer(timer)
        }
    }
}