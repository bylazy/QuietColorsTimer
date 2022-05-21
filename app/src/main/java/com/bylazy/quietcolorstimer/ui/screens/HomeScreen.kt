package com.bylazy.quietcolorstimer.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.TimerType
import com.bylazy.quietcolorstimer.data.color
import com.bylazy.quietcolorstimer.data.durationText
import com.bylazy.quietcolorstimer.data.onColor
import com.bylazy.quietcolorstimer.db.*
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme
import com.bylazy.quietcolorstimer.ui.utils.ExpandableBlock
import com.bylazy.quietcolorstimer.ui.utils.FilledIconButton
import com.bylazy.quietcolorstimer.ui.utils.ListItemCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//v2 design -----------------------------------------------

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()
    val timerState by viewModel.timers.collectAsState(initial = listOf())
    val selectedTimer by viewModel.selectedTimer
    /*
    val listState = if (selectedTimer != null) rememberLazyListState(timerState.map {it.timer}.indexOf(selectedTimer))
        else rememberLazyListState()*/
    val listState = rememberLazyListState()
    val filterText by viewModel.filterFlow.collectAsState(initial = "")
    LaunchedEffect(key1 = selectedTimer) {
        if (selectedTimer != null) {
            val index = timerState.map {it.timer}.indexOf(selectedTimer)
            delay(500)
            val isVisible = listState.layoutInfo.visibleItemsInfo.last().index != index
            if (!isVisible) listState.animateScrollToItem(index)
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeTopBar(
                currentFilter = filterText,
                scope = scope,
                applyFilter = viewModel::applyFilter
            )
        },
        bottomBar = {
            HomeBottomBar(
                listState = listState,
                scope = scope,
                itemsCount = timerState.size
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addTimer()
                scope.launch {
                    delay(100)
                    listState.animateScrollToItem(timerState.size - 1)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add timer"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        LazyColumn(
            state = listState,
            //modifier = Modifier.padding(it),
            contentPadding = it
        ) {
            items(timerState, key = { item -> item.timer.id }) { item ->
                
                Row(modifier = Modifier.animateItemPlacement()) {
                    TimerListItem(
                        timer = item,
                        selectedTimer = selectedTimer,
                        navController = navController,
                        scaffoldState = scaffoldState,
                        onPin = viewModel::pinTimer,
                        onDelete = viewModel::deleteTimer,
                        onSelect = viewModel::selectTimer
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
            }
            item {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
fun HomeBottomBar(
    listState: LazyListState,
    scope: CoroutineScope,
    itemsCount: Int
) {
    BottomAppBar(cutoutShape = CircleShape) {
        Spacer(modifier = Modifier.size(12.dp))
        Icon(imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Scroll to bottom",
            modifier = Modifier.clickable {
                scope.launch {
                    listState.animateScrollToItem(itemsCount - 1)
                }
            })
        Spacer(
            modifier = Modifier
                .size(12.dp)
                .weight(1f)
        )
        Icon(imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Scroll to top",
            modifier = Modifier.clickable {
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            })
        Spacer(modifier = Modifier.size(12.dp))
    }
}

@Composable
fun HomeTopBar(
    currentFilter: String,
    scope: CoroutineScope,
    applyFilter: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester.Default
    var isFilterOn by remember { mutableStateOf(false) }
    TopAppBar {
        if (!isFilterOn) {
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = "Quiet Colors Timer",
                style = MaterialTheme.typography.h6
            )
            Spacer(
                modifier = Modifier
                    .size(12.dp)
                    .weight(1f)
            )
            Icon(imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.clickable {
                    isFilterOn = true
                    scope.launch {
                        delay(50)
                        focusRequester.requestFocus()
                    }
                })
            Spacer(modifier = Modifier.size(12.dp))
        } else {
            TextField(
                value = currentFilter,
                onValueChange = { applyFilter(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(fontWeight = FontWeight.Bold),
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Filter",
                        modifier = Modifier.clickable {
                            applyFilter("")
                            isFilterOn = false
                        })
                }, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimerListItem(
    timer: TimerWithIntervals,
    selectedTimer: InTimer?,
    navController: NavController,
    scaffoldState: ScaffoldState,
    onPin: (InTimer) -> Unit,
    onDelete: (InTimer) -> Unit,
    onSelect: (InTimer) -> Unit,
) {
    val scope = rememberCoroutineScope()
    ListItemCard {
        Column {
            RowTop(modifier = Modifier,
                timer = timer.timer,
                isBlank = timer.intervals.isEmpty(),
                isSelected = timer.timer == selectedTimer,
                onClick = onSelect,
                onPlay = {
                    navController.navigate("start/${timer.timer.id}")
                },
                onBlank = {
                    scope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar("Timer is empty! Add some intervals first")
                    }
                })
            ExpandableBlock(expanded = timer.timer == selectedTimer) {
                Spacer(modifier = Modifier.size(2.dp))
                RowDetails(scaffoldState = scaffoldState,
                    timer = timer.timer,
                    intervals = timer.intervals,
                    onDelete = onDelete,
                    onEdit = {
                        navController.navigate("timer/${timer.timer.id}")
                    },
                    onPin = onPin
                )
            }

        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RowTop(
    modifier: Modifier = Modifier,
    timer: InTimer,
    isBlank: Boolean,
    isSelected: Boolean,
    onClick: (InTimer) -> Unit,
    onPlay: (InTimer) -> Unit,
    onBlank: () -> Unit
) {
    Row(
        modifier
            .clickable { onClick(timer) }
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = when (timer.type) {
                TimerType.WORKOUT -> painterResource(id = R.drawable.ic_big_workout)
                TimerType.YOGA -> painterResource(id = R.drawable.ic_big_yoga)
                TimerType.COOK -> painterResource(id = R.drawable.ic_big_cook)
                else -> painterResource(id = R.drawable.ic_big_common)
            }, contentDescription = "Timer Icon",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        AnimatedContent(targetState = isSelected) { targetState ->
            if (targetState) Text(text = timer.name, fontWeight = FontWeight.Bold)
            else Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(text = timer.name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = timer.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(
            modifier = Modifier
                .size(12.dp)
                .weight(1f)
        )
        Box(modifier = Modifier
            .size(40.dp)
            .clip(shape = CircleShape)
            .clickable {
                if (!isBlank) onPlay(timer)
                else onBlank()
            }
            .background(
                color = MaterialTheme.colors.secondary,
                shape = CircleShape
            ),
            contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play"
            )
        }
    }
}

@Composable
fun IntervalBox(interval: Interval) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(6.dp))
            .background(color = interval.color.color())
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = interval.duration.durationText(),
                style = MaterialTheme.typography.h6, color = interval.color.color().onColor()
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = interval.name, color = interval.color.color().onColor())
        }
    }
}

@Composable
fun RowDetails(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    timer: InTimer,
    intervals: List<Interval>,
    onDelete: (InTimer) -> Unit,
    onEdit: () -> Unit,
    onPin: (InTimer) -> Unit
) {
    //val scope = rememberCoroutineScope()
    //val context = LocalContext.current
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = timer.description)
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        /*
        if (timer.link.isNotBlank()) {
            Text(text = timer.link,
                modifier = Modifier.clickable {
                    scope.launch {
                        try {
                            val uri = Uri.parse(timer.link)
                            val intent = Intent(ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }
                        catch (e: ActivityNotFoundException) {
                            scaffoldState
                                .snackbarHostState
                                .showSnackbar("Invalid Link!")
                        }
                    }
                },
                color = Color.Blue,
                maxLines = 1,
                textDecoration = TextDecoration.Underline)
            Spacer(modifier = Modifier.size(8.dp))
            Divider()
            Spacer(modifier = Modifier.size(8.dp))
        }*/
        if (intervals.isEmpty()) Text(text = "Empty Timer, tap Edit to add some intervals!")
        else Row(modifier = Modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically) {
            intervals.forEachIndexed { index, interval ->
                IntervalBox(interval = interval)
                if (index != intervals.lastIndex) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next"
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            FilledIconButton(
                modifier = Modifier,
                onClick = { showDeleteConfirmationDialog = true },
                text = "Delete",
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete"
            )
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .weight(1f)
            )
            FilledIconButton(
                modifier = Modifier,
                onClick = { onEdit() },
                text = "Edit",
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit"
            )
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .weight(1f)
            )
            FilledIconButton(
                modifier = Modifier,
                onClick = {
                    onPin(timer)
                },
                text = if (timer.pinned) "Unpin" else "Pin",
                imageVector = if (timer.pinned) Icons.Default.KeyboardArrowDown
                else Icons.Default.KeyboardArrowUp,
                contentDescription = "Pin/Unpin"
            )
        }
    }
    if (showDeleteConfirmationDialog)
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            buttons = {
                Row(modifier = Modifier.padding(bottom = 12.dp)) {
                    Spacer(
                        modifier = Modifier
                            .size(12.dp)
                            .weight(1f)
                    )
                    Button(onClick = { showDeleteConfirmationDialog = false }) {
                        Text(text = "Cancel")
                    }
                    Spacer(
                        modifier = Modifier
                            .size(12.dp)
                            .weight(1f)
                    )
                    Button(onClick = {
                        showDeleteConfirmationDialog = false
                        onDelete(timer)
                    }) {
                        Text(text = "Delete")
                    }
                    Spacer(
                        modifier = Modifier
                            .size(12.dp)
                            .weight(1f)
                    )
                }
            },
            title = {
                Text(
                    text = "Confirmation required",
                    style = MaterialTheme.typography.h5
                )
            },
            text = {
                Text(
                    text = "Delete Timer? Are you sure?",
                    style = MaterialTheme.typography.button
                )
            },
            shape = RoundedCornerShape(28.dp),
            properties = DialogProperties()
        )
}


//end v2 design --------------------------------------------


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun RowTopPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            RowTop(Modifier,
                test_timer_1.copy(description = "Loooooooooooooooooooooong long long long long long description"),
                isBlank = false,
                isSelected = false,
                onClick = {},
                onPlay = {},
                onBlank = {})
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun IntervalBoxPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            IntervalBox(interval = test_timer_1_intervals[0].copy(duration = 258))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            RowDetails(scaffoldState = rememberScaffoldState(),
                timer = test_timer_1,
                intervals = test_timer_1_intervals,
                onDelete = {},
                onEdit = {},
                onPin = {})
        }
    }
}
