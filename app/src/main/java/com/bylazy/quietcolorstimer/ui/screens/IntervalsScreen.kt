package com.bylazy.quietcolorstimer.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.*
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.db.test_timer_1
import com.bylazy.quietcolorstimer.db.test_timer_1_intervals
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyColumn
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyListItem
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme
import com.bylazy.quietcolorstimer.ui.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IntervalsScreen(intervalsViewModel: IntervalsViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val intervals by intervalsViewModel.intervalsState.collectAsState(listOf())
    val timer by intervalsViewModel.timer
    var backPressed by remember { mutableStateOf(false) }
    val currentInterval by intervalsViewModel.currentInterval
    val scrollState = intervalsViewModel.scrollToPos.collectAsState()

    LaunchedEffect(scrollState.value) {
        //listState.animateScrollToItem(scrollState.value)
        delay(100) //TODO - check index + refactor
        listState.animateScrollToItem(scrollState.value + 1, scrollOffset = 0)
    }

    Scaffold(
        topBar = {},
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
                IconButton(onClick = {
                    scope.launch {
                        backPressed = true
                        intervalsViewModel.doneAll()
                        navController.popBackStack()
                    }
                }, enabled = !backPressed) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                intervalsViewModel.addInterval()
                scope.launch {
                    listState.animateScrollToItem(intervals.lastIndex)
                    //TODO - Scrolling
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        Column(modifier = Modifier.padding(it)) {
            AnimatedLazyColumn(state = listState, items = buildList {
                add(AnimatedLazyListItem(key = "timer", value = "timer") {
                    InListTimerEditor(timer = timer, onOk = intervalsViewModel::updateTimer)
                })
                addAll(intervals.map { interval ->
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
            })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InListTimerEditor(
    timer: InTimer,
    onOk: (InTimer) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(timer.name) }
    var desc by remember { mutableStateOf(timer.description) }
    var type by remember { mutableStateOf(timer.type) }
    val focusRequester = FocusRequester.Default
    val focusManager = LocalFocusManager.current
    Row {
        ListItemCard {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = when (timer.type) {
                                TimerType.WORKOUT -> painterResource(id = R.drawable.ic_type_workout)
                                TimerType.YOGA -> painterResource(id = R.drawable.ic_type_yoga)
                                TimerType.COOK -> painterResource(id = R.drawable.ic_type_cook)
                                else -> painterResource(id = R.drawable.ic_type_default)
                            },
                            contentDescription = "Timer Icon"
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = timer.name, style = MaterialTheme.typography.h5)
                    Spacer(
                        modifier = Modifier
                            .size(4.dp)
                            .weight(1f)
                    )
                    FadingBlock(visible = !expanded) {
                        RoundIconButton(imageVector = Icons.Default.Edit) {
                            expanded = true
                        }
                    }
                }
                ExpandableBlock(expanded = expanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it.take(25) }, //TODO - const?
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = "Short Timer Name:") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() })
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            label = { Text(text = "Timer Description:") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Row {
                            Column(modifier = Modifier.weight(0.5f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_type_workout),
                                        contentDescription = "Workout"
                                    )
                                    Spacer(modifier = Modifier.size(2.dp))
                                    RadioButton(
                                        selected = type == TimerType.WORKOUT,
                                        onClick = { type = TimerType.WORKOUT })
                                    Spacer(modifier = Modifier.size(2.dp))
                                    Text(text = "Workout")
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_type_yoga),
                                        contentDescription = "Yoga"
                                    )
                                    Spacer(modifier = Modifier.size(2.dp))
                                    RadioButton(
                                        selected = type == TimerType.YOGA,
                                        onClick = { type = TimerType.YOGA })
                                    Spacer(modifier = Modifier.size(2.dp))
                                    Text(text = "Yoga")
                                }
                            }
                            Column(modifier = Modifier.weight(0.5f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_type_cook),
                                        contentDescription = "Cooking"
                                    )
                                    Spacer(modifier = Modifier.size(2.dp))
                                    RadioButton(
                                        selected = type == TimerType.COOK,
                                        onClick = { type = TimerType.COOK })
                                    Spacer(modifier = Modifier.size(2.dp))
                                    Text(text = "Cook")
                                }
                                Spacer(modifier = Modifier.size(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_type_default),
                                        contentDescription = "Other"
                                    )
                                    Spacer(modifier = Modifier.size(2.dp))
                                    RadioButton(
                                        selected = type == TimerType.OTHER,
                                        onClick = { type = TimerType.OTHER })
                                    Spacer(modifier = Modifier.size(2.dp))
                                    Text(text = "Other")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        Divider()
                        Spacer(modifier = Modifier.size(4.dp))
                        Row {
                            Spacer(modifier = Modifier.size(12.dp))
                            OvalIconButton(
                                caption = "Cancel",
                                imageVector = Icons.Default.Close
                            ) {
                                expanded = false
                            }
                            Spacer(
                                modifier = Modifier
                                    .size(12.dp)
                                    .weight(1f)
                            )
                            OvalIconButton(
                                caption = "OK",
                                imageVector = Icons.Default.Done
                            ) {
                                onOk(timer.copy(name = name, description = desc, type = type))
                                expanded = false
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
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
    Row(
        modifier = Modifier.clickable { onSelect(interval) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        DurationBox(interval = interval)
        Spacer(modifier = Modifier.size(4.dp))
        Icon(
            painter = painterResource(
                id = when (interval.type) {
                    IntervalType.BRIGHT -> R.drawable.ic_int_type_bright
                    IntervalType.DARK -> R.drawable.ic_int_type_dark
                    else -> R.drawable.ic_int_type_default
                }
            ),
            contentDescription = "Backlit mode"
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = interval.name, style = MaterialTheme.typography.h6)
        Spacer(
            modifier = Modifier
                .size(4.dp)
                .weight(1f)
        )
        FadingBlock(visible = currentInterval != interval) {
            IntervalRowButtons(
                interval = interval,
                onCopy = onCopy,
                onUp = onUp,
                onDown = onDown
            )
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
        Text(
            text = interval.duration.durationText(), style = MaterialTheme.typography.button,
            color = if (interval.color.color().luminance() >= 0.5f) Color.Black else Color.White
        )
    }
}

@Composable
fun IntervalRowButtons(
    interval: Interval,
    onCopy: (Interval) -> Unit,
    onUp: (Interval) -> Unit,
    onDown: (Interval) -> Unit,
) {
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

@ExperimentalAnimationApi
@Composable
fun IntervalEditor(
    interval: Interval,
    onDelete: (Interval) -> Unit,
    onCancel: () -> Unit,
    onDone: (Interval) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var intervalName by remember { mutableStateOf(interval.name) }
    var intervalType by remember { mutableStateOf(interval.type) }
    var intervalSignal by remember { mutableStateOf(interval.signal) }
    var intervalColor by remember { mutableStateOf(interval.color.color()) }
    var intervalDuration by remember { mutableStateOf(interval.duration) }
    //val intervalDuration by remember { mutableStateOf(TextFieldValue(interval.duration.toString())) }
    Column {
        OutlinedTextField(
            value = intervalName,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            onValueChange = { intervalName = it.take(MAX_INTERVAL_NAME_LENGTH) },
            singleLine = true,
            label = { Text(text = "Short Interval name (up to 12 symbols):") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Select Interval duration in seconds (5 to 600):")
        Spacer(modifier = Modifier.size(4.dp))
        DurationSelector(initial = interval.duration,
            onChange = { intervalDuration = it })
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Backlight Mode")
        Spacer(modifier = Modifier.size(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = intervalType == IntervalType.BRIGHT,
                onClick = { intervalType = IntervalType.BRIGHT })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_int_type_bright),
                contentDescription = "Bright"
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Bright")
            Spacer(
                modifier = Modifier
                    .size(4.dp)
                    .weight(1f)
            )
            RadioButton(selected = intervalType == IntervalType.DARK,
                onClick = { intervalType = IntervalType.DARK })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_int_type_dark),
                contentDescription = "Dark"
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Dark")
            Spacer(
                modifier = Modifier
                    .size(4.dp)
                    .weight(1f)
            )
            RadioButton(selected = intervalType == IntervalType.DEFAULT,
                onClick = { intervalType = IntervalType.DEFAULT })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_int_type_default),
                contentDescription = "Default"
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Default")
        }
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Sound Settings")
        Spacer(modifier = Modifier.size(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = intervalSignal == IntervalSignal.SILENT,
                onClick = { intervalSignal = IntervalSignal.SILENT })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Silent"
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Silent")
            Spacer(
                modifier = Modifier
                    .size(4.dp)
                    .weight(1f)
            )
            RadioButton(selected = intervalSignal == IntervalSignal.SOUND,
                onClick = { intervalSignal = IntervalSignal.SOUND })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Sound"
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Sound")
            Spacer(
                modifier = Modifier
                    .size(4.dp)
                    .weight(1f)
            )
            RadioButton(selected = intervalSignal == IntervalSignal.VIBRATION,
                onClick = { intervalSignal = IntervalSignal.VIBRATION })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                imageVector = Icons.Default.Warning, //TODO change icons
                contentDescription = "Vibration"
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Vibration")
        }
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Interval color:")
        Spacer(modifier = Modifier.size(4.dp))
        ColorSelector(initial = interval.color.color(), onChange = { intervalColor = it })
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OvalIconButton(
                caption = "Delete",
                imageVector = Icons.Default.Delete
            ) {
                onDelete(interval)
            }
            Spacer(
                modifier = Modifier
                    .size(4.dp)
                    .weight(1f)
            )
            OvalIconButton(
                caption = "Cancel",
                imageVector = Icons.Default.Close
            ) {
                onCancel()
            }
            Spacer(
                modifier = Modifier
                    .size(4.dp)
                    .weight(1f)
            )
            OvalIconButton(
                caption = "Done",
                imageVector = Icons.Default.Done
            ) {
                onDone(
                    interval.copy(
                        name = intervalName,
                        duration = intervalDuration,
                        color = intervalColor.string(),
                        signal = intervalSignal,
                        type = intervalType
                    )
                )
            }
        }
    }
}

@Composable
fun ColorSelector(initial: Color, onChange: (Color) -> Unit) {
    var color by remember { mutableStateOf(initial) }
    val listState = rememberLazyListState(if (initial in colors) colors.indexOf(initial) else 0)
    var showColorDialog by remember { mutableStateOf(false) }
    Row {
        Column {
            Text(text = "Customize:", fontSize = 11.sp)
            Spacer(modifier = Modifier.size(4.dp))
            ColorBox(
                modifier = Modifier.clickable { showColorDialog = true },
                color = color,
                selected = color !in colors
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Or select color:", fontSize = 11.sp)
            Spacer(modifier = Modifier.size(4.dp))
            LazyRow(state = listState) {
                items(colors) { c ->
                    ColorBox(
                        modifier = Modifier.clickable {
                            color = c
                            onChange(c)
                        },
                        color = c,
                        selected = color == c
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
        }
    }

    if (showColorDialog) ColorDialog(color = color,
        onCancel = { showColorDialog = false },
        onOk = {
            color = it
            onChange(it)
            showColorDialog = false
        })

}

@Composable
fun ColorBox(modifier: Modifier, color: Color, selected: Boolean) {
    Box(
        modifier
            .size(54.dp)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colors.onPrimary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@ExperimentalAnimationApi
@Composable
fun DurationSelector(initial: Int, onChange: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val duration = remember { mutableStateOf(TextFieldValue(initial.toString())) }
    var editMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Row(verticalAlignment = Alignment.CenterVertically) {
        RepeatingClickableButton(onLongClick = {
            duration.value =
                TextFieldValue(((duration.value.text.toInt() - 1)).coerceAtLeast(0).toString())
            onChange(duration.value.text.toIntOrNull() ?: 0)
        }, shape = RoundedCornerShape(50)) {
            Text(text = "-", style = MaterialTheme.typography.button)
        }
        Spacer(modifier = Modifier.size(4.dp))
        if (editMode) BasicTextField(
            value = duration.value,
            onValueChange = { duration.value = it },
            textStyle = MaterialTheme.typography.h6,
            modifier = Modifier
                .width(48.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { editMode = false })
        )
        else AnimatedContent(targetState = duration.value.text.toInt(), transitionSpec = {
            if (targetState > initialState) {
                slideInVertically(initialOffsetY = { height -> height }) + fadeIn() with
                        slideOutVertically(targetOffsetY = { height -> -height }) + fadeOut()
            } else {
                slideInVertically(initialOffsetY = { height -> -height }) + fadeIn() with
                        slideOutVertically(targetOffsetY = { height -> height }) + fadeOut()
            }.using(SizeTransform(clip = false))
        })
        { targetDur ->
            Text(
                text = targetDur.toString().padStart(3, '0'),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Icon(imageVector = if (!editMode) Icons.Default.Edit else Icons.Default.Done,
            contentDescription = "Edit",
            modifier = Modifier.clickable {
                editMode = !editMode
                if (editMode) {
                    duration.value = duration.value
                        .copy(selection = TextRange(0, duration.value.text.length))
                    scope.launch {
                        delay(50)
                        focusRequester.requestFocus()
                    }
                } else {
                    onChange(duration.value.text.toIntOrNull() ?: 0)
                }
            })
        Spacer(modifier = Modifier.size(4.dp))
        RepeatingClickableButton(onLongClick = {
            duration.value = TextFieldValue((duration.value.text.toInt() + 1).toString())
            onChange(duration.value.text.toIntOrNull() ?: 0)
        }, shape = RoundedCornerShape(50)) {
            Text(text = "+", style = MaterialTheme.typography.button)
        }
    }
}

//---v2-design---

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun IntervalDetails(
    interval: Interval,
    onDelete: (Interval) -> Unit,
    onDone: (Interval) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(interval.name) }
    var duration by remember { mutableStateOf(interval.duration) }
    var type by remember { mutableStateOf(interval.type) }
    var sound by remember { mutableStateOf(interval.signal) }
    var color by remember { mutableStateOf(interval.color.color()) }
    var typeSelectorExpanded by remember { mutableStateOf(false) }
    var soundSelectorExpanded by remember { mutableStateOf(false)}
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Divider() //todo padding
        Spacer(modifier = Modifier.size(8.dp))
        TextField(
            value = name,
            onValueChange = { name = it.take(MAX_INTERVAL_NAME_LENGTH) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Short interval name") },
            singleLine = true
        )
        Spacer(modifier = Modifier.size(8.dp))
        Divider() //todo padding
        Spacer(modifier = Modifier.size(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(0.5f)) {
                ExposedDropdownMenuBox(expanded = typeSelectorExpanded,
                    onExpandedChange = { typeSelectorExpanded = !typeSelectorExpanded }) {
                    TextField(value = when (type) {
                        IntervalType.BRIGHT -> "Bright"
                        IntervalType.DARK -> "Dark"
                        else -> "Default"
                    },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = typeSelectorExpanded
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = when (type) {
                                    IntervalType.BRIGHT -> painterResource(id = R.drawable.ic_int_type_bright)
                                    IntervalType.DARK -> painterResource(id = R.drawable.ic_int_type_dark)
                                    else -> painterResource(id = R.drawable.ic_int_type_default)
                                },
                                contentDescription = "Type"
                            )
                        })
                    ExposedDropdownMenu(expanded = typeSelectorExpanded,
                        onDismissRequest = { typeSelectorExpanded = false }) {
                        DropdownMenuItem(onClick = {
                            type = IntervalType.BRIGHT
                            typeSelectorExpanded = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_int_type_bright),
                                contentDescription = "Type"
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Bright",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        DropdownMenuItem(onClick = {
                            type = IntervalType.DARK
                            typeSelectorExpanded = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_int_type_dark),
                                contentDescription = "Type"
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Dark",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        DropdownMenuItem(onClick = {
                            type = IntervalType.DEFAULT
                            typeSelectorExpanded = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_int_type_default),
                                contentDescription = "Type"
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Default",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Column(modifier = Modifier.weight(0.5f)) {
                ExposedDropdownMenuBox(expanded = soundSelectorExpanded,
                    onExpandedChange = { soundSelectorExpanded = !soundSelectorExpanded }) {
                    TextField(value = when (sound) {
                        IntervalSignal.SILENT -> "Silent"
                        IntervalSignal.SOUND -> "Sound"
                        IntervalSignal.VIBRATION -> "Vibration"
                    },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = typeSelectorExpanded
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = when (sound) {
                                    IntervalSignal.SILENT -> painterResource(id = R.drawable.ic_int_sound_silent)
                                    IntervalSignal.SOUND -> painterResource(id = R.drawable.ic_int_sound_quiet)
                                    IntervalSignal.VIBRATION -> painterResource(id = R.drawable.ic_int_sound_vibro)
                                },
                                contentDescription = "Sound"
                            )
                        })
                    ExposedDropdownMenu(expanded = soundSelectorExpanded,
                        onDismissRequest = { soundSelectorExpanded = false }) {
                        DropdownMenuItem(onClick = {
                            sound = IntervalSignal.SILENT
                            typeSelectorExpanded = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_int_sound_silent),
                                contentDescription = "Sound"
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Silent",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        DropdownMenuItem(onClick = {
                            sound = IntervalSignal.SOUND
                            typeSelectorExpanded = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_int_sound_quiet),
                                contentDescription = "Type"
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Sound",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        DropdownMenuItem(onClick = {
                            sound = IntervalSignal.VIBRATION
                            typeSelectorExpanded = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_int_sound_vibro),
                                contentDescription = "Type"
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Vibration",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Divider() //todo padding
        Spacer(modifier = Modifier.size(8.dp))
        //todo new?
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Duration (seconds):")
            Spacer(modifier = Modifier.size(8.dp).weight(1f))
            DurationSelector(initial = duration, onChange = {duration = it})
        }
        Spacer(modifier = Modifier.size(8.dp))
        Divider() //todo padding
        Spacer(modifier = Modifier.size(8.dp))
        //todo new?
        ColorSelector(initial = color, onChange = {color = it})
        Spacer(modifier = Modifier.size(8.dp))
        Divider() //todo padding
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            FilledIconButton(modifier = Modifier,
                onClick = { onDelete(interval) },
                text = "Delete",
                imageVector = Icons.Default.Delete)
            Spacer(modifier = Modifier
                .size(8.dp)
                .weight(1f))
            FilledIconButton(modifier = Modifier,
                onClick = { onCancel() },
                text = "Cancel",
                imageVector = Icons.Default.Close)
            Spacer(modifier = Modifier
                .size(8.dp)
                .weight(1f))
            FilledIconButton(modifier = Modifier,
                //todo - check constrains
                onClick = { onDone(interval.copy(name = name,
                    duration = duration,
                    type = type,
                    signal = sound,
                    color = color.string())) },
                text = "Done",
                imageVector = Icons.Default.Done)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IntervalRowTop(
    interval: Interval,
    selected: Boolean,
    onCopy: (Interval) -> Unit,
    onUp: (Interval) -> Unit,
    onDown: (Interval) -> Unit,
    onSelect: (Interval) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DurationBox(duration = interval.duration, color = interval.color.color())
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Icon(
                painter = painterResource(
                    id = when (interval.type) {
                        IntervalType.BRIGHT -> R.drawable.ic_int_type_bright
                        IntervalType.DARK -> R.drawable.ic_int_type_dark
                        else -> R.drawable.ic_int_type_default
                    }
                ), contentDescription = "Type",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(2.dp))
            Icon(
                painter = painterResource(
                    id = when (interval.signal) {
                        IntervalSignal.SILENT -> R.drawable.ic_int_sound_silent
                        IntervalSignal.SOUND -> R.drawable.ic_int_sound_quiet
                        IntervalSignal.VIBRATION -> R.drawable.ic_int_sound_vibro
                    }
                ),
                contentDescription = "Sound",
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = interval.name, fontWeight = FontWeight.Bold)
        Spacer(
            modifier = Modifier
                .size(8.dp)
                .weight(1f)
        )
        FadingBlock(visible = !selected) {
            Row {
                RoundIconButton(
                    onClick = { onCopy(interval) },
                    painter = painterResource(id = R.drawable.ic_copy),
                    desc = "Copy"
                )
                Spacer(modifier = Modifier.size(8.dp))
                RoundIconButton(
                    onClick = { onUp(interval) },
                    imageVector = Icons.Default.KeyboardArrowUp,
                    desc = "Move Up"
                )
                Spacer(modifier = Modifier.size(8.dp))
                RoundIconButton(
                    onClick = { onDown(interval) },
                    imageVector = Icons.Default.KeyboardArrowDown,
                    desc = "Move Down"
                )
                Spacer(modifier = Modifier.size(8.dp))
                RoundIconButton(
                    onClick = { onSelect(interval) },
                    imageVector = Icons.Default.Edit,
                    desc = "Edit"
                )
            }
        }
    }
}


@Composable
fun DurationBox(duration: Int, color: Color) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(60.dp)
            .background(shape = RoundedCornerShape(8.dp), color = color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = duration.durationText(),
            fontFamily = FontFamily(Font(R.font.ubuntu_bold)),
            fontSize = dpToSp(dp = 16.dp),
            color = color.onColor()
        )
    }
}

//---end-v2-design---

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun IntervalPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            DurationBox(duration = 188, color = Color.Blue)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun IntervalRowTopPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            IntervalRowTop(interval = test_timer_1_intervals[0],
                selected = false, onCopy = {}, onDown = {}, onSelect = {}, onUp = {})
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun IntervalDetailsPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            IntervalDetails(interval = test_timer_1_intervals[0], onDelete = {}, onDone = {}) {

            }
        }
    }
}