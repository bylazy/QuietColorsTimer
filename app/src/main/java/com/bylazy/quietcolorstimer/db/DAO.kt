package com.bylazy.quietcolorstimer.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDAO{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(timer: InTimer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterval(interval: Interval)

    @Update
    suspend fun updateTimer(timer: InTimer)

    @Delete
    suspend fun deleteTimer(timer: InTimer)

    @Query("DELETE FROM intervals_table WHERE timerId=:id")
    suspend fun clearTimer(id: Int)

    @Query("SELECT * FROM timers_table")
    fun getAllTimersWithIntervals(): Flow<List<TimerWithIntervals>>

    @Query("SELECT * FROM timers_table WHERE id=:id")
    suspend fun getTimerWithIntervals(id: Int): TimerWithIntervals

    @Transaction
    suspend fun updateTimerWithIntervals(timer: InTimer, intervals: List<Interval>) {
        clearTimer(timer.id)
        updateTimer(timer)
        for (i in intervals) {
            insertInterval(i.copy(timerId = timer.id))
        }
    }

    @Transaction
    suspend fun updateTimerWithIntervals(timerWithIntervals: TimerWithIntervals) {
        clearTimer(timerWithIntervals.timer.id)
        updateTimer(timerWithIntervals.timer)
        for (i in timerWithIntervals.intervals) {
            insertInterval(i.copy(timerId = timerWithIntervals.timer.id))
        }
    }

}