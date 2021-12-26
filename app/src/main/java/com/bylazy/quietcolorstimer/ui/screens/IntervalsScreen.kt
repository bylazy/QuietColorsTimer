package com.bylazy.quietcolorstimer.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyColumn
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyListItem
import com.bylazy.quietcolorstimer.ui.utils.ListBlock
import com.bylazy.quietcolorstimer.ui.utils.ListItemCard

@ExperimentalAnimationApi
@Composable
fun IntervalsScreen(intervalsViewModel: IntervalsViewModel){
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val intervals by intervalsViewModel.intervalsState.collectAsState(listOf())
    val timer by intervalsViewModel.timer

    Scaffold(bottomBar = {/* TODO - ok and cancel */ },
        floatingActionButton = { FloatingActionButton(onClick = { /*TODO - add interval*/ }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }},
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true) {
        Column(modifier = Modifier.padding(it)) {
            TimerEditor(timer)
            Spacer(modifier = Modifier.size(2.dp))
            AnimatedLazyColumn(state = listState, items = intervals.map { interval ->
                AnimatedLazyListItem(key = interval.id.toString(), value = interval) {
                    IntervalRow(interval = interval)
                }
            })
        }
    }
}

@Composable
fun TimerEditor(timer: InTimer){
    ListBlock {
        Text(text = "Timer Editor", modifier = Modifier.padding(4.dp))
    }
}

@Composable
fun IntervalRow(interval: Interval) {
    ListItemCard() {
        Text(text = interval.name, modifier = Modifier.padding(4.dp))
    }
}