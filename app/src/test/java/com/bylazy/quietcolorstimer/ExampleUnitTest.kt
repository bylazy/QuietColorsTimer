package com.bylazy.quietcolorstimer

import androidx.compose.ui.graphics.Color
import com.bylazy.quietcolorstimer.data.color
import com.bylazy.quietcolorstimer.data.string
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {
    @Test
    fun testColorTypeConverters() {
        assertEquals(Color.Green.string().color(), Color.Green)
    }
}