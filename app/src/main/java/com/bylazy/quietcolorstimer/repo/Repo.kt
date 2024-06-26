package com.bylazy.quietcolorstimer.repo

import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.db.TimerDAO

class Repo(private val dao: TimerDAO){
    suspend fun insertTimer(timer: InTimer) = dao.insertTimer(timer = timer)
    suspend fun deleteTimer(timer: InTimer) = dao.deleteTimer(timer = timer)
    suspend fun updateTimerWithIntervals(timer: InTimer,
                                         intervals: List<Interval>) = dao.updateTimerWithIntervals(timer, intervals)
    fun getAllTimersWithIntervals(filter: String) = dao.getAllTimersWithIntervals(filter)
    fun getTimerWithIntervalsFlow(id: Int) = dao.getTimerWithIntervalsFlow(id)
    suspend fun updateTimer(timer: InTimer) = dao.updateTimer(timer)

}