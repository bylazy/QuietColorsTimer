package com.bylazy.quietcolorstimer.ui.screens

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.db.TimerWithIntervals
import com.bylazy.quietcolorstimer.repo.Repo
import kotlinx.coroutines.flow.map

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = TimerDB.getInstance(application)
    private val repo = Repo(db.timerDAO())

    private val filter = mutableStateOf("")

    val timers = repo.getAllTimersWithIntervals().map { list ->
        list.map { element -> TimerWithIntervals(element.timer, element.intervals.sortedBy { it.position }) }
    }.also { flow ->
        if (filter.value != "") flow.map {list ->
            list.filter { it.timer.name.contains(filter.value, true) }} }

    fun applyFilter(newFilter: String) {
        filter.value = newFilter
    }
}