package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bylazy.quietcolorstimer.data.IntervalSound
import com.bylazy.quietcolorstimer.data.NEW_TIMER
import com.bylazy.quietcolorstimer.data.resPath
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.db.TimerWithIntervals
import com.bylazy.quietcolorstimer.repo.Repo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TimerDB.getInstance(application, viewModelScope)
    private val repo = Repo(db.timerDAO())


    val filterFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val timers = filterFlow.flatMapLatest { filter ->
        repo.getAllTimersWithIntervals(filter).map { list ->
            list.map { element -> TimerWithIntervals(element.timer, element.intervals.sortedBy { it.position }) }
        }
    }

    val selectedTimer = mutableStateOf<InTimer?>(null)



    fun applyFilter(newFilter: String) {
        filterFlow.tryEmit(newFilter)
    }

    fun addTimer() {
        selectedTimer.value = null
        viewModelScope.launch {
            repo.insertTimer(NEW_TIMER)
        }
    }

    fun selectTimer(timer: InTimer) {
        if (selectedTimer.value == timer) selectedTimer.value = null
        else selectedTimer.value = timer
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