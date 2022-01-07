package com.bylazy.quietcolorstimer.repo

import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.db.TimerDAO
import com.bylazy.quietcolorstimer.db.TimerWithIntervals

class Repo(private val dao: TimerDAO){
    suspend fun insertTimer(timer: InTimer) = dao.insertTimer(timer = timer)
    suspend fun deleteTimer(timer: InTimer) = dao.deleteTimer(timer = timer)
    suspend fun updateTimerWithIntervals(timer: InTimer,
                                         intervals: List<Interval>) = dao.updateTimerWithIntervals(timer, intervals)
    suspend fun updateTimerWithIntervals(timerWithIntervals: TimerWithIntervals) = dao.updateTimerWithIntervals(timerWithIntervals)
    fun getAllTimersWithIntervals() = dao.getAllTimersWithIntervals()
    suspend fun getTimerWithIntervals(id: Int) = dao.getTimerWithIntervals(id)
    suspend fun updateTimer(timer: InTimer) = dao.updateTimer(timer)
}