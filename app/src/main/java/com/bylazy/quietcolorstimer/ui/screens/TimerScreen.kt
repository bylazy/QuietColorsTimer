package com.bylazy.quietcolorstimer.ui.screens

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.*
import com.bylazy.quietcolorstimer.ui.utils.dpToSp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay


//---v2-design---

@Composable
fun TimerScr(viewModel: TimerViewModel,
             navController: NavController,
             keepScreenOn: (Boolean) -> Unit,
             restoreBrightness: () -> Unit,
             adjustBrightness: (IntervalType) -> Unit,
             loadSound: (Uri) -> Unit,
             playSound: () -> Unit,
             stopSound: () -> Unit,
             vibrate: () -> Unit){
    val systemUiController = rememberSystemUiController()
    val tick by viewModel.intervalsState.collectAsState(initial = START_EVENT)
    val orientation = LocalConfiguration.current.orientation

    LaunchedEffect(key1 = tick.type) {
        adjustBrightness(tick.type)
    }

    LaunchedEffect(key1 = tick.interval) {
        if (tick.sound == IntervalSignal.SOUND_START) playSound()
    }

    LaunchedEffect(key1 = tick) {
        when (tick.sound) {
            IntervalSignal.SOUND -> playSound()
            IntervalSignal.VIBRATION -> vibrate()
            else -> {}
        }
        if (tick.currentSecondsLeft == 0) {
            loadSound(tick.soundUri)
        }
    }

    LaunchedEffect(key1 = tick) {
        if (tick.duration == 1 && tick.interval == "Done!") {
            delay(3000)
            navController.popBackStack()
        }
    }

    DisposableEffect(Unit) {
        keepScreenOn(true)
        systemUiController.isSystemBarsVisible = false
        onDispose {
            keepScreenOn(false)
            systemUiController.isSystemBarsVisible = true
            restoreBrightness()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = tick.color)) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    Counter(tick = tick)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    TmrDetails(viewModel = viewModel,
                        tick = tick, onQuit = {
                            stopSound()
                            navController.popBackStack()
                        })
                }
            }
        }
        else {
            Row {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)) {
                    Counter(tick = tick)
                }
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)) {
                    TmrDetails(viewModel = viewModel,
                        tick = tick, onQuit = {navController.popBackStack()})
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TmrDetails(viewModel: TimerViewModel, tick: Event, onQuit: () -> Unit) {
    val onColor = tick.color.onColor()
    val isPaused by viewModel.pause
    val progress by animateFloatAsState(targetValue = tick.overallProgress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier
            .size(8.dp)
            .weight(1f))
        Text(text = tick.interval, fontSize = dpToSp(dp = 54.dp), fontWeight = FontWeight.ExtraBold, color = onColor)
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = if (tick.interval == "Done!" && tick.duration == 1) " " else "Next: ${tick.next}",
            fontSize = dpToSp(dp = 20.dp),
            color = onColor)
        Spacer(modifier = Modifier.size(24.dp))
        Row(modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .height(30.dp)
            .border(width = 1.dp, color = onColor),
            verticalAlignment = Alignment.CenterVertically) {
            viewModel.colorProgress.forEach {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(it.first)
                    .background(color = it.second))
            }
        }
        BoxWithConstraints(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)) {
            Box(modifier = Modifier
                .offset(this.maxWidth * progress)
                .size(16.dp)
                .clip(PointerShape())
                .background(color = onColor))
        }
        Spacer(modifier = Modifier.size(16.dp))
        if (tick.interval == "Done!" && tick.duration == 1) {Text(text = "---", color = onColor, fontSize = dpToSp(dp = 20.dp))}
        else Row {
            AnimatedContent(targetState = tick.overallSeconds, transitionSpec = {
                (slideInVertically(initialOffsetY = { height -> height }) + fadeIn() with
                        slideOutVertically(targetOffsetY =  { height -> -height }) + fadeOut())
                    .using(sizeTransform = SizeTransform(clip = false))
            }) { target ->
                Text(text = target.toString(), color = onColor, fontSize = dpToSp(dp = 24.dp))
            }
            Text(text = " of ${tick.overallDuration}", color = onColor, fontSize = dpToSp(dp = 24.dp))
        }
        Spacer(modifier = Modifier
            .size(8.dp)
            .weight(1f))
        Row {
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = onColor, shape = CircleShape)
                    .clickable { onQuit() }, contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Exit", tint = onColor)
            }
            Spacer(modifier = Modifier
                .size(8.dp)
                .weight(1f))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = onColor, shape = CircleShape)
                    .clickable { viewModel.skipInterval() }, contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_skip), contentDescription = "Skip", tint = onColor)
            }
            Spacer(modifier = Modifier
                .size(8.dp)
                .weight(1f))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = onColor, shape = CircleShape)
                    .clickable { viewModel.pauseInterval() }, contentAlignment = Alignment.Center
            ) {
                Icon(painter = if (isPaused) painterResource(id = R.drawable.ic_play)
                else painterResource(id = R.drawable.is_pause),
                    contentDescription = "Pause",
                    tint = onColor)
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Counter(tick: Event) {
    val onColor = tick.color.onColor()
    var animProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(tick) {
        animProgress = tick.currentProgress
        if (tick.currentProgress == 1f) {
            delay(900)
            animProgress = 0f
        }
    }
    val pulse = updateTransition(targetState = tick, label = "Pulse")

    val animRadius by pulse.animateFloat(transitionSpec = { keyframes {
        durationMillis = 500
        0f at 0
        0f at 50
        100f at 100
        300f at 200
        1000f at 300
    }}, label = "radius") {
        it
        2000f
    }

    val animColor by pulse.animateColor(transitionSpec = { keyframes {
        durationMillis = 600
        tick.color.shaded() at 0
        tick.color.shaded() at 100
    }}, label = "color") {
        it.color
    }

    val progress by animateFloatAsState(targetValue = animProgress,
        animationSpec = if (animProgress == 0f) snap()
        else tween(1000, easing = LinearEasing))

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = this.size.minDimension * 0.85f
            drawCircle(color = animColor, radius = animRadius)
            drawCircle(brush = Brush.radialGradient(0.96f to onColor,
                0.98f to onColor.copy(alpha = 0.1f),
                0.99f to Color.Transparent),
                radius = 1.15f * r / 2, style = Stroke(width = 0.03f * r / 2))
            drawCircle(brush = Brush.radialGradient(0.71f to Color.Transparent,
                0.72f to onColor.copy(alpha = 0.1f),
                0.74f to onColor),
                radius = 0.85f * r / 2, style = Stroke(width = 0.03f * r / 2))
            drawArc(color = onColor,
                startAngle = 270f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset((size.width - r)/2f, (size.height-r)/2f),
                size = Size(r, r), style = Stroke(width = 0.27f * r / 2))

        }
        Row {
            tick.currentSecondsLeft.durationText().forEach {
                AnimatedContent(targetState = it,
                    transitionSpec = {fadeIn() with fadeOut()}) { target ->
                    Text(text = target.toString(),
                        fontSize = dpToSp(dp = if (tick.currentSecondsLeft <= 99) 160.dp else 80.dp),
                        fontWeight = FontWeight.Bold,
                        color = onColor)
                }
            }
        }
    }
}

//---v2-design-ends---



fun pointerPath(size: Size) = Path().apply {
    moveTo(size.width / 2f, 0f)
    lineTo(size.width / 4f, size.height / 2f)
    quadraticBezierTo(size.width / 8f, 3f * size.height / 4f, size.width / 4f, 7f * size.height / 8f)
    quadraticBezierTo(size.width / 2f, 9f * size.height / 8f, 3f * size.width / 4f, 7f * size.height / 8f)
    quadraticBezierTo(7f * size.width / 8f, 3f * size.height / 4f, 3f * size.width / 4f, size.height / 2f)
    close()
}

class PointerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(path = pointerPath(size = size))
    }
}