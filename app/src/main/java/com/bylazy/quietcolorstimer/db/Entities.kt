package com.bylazy.quietcolorstimer.db

import androidx.compose.ui.graphics.Color
import androidx.room.*
import androidx.room.ForeignKey.CASCADE

//todo - move to separate file*************************
enum class IntervalType {DEFAULT, BRIGHT, DARK, OFF}

enum class TimerType {WORKOUT, COOK, OTHER}

fun Color.string() = this.value.toString()

fun String.color() = Color(this.toULong()) //todo - refactor with null-safety
//todo--------------------------------------------------


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
    val type: IntervalType
)

@Entity(tableName = "timers_table")
data class InTimer(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val description: String,
    val pinned: Boolean,
    val type: TimerType //TODO - online properties
)

data class TimerWithIntervals(
    @Embedded
    val timer: InTimer,
    @Relation(parentColumn = "id", entityColumn = "timerId")
    val intervals: List<Interval>
)