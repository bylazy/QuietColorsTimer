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
    version = 3,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)],
    exportSchema = true)
abstract class TimerDB: RoomDatabase(){
    abstract fun timerDAO(): TimerDAO

    companion object {
        @Volatile
        private var INSTANCE: TimerDB? = null
        fun getInstance(context: Context, coroutineScope: CoroutineScope): TimerDB {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    TimerDB::class.java, "6_timers.db")
                    .addCallback(object: RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            coroutineScope.launch {
                                getInstance(context, coroutineScope).timerDAO().apply {
                                    insertTimerWithIntervals(test_timer_1, test_timer_1_intervals)
                                    insertTimerWithIntervals(test_timer_2, test_timer_2_intervals)
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

