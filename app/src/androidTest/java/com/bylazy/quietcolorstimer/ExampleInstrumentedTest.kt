package com.bylazy.quietcolorstimer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bylazy.quietcolorstimer.db.TimerDAO
import com.bylazy.quietcolorstimer.db.TimerDB
import com.bylazy.quietcolorstimer.db.test_timer_1
import com.bylazy.quietcolorstimer.db.test_timer_1_intervals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DBTest {

    private lateinit var db: TimerDB
    private lateinit var dao: TimerDAO

    @Before
    fun createDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, TimerDB::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.timerDAO()
    }

    @After
    fun deleteDB() {
        db.close()
    }

    @Test
    fun testAddTimer() = runBlocking {
        dao.insertTimer(test_timer_1)
        dao.getAllTimersWithIntervals().take(1).collect { list ->
            assertEquals(list[0].timer.name, test_timer_1.name)
        }
    }

    @Test
    fun testDeleteTimer() = runBlocking {
        val row = dao.insertTimer(test_timer_1)
        dao.deleteTimer(test_timer_1.copy(id = row.toInt()))
        dao.getAllTimersWithIntervals().take(1).collect { list ->
            assertEquals(list.size, 0)
        }
    }

    @Test
    fun testUpdateTimerWithIntervals() = runBlocking {
        val row = dao.insertTimer(test_timer_1)
        dao.updateTimerWithIntervals(test_timer_1.copy(id = row.toInt(), name = "Check"),
            test_timer_1_intervals)
        dao.getAllTimersWithIntervals().take(1).collect { list ->
            assertEquals(list.size, 1)
            assertEquals(list[0].timer.name, "Check")
            assertEquals(list[0].intervals.size, test_timer_1_intervals.size)
        }
    }

}