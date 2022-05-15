package com.bylazy.quietcolorstimer.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.bylazy.quietcolorstimer.data.IntervalSignal
import com.bylazy.quietcolorstimer.data.IntervalSound
import com.bylazy.quietcolorstimer.data.IntervalType
import com.bylazy.quietcolorstimer.data.TimerType


@Entity(tableName = "intervals_table",
    foreignKeys = [ForeignKey(entity = InTimer::class,
        parentColumns = ["id"],
        childColumns = ["timerId"],
        onDelete = CASCADE)])
data class Interval(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(index = true)
    val timerId: Int,
    val position: Int,
    val name: String,
    val duration: Int,
    val color: String,
    @ColumnInfo(defaultValue = "SILENT")
    val signal: IntervalSignal,
    @ColumnInfo(defaultValue = "KNUCKLE")
    val sound: IntervalSound,
    @ColumnInfo(defaultValue = "")
    val customSoundUri: String,
    val type: IntervalType
)

@Entity(tableName = "timers_table")
data class InTimer(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val description: String,
    @ColumnInfo(defaultValue = "")
    val link: String,
    val pinned: Boolean,
    val type: TimerType //TODO - online properties - v2 futures
)

data class TimerWithIntervals(
    @Embedded
    val timer: InTimer,
    @Relation(parentColumn = "id", entityColumn = "timerId")
    val intervals: List<Interval>
)