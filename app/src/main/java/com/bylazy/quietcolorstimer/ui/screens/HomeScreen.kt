package com.bylazy.quietcolorstimer.ui.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.TimerType
import com.bylazy.quietcolorstimer.data.color
import com.bylazy.quietcolorstimer.data.durationText
import com.bylazy.quietcolorstimer.db.*
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyColumn
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyListItem
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme
import com.bylazy.quietcolorstimer.ui.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackBarHostState = remember {SnackbarHostState()}
    val timerState by homeViewModel.timers.collectAsState(initial = listOf())
    val selectedTimer by homeViewModel.selectedTimer
    var filterOn by remember { mutableStateOf(false)}
    val filterText by homeViewModel.filterFlow.collectAsState(initial = "")
    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester.Default
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackBarHostState)
    Scaffold(modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {/* TODO - search */ TopAppBar() {
            if (filterOn) {
                Spacer(modifier = Modifier.size(12.dp))
                BasicTextField(value = filterText, onValueChange = {
                    homeViewModel.applyFilter(it)
                }, modifier = Modifier.focusRequester(focusRequester = focusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
                )
                Spacer(modifier = Modifier
                    .size(4.dp)
                    .weight(1f))
                Icon(imageVector = Icons.Default.Clear,
                    contentDescription = "Clear filter", modifier = Modifier.clickable {
                        homeViewModel.applyFilter("")
                        filterOn = false
                    })
                Spacer(modifier = Modifier.size(12.dp))
            } else {
                Spacer(modifier = Modifier.size(12.dp))
                Text(text = "Quiet Colors Timer", style = MaterialTheme.typography.h5)
            }
        }},
        bottomBar = {
                    BottomAppBar(cutoutShape = CircleShape) {
                        IconButton(onClick = { scope.launch { listState.animateScrollToItem(0) } }) {
                            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to Top")
                        }
                        Spacer(modifier = Modifier
                            .size(4.dp)
                            .weight(1F))
                        IconButton(onClick = { if (!filterOn)
                            scope.launch {
                                filterOn = true
                                delay(20)
                                focusRequester.requestFocus()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                /* TODO - Add timer and scroll to end */
                homeViewModel.addTimer()
                scope.launch {
                    delay(500)
                    listState.animateScrollToItem(timerState.size-1)
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true) {
        AnimatedLazyColumn(state = listState,
            contentPadding = it, items = timerState.map { timerWithIntervals ->
                AnimatedLazyListItem(key = timerWithIntervals.timer.id.toString(),
                    value = timerWithIntervals) {
                    TimerRow(timer = timerWithIntervals,
                        selectedTimer = selectedTimer,
                        navController = navController,
                        scaffoldState = scaffoldState,
                        onPin = homeViewModel::pinTimer,
                        onDelete = homeViewModel::deleteTimer,
                        onSelect = homeViewModel::selectTimer)
                }
            })
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerRow(timer: TimerWithIntervals,
             selectedTimer: InTimer?,
             navController: NavController,
             scaffoldState: ScaffoldState,
             onPin: (InTimer) -> Unit,
             onDelete: (InTimer) -> Unit,
             onSelect: (InTimer) -> Unit){
    ListItemCard {
        TimerRowTop(timer = timer.timer,
            isEmpty = timer.intervals.isEmpty(),
            selectedTimer = selectedTimer,
            navController = navController,
            scaffoldState = scaffoldState,
            onSelect = onSelect)
        ExpandableBlock(expanded = timer.timer == selectedTimer) {
            Spacer(modifier = Modifier.size(4.dp))
            TimerRowDetails(timer = timer.timer,
                intervals = timer.intervals,
                onPin = onPin,
                onDelete = onDelete)
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerRowTop(timer: InTimer,
                isEmpty: Boolean,
                selectedTimer: InTimer?,
                navController: NavController,
                scaffoldState: ScaffoldState,
                onSelect: (InTimer) -> Unit){
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier.clickable { onSelect(timer) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center) {
            Icon(modifier = Modifier.padding(4.dp),
                painter = painterResource(id = when (timer.type) {
                    TimerType.WORKOUT -> R.drawable.ic_type_workout
                    TimerType.COOK -> R.drawable.ic_type_cook
                    TimerType.YOGA -> R.drawable.ic_type_yoga
                    else -> R.drawable.ic_type_default
                }),
                contentDescription = "")
        }
        Spacer(modifier = Modifier.size(4.dp))
        Column(modifier = Modifier.fillMaxWidth(0.65f)) {
            Text(text = timer.name, style = MaterialTheme.typography.h6, maxLines = 1)
            Spacer(modifier = Modifier.size(2.dp))
            FadingBlock(visible = timer != selectedTimer) {
                Text(text = timer.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis) //TODO - expand
            }
        }
        Spacer(modifier = Modifier
            .size(4.dp)
            .weight(1f, true))
        RoundIconButton(imageVector = Icons.Default.Edit) {
            navController.navigate("timer/${timer.id}")
        }
        Spacer(modifier = Modifier.size(4.dp))
        RoundIconButton(imageVector = Icons.Default.PlayArrow) {
            if (!isEmpty) navController.navigate("start/${timer.id}")
            else scope.launch { scaffoldState.snackbarHostState.showSnackbar("Timer is empty") }
        }
    }
}

@Composable
fun TimerRowDetails(timer: InTimer,
                    intervals: List<Interval>,
                    onDelete: (InTimer) -> Unit,
                    onPin: (InTimer) -> Unit){
    var showDeleteAlert by remember { mutableStateOf(false)}
    Column {
        Spacer(modifier = Modifier.size(12.dp))
        Text(text = timer.description)
        Spacer(modifier = Modifier.size(12.dp))
        Divider()
        Spacer(modifier = Modifier.size(12.dp))
        if (intervals.isEmpty()) Text(text = "Timer is Empty",
            style = MaterialTheme.typography.h6, color = Color.Gray)
        else LazyRow(verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(end = 4.dp)) {
            itemsIndexed(intervals) { i, interval ->
                IntervalBox(interval = interval, index = i+1)
                if (i != intervals.size-1) {
                    Spacer(modifier = Modifier.size(2.dp))
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "")
                    Spacer(modifier = Modifier.size(2.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        Divider()
        Spacer(modifier = Modifier.size(12.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            OvalIconButton(caption = "Delete", description = "Delete",
                imageVector = Icons.Default.Delete) {
                showDeleteAlert = true
            }
            Spacer(modifier = Modifier
                .size(4.dp)
                .weight(1f))
            OvalIconButton(caption = if (timer.pinned) "Unpin" else "Pin to top",
                description = "Pin/Unpin",
                imageVector = if (timer.pinned) Icons.Default.ArrowDropDown else Icons.Default.KeyboardArrowUp) {
                onPin(timer)
            }
        }
    }
    if (showDeleteAlert) AlertDialog(onDismissRequest = { showDeleteAlert = false },
        buttons = { Row(modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween){
            Button(onClick = {
                onDelete(timer)
                showDeleteAlert = false
            }) {
                Text(text = "Delete")
            }
            Spacer(modifier = Modifier
                .size(4.dp)
                .weight(1f))
            Button(onClick = { showDeleteAlert = false }) {
                Text(text = "Cancel")
            }
        } },
        title = { Text(text = "Delete Timer?")},
        text = { Text(text = "Are You Sure?", style = MaterialTheme.typography.body1) },
        shape = RoundedCornerShape(12.dp))
}

@Composable
fun IntervalBox(interval: Interval, index: Int){
    Box(modifier = Modifier
        .background(color = interval.color.color(),
            shape = RoundedCornerShape(10.dp))) {
        Box(modifier = Modifier
            .size(20.dp)
            .align(Alignment.TopStart)
            .background(color = Color.DarkGray, shape = CircleShape),
            contentAlignment = Alignment.Center) {
            Text(text = index.toString(), color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.body2)
        }
        Column(modifier = Modifier
            .align(Alignment.Center)
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = interval.duration.durationText(),
                color = if (interval.color.color().luminance() >= 0.5f) Color.Black else Color.White,
                style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = interval.name,
                color = if (interval.color.color().luminance() >= 0.5f) Color.Black else Color.White,
                style = MaterialTheme.typography.body1)
        }
    }
}



@ExperimentalAnimationApi
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun TimerRowPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            TimerRow(TimerWithIntervals(test_timer_1.copy(description = "short shorter shortest most shortest description of all descriptions"),
                test_timer_2_intervals), null, rememberNavController(), rememberScaffoldState(), {}, {}, {})
        }
    }
}

@ExperimentalAnimationApi
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun IntervalBoxPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            IntervalBox(test_timer_1_intervals[0].copy(duration = 158), 22)
        }
    }
}