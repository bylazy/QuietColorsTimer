package com.bylazy.quietcolorstimer.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.IntervalType
import com.bylazy.quietcolorstimer.data.MAX_INTERVAL_NAME_LENGTH
import com.bylazy.quietcolorstimer.data.color
import com.bylazy.quietcolorstimer.data.durationText
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyColumn
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyListItem
import com.bylazy.quietcolorstimer.ui.utils.*

@ExperimentalAnimationApi
@Composable
fun IntervalsScreen(intervalsViewModel: IntervalsViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val intervals by intervalsViewModel.intervalsState.collectAsState(listOf())
    val timer by intervalsViewModel.timer
    val currentInterval by intervalsViewModel.currentInterval

    Scaffold(
        bottomBar = {
            BottomAppBar(cutoutShape = CircleShape) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Exit")
                }
                Spacer(
                    modifier = Modifier
                        .size(4.dp)
                        .weight(1f)
                )
                IconButton(onClick = { /*TODO - Save*/ }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                intervalsViewModel.addInterval()
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        Column(modifier = Modifier.padding(it)) {
            TimerEditor(timer)
            Spacer(modifier = Modifier.size(4.dp))
            AnimatedLazyColumn(state = listState, items = intervals.map { interval ->
                AnimatedLazyListItem(key = interval.id.toString(), value = interval) {
                    IntervalRow(
                        interval = interval,
                        currentInterval = currentInterval,
                        onCopy = intervalsViewModel::copyInterval,
                        onUp = intervalsViewModel::upInterval,
                        onDown = intervalsViewModel::downInterval,
                        onDelete = intervalsViewModel::deleteInterval,
                        onCancel = intervalsViewModel::cancelEdit,
                        onDone = intervalsViewModel::doneEdit,
                        onSelect = intervalsViewModel::selectInterval
                    )
                }
            })
        }
    }
}

@Composable
fun TimerEditor(timer: InTimer) {
    ListBlock {
        Text(text = "Timer Editor", modifier = Modifier.padding(4.dp))
    }
}

@ExperimentalAnimationApi
@Composable
fun IntervalRow(
    interval: Interval,
    currentInterval: Interval?,
    onCopy: (Interval) -> Unit,
    onUp: (Interval) -> Unit,
    onDown: (Interval) -> Unit,
    onDelete: (Interval) -> Unit,
    onCancel: () -> Unit,
    onDone: (Interval) -> Unit,
    onSelect: (Interval) -> Unit
) {
    ListItemCard {
        Column {
            IntervalRowTop(
                interval = interval,
                currentInterval = currentInterval,
                onSelect = onSelect,
                onCopy = onCopy,
                onUp = onUp,
                onDown = onDown
            )
            ExpandableBlock(expanded = interval == currentInterval) {
                IntervalEditor(
                    interval = interval,
                    onDelete = onDelete,
                    onCancel = onCancel,
                    onDone = onDone
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun IntervalRowTop(
    interval: Interval,
    currentInterval: Interval?,
    onCopy: (Interval) -> Unit,
    onUp: (Interval) -> Unit,
    onDown: (Interval) -> Unit,
    onSelect: (Interval) -> Unit
) {
    Row(modifier = Modifier.clickable { onSelect(interval) },
        verticalAlignment = Alignment.CenterVertically) {
        DurationBox(interval = interval)
        Spacer(modifier = Modifier.size(4.dp))
        Icon(painter = painterResource(
            id = when (interval.type) {
                IntervalType.BRIGHT -> R.drawable.ic_int_type_bright
                IntervalType.DARK -> R.drawable.ic_int_type_dark
                else -> R.drawable.ic_int_type_default
            }
        ),
            contentDescription = "Backlit mode")
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = interval.name, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier
            .size(4.dp)
            .weight(1f))
        FadingBlock(visible = currentInterval != interval) {
            IntervalRowButtons(interval = interval,
                onCopy = onCopy,
                onUp = onUp,
                onDown = onDown)
        }
    }
}

@Composable
fun DurationBox(interval: Interval) {
    Box(
        modifier = Modifier
            .background(
                color = interval.color.color(),
                shape = RoundedCornerShape(4.dp)
            )
            .size(50.dp), contentAlignment = Alignment.Center
    ) {
        Text(text = interval.duration.durationText(), style = MaterialTheme.typography.button,
            color = if (interval.color.color().luminance() >= 0.5f) Color.Black else Color.White)
    }
}

@Composable
fun IntervalRowButtons(interval: Interval,
                       onCopy: (Interval) -> Unit,
                       onUp: (Interval) -> Unit,
                       onDown: (Interval) -> Unit, ){
    Row {
        RoundIconButton(painter = painterResource(id = R.drawable.ic_copy)) {
            onCopy(interval)
        }
        Spacer(modifier = Modifier.size(4.dp))
        RoundIconButton(imageVector = Icons.Default.KeyboardArrowUp) {
            onUp(interval)
        }
        Spacer(modifier = Modifier.size(4.dp))
        RoundIconButton(imageVector = Icons.Default.KeyboardArrowDown) {
            onDown(interval)
        }
    }
}

@Composable
fun IntervalEditor(
    interval: Interval,
    onDelete: (Interval) -> Unit,
    onCancel: () -> Unit,
    onDone: (Interval) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var intervalName by remember { mutableStateOf(interval.name) }
    Column {
        OutlinedTextField(value = intervalName,
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
            onValueChange = {intervalName = it.take(MAX_INTERVAL_NAME_LENGTH)},
            singleLine = true,
            label = { Text(text = "Short Interval name (up to 12 symbols):")},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
        )
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))

    }
}