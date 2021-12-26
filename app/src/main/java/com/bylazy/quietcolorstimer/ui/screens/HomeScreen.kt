package com.bylazy.quietcolorstimer.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.bylazy.quietcolorstimer.db.TimerWithIntervals
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyColumn
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyListItem
import com.bylazy.quietcolorstimer.ui.utils.ListItemCard

@ExperimentalAnimationApi
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val timerState by homeViewModel.timers.collectAsState(initial = listOf())

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {/* TODO - search */},
        bottomBar = {/* TODO - scroll */},
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO - Add timer */ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true) {
        AnimatedLazyColumn(state = listState,
            contentPadding = it, items = timerState.map { timerWithIntervals ->
                AnimatedLazyListItem(key = timerWithIntervals.timer.id.toString(),
                    value = timerWithIntervals) {
                    TimerRow(timer = timerWithIntervals)
                }
            })
    }
}

@Composable
fun TimerRow(timer: TimerWithIntervals){
    ListItemCard() {
        Text(text = timer.timer.name, modifier = Modifier.padding(4.dp))
    }
}