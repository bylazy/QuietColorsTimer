package com.bylazy.quietcolorstimer.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [InTimer::class, Interval::class],
    version = 4,
    autoMigrations = [AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3), AutoMigration(from = 3, to = 4)],
    exportSchema = true)
abstract class TimerDB: RoomDatabase(){
    abstract fun timerDAO(): TimerDAO

    companion object {
        @Volatile
        private var INSTANCE: TimerDB? = null
        fun getInstance(context: Context, coroutineScope: CoroutineScope): TimerDB {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    TimerDB::class.java, "timers.db")
                    .addCallback(object: RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            coroutineScope.launch {
                                getInstance(context, coroutineScope).timerDAO().apply {
                                    insertTimerWithIntervals(initial_timer_yoga_1, initial_timer_yoga_1_intervals)
                                    insertTimerWithIntervals(initial_timer_yoga_2, initial_timer_yoga_2_intervals)
                                    insertTimerWithIntervals(initial_timer_yoga_3, initial_timer_yoga_3_intervals)
                                    insertTimerWithIntervals(initial_timer_yoga_4, initial_timer_yoga_4_intervals)
                                    insertTimerWithIntervals(initial_timer_workout_1, initial_timer_1_workout_intervals)
                                    insertTimerWithIntervals(initial_timer_workout_2, initial_timer_workout_2_intervals)
                                    insertTimerWithIntervals(initial_timer_workout_3, initial_timer_workout_3_intervals)
                                    insertTimerWithIntervals(initial_timer_workout_4, initial_timer_workout_4_intervals)
                                    insertTimerWithIntervals(initial_timer_cook_1, initial_timer_cook_1_intervals)
                                    insertTimerWithIntervals(initial_timer_cook_2, initial_timer_cook_2_intervals)
                                    insertTimerWithIntervals(initial_timer_common_1, initial_timer_common_1_intervals)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

