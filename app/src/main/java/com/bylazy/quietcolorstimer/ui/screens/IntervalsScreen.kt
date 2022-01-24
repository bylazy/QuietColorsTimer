package com.bylazy.quietcolorstimer.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.*
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyColumn
import com.bylazy.quietcolorstimer.ui.animlazylist.AnimatedLazyListItem
import com.bylazy.quietcolorstimer.ui.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                IconButton(onClick = {
                    scope.launch {
                        intervalsViewModel.doneAll()
                        navController.popBackStack()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                intervalsViewModel.addInterval()
                scope.launch {
                    listState.animateScrollToItem(intervals.lastIndex)
                }
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
    var intervalColor by remember { mutableStateOf(interval.color.color()) }
    var intervalDuration by remember { mutableStateOf(interval.duration) }
    //val intervalDuration by remember { mutableStateOf(TextFieldValue(interval.duration.toString())) }
    Column {
        OutlinedTextField(value = intervalName,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            onValueChange = {intervalName = it.take(MAX_INTERVAL_NAME_LENGTH)},
            singleLine = true,
            label = { Text(text = "Short Interval name (up to 12 symbols):")},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
        )
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Select Interval duration in seconds (5 to 600):")
        Spacer(modifier = Modifier.size(4.dp))
        DurationSelector(initial = interval.duration,
            onChange = {intervalDuration = it})
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Backlight Mode")
        Spacer(modifier = Modifier.size(4.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = intervalType == IntervalType.BRIGHT,
                onClick = { intervalType = IntervalType.BRIGHT })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(painter = painterResource(id = R.drawable.ic_int_type_bright),
                contentDescription = "Bright")
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Bright")
            Spacer(modifier = Modifier
                .size(4.dp)
                .weight(1f))
            RadioButton(selected = intervalType == IntervalType.DARK,
                onClick = { intervalType = IntervalType.DARK })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(painter = painterResource(id = R.drawable.ic_int_type_dark),
                contentDescription = "Dark")
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Dark")
            Spacer(modifier = Modifier
                .size(4.dp)
                .weight(1f))
            RadioButton(selected = intervalType == IntervalType.DEFAULT,
                onClick = { intervalType = IntervalType.DEFAULT })
            Spacer(modifier = Modifier.size(4.dp))
            Icon(painter = painterResource(id = R.drawable.ic_int_type_default),
                contentDescription = "Default")
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Default")
        }
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Interval color:")
        Spacer(modifier = Modifier.size(4.dp))
        ColorSelector(initial = interval.color.color(), onChange = {intervalColor = it})
        Spacer(modifier = Modifier.size(4.dp))
        Divider()
        Spacer(modifier = Modifier.size(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OvalIconButton(caption = "Delete",
                imageVector = Icons.Default.Delete) {
                onDelete(interval)
            }
            Spacer(modifier = Modifier
                .size(4.dp)
                .weight(1f))
            OvalIconButton(caption = "Cancel",
                imageVector = Icons.Default.Close) {
                onCancel()
            }
            Spacer(modifier = Modifier
                .size(4.dp)
                .weight(1f))
            OvalIconButton(caption = "Done",
                imageVector = Icons.Default.Done) {
                onDone(interval.copy(name = intervalName,
                    duration = intervalDuration,
                    color = intervalColor.string(),
                    type = intervalType))
            }
        }
    }
}

@Composable
fun ColorSelector(initial: Color, onChange: (Color) -> Unit){
    var color by remember { mutableStateOf(initial) }
    val listState = rememberLazyListState(if (initial in colors) colors.indexOf(initial) else 0)
    var showColorDialog by remember { mutableStateOf(false) }
    Row {
        Column {
            Text(text = "Customize:", fontSize = 11.sp)
            Spacer(modifier = Modifier.size(4.dp))
            ColorBox(modifier = Modifier.clickable { showColorDialog = true },
                color = color,
                selected = color !in colors)
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Or select:", fontSize = 11.sp)
            Spacer(modifier = Modifier.size(4.dp))
            LazyRow(state = listState) {
                items(colors) { c ->
                    ColorBox(modifier = Modifier.clickable {
                        color = c
                        onChange(c)
                    },
                        color = c,
                        selected = color == c)
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
            ))
}

@ExperimentalAnimationApi
@Composable
fun DurationSelector(initial: Int, onChange: (Int) -> Unit){
    val scope = rememberCoroutineScope()
    val duration = remember { mutableStateOf(TextFieldValue(initial.toString())) }
    var editMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Row(verticalAlignment = Alignment.CenterVertically) {
        RepeatingClickableButton(onLongClick = {
            duration.value = TextFieldValue(((duration.value.text.toInt()-1)).coerceAtLeast(0).toString())
            onChange(duration.value.text.toIntOrNull()?:0)
        }) {
            Text(text = "-", style = MaterialTheme.typography.button)
        }
        Spacer(modifier = Modifier.size(4.dp))
        if (editMode) BasicTextField(value = duration.value,
            onValueChange = {duration.value = it},
            textStyle = MaterialTheme.typography.h6,
            modifier = Modifier
                .width(48.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {editMode = false})
        )
        else AnimatedContent(targetState = duration.value.text.toInt(), transitionSpec = {
            if (targetState > initialState) {
                slideInVertically({ height -> height }) + fadeIn() with
                        slideOutVertically({ height -> -height }) + fadeOut()
            } else {
                slideInVertically({ height -> -height }) + fadeIn() with
                        slideOutVertically({ height -> height }) + fadeOut()
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
                    onChange(duration.value.text.toIntOrNull()?:0)
                }
            })
        Spacer(modifier = Modifier.size(4.dp))
        RepeatingClickableButton(onLongClick = {
            duration.value = TextFieldValue((duration.value.text.toInt()+1).toString())
            onChange(duration.value.text.toIntOrNull()?:0)
        }) {
            Text(text = "+", style = MaterialTheme.typography.button)
        }
    }
}