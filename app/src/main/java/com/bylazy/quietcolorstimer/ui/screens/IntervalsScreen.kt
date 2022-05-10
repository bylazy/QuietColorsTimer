package com.bylazy.quietcolorstimer.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.navigation.NavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.*
import com.bylazy.quietcolorstimer.db.InTimer
import com.bylazy.quietcolorstimer.db.Interval
import com.bylazy.quietcolorstimer.db.test_timer_1
import com.bylazy.quietcolorstimer.db.test_timer_1_intervals
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme
import com.bylazy.quietcolorstimer.ui.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//---v2-design---

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IntervalScreen(
    intervalsViewModel: IntervalsViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var isEdit by remember { mutableStateOf(false) }
    val dummyState by intervalsViewModel.state.collectAsState(initial = null)
    val timer by intervalsViewModel.timer
    val intervals by intervalsViewModel.intervalsState.collectAsState()
    val selectedInterval by intervalsViewModel.currentInterval
    val scrollTo by intervalsViewModel.scrollToPos.collectAsState(initial = 0)
    Scaffold(
        topBar = {
            TopBar(name = timer.name,
                type = timer.type, isEdit = isEdit, onEdit = { isEdit = !isEdit })
        },
        bottomBar = {
            BottomBar(onOk = {
                scope.launch {
                    intervalsViewModel.doneAll()
                    navController.popBackStack()
                }
            },
                onCancel = { navController.popBackStack() })
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = { FloatingActionButton(onClick = {
            intervalsViewModel.addInterval()
        }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }}
    )
    { paddingValues ->
        FadingBlock(visible = isEdit) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TimerEditor(timer = timer,
                    onCancel = { isEdit = false },
                    onOk = { intervalsViewModel.updateTimer(it); isEdit = false })
            }
        }
        FadingBlock(visible = !isEdit) {
            IntervalsList(
                intervals = intervals,
                selectedInterval = selectedInterval,
                scrollTo = scrollTo,
                onDelete = intervalsViewModel::deleteInterval,
                onDone = intervalsViewModel::doneEdit,
                onCancel = intervalsViewModel::cancelEdit,
                onCopy = intervalsViewModel::copyInterval,
                onUp = intervalsViewModel::upInterval,
                onDown = intervalsViewModel::downInterval,
                onSelect = intervalsViewModel::selectInterval,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun TopBar(name: String, type: TimerType, isEdit: Boolean, onEdit: () -> Unit) {
    TopAppBar {
        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            painter = painterResource(
                id = when (type) {
                    TimerType.WORKOUT -> R.drawable.ic_big_workout
                    TimerType.YOGA -> R.drawable.ic_big_yoga
                    TimerType.COOK -> R.drawable.ic_big_cook
                    else -> R.drawable.ic_big_common
                }
            ),
            contentDescription = "Timer type",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = name, style = MaterialTheme.typography.h6)
        Spacer(
            modifier = Modifier
                .size(8.dp)
                .weight(1f)
        )
        IconButton(onClick = { onEdit() }) {
            Icon(
                imageVector = when (isEdit) {
                    true -> Icons.Default.Close
                    false -> Icons.Default.Edit
                }, contentDescription = "Edit"
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntervalsList(
    intervals: List<Interval>,
    selectedInterval: Interval?,
    scrollTo: Int = 0,
    onDelete: (Interval) -> Unit,
    onDone: (Interval) -> Unit,
    onCancel: () -> Unit,
    onCopy: (Interval) -> Unit,
    onUp: (Interval) -> Unit,
    onDown: (Interval) -> Unit,
    onSelect: (Interval) -> Unit,
    paddingValues: PaddingValues
) {
    val state = rememberLazyListState()
    LaunchedEffect(key1 = scrollTo) {
        delay(100)
        state.animateScrollToItem(scrollTo)
    }
    LazyColumn(state = state, contentPadding = paddingValues) {
        items(intervals, key = { item -> item.id }) { interval ->
            Row(modifier = Modifier.animateItemPlacement()) {
                IntervalListItem(
                    interval = interval,
                    selected = interval == selectedInterval,
                    onDelete = onDelete,
                    onDone = onDone,
                    onCancel = onCancel,
                    onCopy = onCopy,
                    onUp = onUp,
                    onDown = onDown,
                    onSelect = onSelect
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimerEditor(
    timer: InTimer,
    onCancel: () -> Unit,
    onOk: (InTimer) -> Unit
) {
    var name by remember { mutableStateOf(timer.name) }
    var desc by remember { mutableStateOf(timer.description) }
    var link by remember { mutableStateOf(timer.link) }
    var type by remember { mutableStateOf(timer.type) }
    var expanded by remember { mutableStateOf(false) }
    val descFocusRequester = remember {FocusRequester()}
    //val linkFocusRequester = remember {FocusRequester()}
    //val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = name,
            onValueChange = { name = it.take(MAX_TIMER_NAME_LENGTH) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name:") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { descFocusRequester.requestFocus() }),
            //colors = TextFieldColors
        )
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        TextField(
            value = desc,
            onValueChange = { desc = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(descFocusRequester),
            label = { Text(text = "Description:") },
            //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            //keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
        /*
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        TextField(
            value = link,
            onValueChange = { link = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(linkFocusRequester),
            label = { Text(text = "Link:") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )*/
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        ExposedDropdownMenuBox(expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            TextField(value = when (type) {
                TimerType.WORKOUT -> "Workout / Training"
                TimerType.YOGA -> "Yoga / Relax"
                TimerType.COOK -> "Cooking"
                else -> "Other"
            },
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = when (type) {
                                TimerType.WORKOUT -> R.drawable.ic_big_workout
                                TimerType.YOGA -> R.drawable.ic_big_yoga
                                TimerType.COOK -> R.drawable.ic_big_cook
                                else -> R.drawable.ic_big_common
                            }
                        ),
                        contentDescription = "Timer type",
                        modifier = Modifier.size(24.dp)
                    )
                }, trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                })
            ExposedDropdownMenu(expanded = expanded,
                onDismissRequest = { expanded = false }) {
                DropdownMenuItem(onClick = { type = TimerType.WORKOUT; expanded = false }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_big_workout),
                        contentDescription = "Workout",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Workout / Training",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                DropdownMenuItem(onClick = { type = TimerType.YOGA; expanded = false }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_big_yoga),
                        contentDescription = "Yoga",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Yoga / Relax",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                DropdownMenuItem(onClick = { type = TimerType.COOK; expanded = false }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_big_cook),
                        contentDescription = "Cooking",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Cooking",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                DropdownMenuItem(onClick = { type = TimerType.OTHER; expanded = false }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_big_common),
                        contentDescription = "Other",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Other",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            FilledIconButton(
                modifier = Modifier,
                onClick = onCancel,
                text = "Cancel",
                imageVector = Icons.Default.Close
            )
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .weight(1f)
            )
            FilledIconButton(
                modifier = Modifier,
                onClick = { onOk(timer.copy(name = name.ifBlank { "Unnamed" },
                    description = desc,
                    link = link,
                    type = type)) },
                text = "OK",
                imageVector = Icons.Default.Done
            )
        }
    }
}

@Composable
fun BottomBar(
    onOk: () -> Unit,
    onCancel: () -> Unit
) {
    BottomAppBar(cutoutShape = CircleShape) {
        Spacer(modifier = Modifier.size(8.dp))
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel"
            )
        }
        Spacer(
            modifier = Modifier
                .size(8.dp)
                .weight(1f)
        )
        IconButton(onClick = onOk) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Done"
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IntervalListItem(
    interval: Interval,
    selected: Boolean,
    onDelete: (Interval) -> Unit,
    onDone: (Interval) -> Unit,
    onCancel: () -> Unit,
    onCopy: (Interval) -> Unit,
    onUp: (Interval) -> Unit,
    onDown: (Interval) -> Unit,
    onSelect: (Interval) -> Unit
) {
    ListItemCard {
        Column {
            IntervalRowTop(
                interval = interval,
                selected = selected,
                onCopy = onCopy,
                onUp = onUp,
                onDown = onDown,
                onSelect = onSelect
            )
            ExpandableBlock(expanded = selected) {
                IntervalDetails(
                    interval = interval,
                    onDelete = onDelete,
                    onDone = onDone,
                    onCancel = onCancel
                )
            }
        }
    }    
}

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
    var soundSelectorExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it.take(MAX_INTERVAL_NAME_LENGTH) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Short interval name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
        )
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
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
                        IntervalSignal.VIBRATION -> "Vibro"
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
                            soundSelectorExpanded = false
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
                            soundSelectorExpanded = false
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
                            soundSelectorExpanded = false
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
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        //todo new?
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Duration (sec):")
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .weight(1f)
            )
            DurationSelector(initial = duration, onChange = { duration = it })
        }
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        //todo new?
        ColorSelector(initial = color, onChange = { color = it })
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            FilledIconButton(
                modifier = Modifier,
                onClick = { onDelete(interval) },
                text = "Delete",
                imageVector = Icons.Default.Delete
            )
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .weight(1f)
            )
            FilledIconButton(
                modifier = Modifier,
                onClick = { onCancel() },
                text = "Cancel",
                imageVector = Icons.Default.Close
            )
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .weight(1f)
            )
            FilledIconButton(
                modifier = Modifier,
                onClick = {
                    onDone(
                        interval.copy(
                            name = name.take(MAX_INTERVAL_NAME_LENGTH).ifBlank { "Interval" },
                            duration = duration.coerceIn(MIN_INTERVAL_DURATION, MAX_INTERVAL_DURATION),
                            type = type,
                            signal = sound,
                            color = color.string()
                        )
                    )
                },
                text = "Done",
                imageVector = Icons.Default.Done
            )
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
            .clickable { onSelect(interval) }
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
            }
        }
    }
}

@Composable
fun ColorSelector(initial: Color, onChange: (Color) -> Unit) {
    var color by remember { mutableStateOf(initial) }
    val listState = rememberLazyListState(if (initial in colors) colors.indexOf(initial) else 0)
    var showColorDialog by remember { mutableStateOf(false) }
    Column {
        Text(text = "Customize or select color:")
        Spacer(modifier = Modifier.size(4.dp))
        Row {
            ColorBox(
                modifier = Modifier.clickable { showColorDialog = true },
                color = color,
                selected = color !in colors
            )
            Spacer(modifier = Modifier.size(4.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(48.dp)
                    .background(color = MaterialTheme.colors.primaryVariant)
            )
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
            .size(48.dp)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colors.onPrimary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(4.dp)
            )
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DurationSelector(initial: Int, onChange: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val duration = remember { mutableStateOf(TextFieldValue(initial.toString())) }
    var editMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
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
            onValueChange = { duration.value = it; onChange(it.text.toIntOrNull()?:0) },
            textStyle = MaterialTheme.typography.h6,
            modifier = Modifier
                .width(48.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); editMode = false })
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
fun BottomPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            BottomBar({ }, { })
        }
    }
}

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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun TimerEditorPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            TimerEditor(timer = test_timer_1, onCancel = {}, onOk = {})
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun TopPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            TopBar(name = "Timer name", type = TimerType.OTHER, isEdit = false) {}
        }
    }
}