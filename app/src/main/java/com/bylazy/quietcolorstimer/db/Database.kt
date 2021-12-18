package com.bylazy.quietcolorstimer.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [InTimer::class, Interval::class], version = 1, exportSchema = false)
abstract class TimerDB: RoomDatabase(){
    abstract fun timerDAO(): TimerDAO

    companion object {
        @Volatile
        private var INSTANCE: TimerDB? = null
        fun getInstance(context: Context): TimerDB {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    TimerDB::class.java, "6_timers.db")
                    .addCallback(object: RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            GlobalScope.launch {
                                getInstance(context).timerDAO().apply {
                                    //TODO(Initial data)
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

