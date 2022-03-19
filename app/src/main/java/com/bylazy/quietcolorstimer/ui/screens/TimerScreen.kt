package com.bylazy.quietcolorstimer.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bylazy.quietcolorstimer.R
import com.bylazy.quietcolorstimer.data.*
import com.bylazy.quietcolorstimer.ui.utils.RoundIconButton
import com.bylazy.quietcolorstimer.ui.utils.dpToSp
import kotlinx.coroutines.delay


@Composable
fun TimerScreen(viewModel: TimerViewModel,
                navController: NavController,
                keepScreenOn: (Boolean) -> Unit,
                adjustBrightness: (IntervalType) -> Unit) {
    val loading by viewModel.loading
    if (loading) CircularProgressIndicator() else TimerStartedScreen(viewModel, navController, adjustBrightness)
    DisposableEffect(true){
        keepScreenOn(true)
        onDispose { keepScreenOn(false) }
    }
}

@Composable
fun TimerStartedScreen(viewModel: TimerViewModel,
                       navController: NavController,                       
                       adjustBrightness: (IntervalType) -> Unit){

    val currentState by viewModel.ticks.collectAsState(initial = START_EVENT)
    val paused by viewModel.pause
    val orientation = LocalConfiguration.current.orientation
    LaunchedEffect(key1 = currentState.duration) {
        if (currentState == FINISH_EVENT) {
            delay(3000)
            navController.popBackStack()
        }
    }
    val color by animateColorAsState(targetValue = currentState.color)
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = color)) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Column(modifier = Modifier.fillMaxSize()) {
                Counter(modifier = Modifier.weight(0.5f), currentState)
                Details(modifier = Modifier.weight(0.5f),
                    currentState,
                    viewModel.weights,
                    paused,
                    { navController.popBackStack() },
                    viewModel::skipInterval,
                    viewModel::pauseInterval)
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Counter(modifier = Modifier.weight(0.5f), currentState)
                Details(modifier = Modifier.weight(0.5f),
                    currentState,
                    viewModel.weights,
                    paused,
                    { navController.popBackStack() },
                    viewModel::skipInterval,
                    viewModel::pauseInterval)
            }
        }
    }    
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Counter(modifier: Modifier = Modifier, current: Event){
    var tick by remember { mutableStateOf(false)}
    LaunchedEffect(key1 = current) {
        tick = !tick
        delay(480)
        tick = !tick
    }
    val textColor = if (current.color.luminance() >= 0.5f) Color.Black else Color.White
    val transition = updateTransition(targetState = tick, label = "Ticker")

    val pulse by transition.animateFloat(label = "Pulse",
        transitionSpec = { if (targetState) spring()
            else tween(400)
        }) {
        if (it) 0f else 1f
    }
    val progress by animateFloatAsState(targetValue = current.currentProgress,
        animationSpec = tween(990, easing = LinearEasing))

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val r = this.size.minDimension * 0.85f
            drawArc(brush = Brush.radialGradient(0.67f+0.02f*pulse to Color.Transparent,
                0.7f+0.02f*pulse to textColor.copy(alpha = 0.2f),
                0.75f+0.02f*pulse to textColor,
                0.9f-0.02f*pulse to textColor,
                0.95f-0.02f*pulse to textColor.copy(alpha = 0.2f),
                0.98f-0.02f*pulse to Color.Transparent),
                startAngle = 270f,
                sweepAngle = 360f*progress,
                useCenter = false,
                topLeft = Offset((size.width - r)/2f, (size.height - r)/2),
                size = Size(r, r),
                style = Stroke(width = r*.2f))
        }
        Row {
            current.currentSecondsLeft.durationText().forEach {
                AnimatedContent(targetState = it,
                    transitionSpec = {fadeIn() with fadeOut()}) { target ->
                    Text(text = target.toString(),
                        fontSize = dpToSp(dp = 80.dp),
                        fontWeight = FontWeight.Bold,
                        color = textColor)
                }
            }
        }

    }
    
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Details(modifier: Modifier = Modifier,
            current: Event,
            weights: List<Pair<Float, Color>>,
            paused: Boolean = false,
            onQuit: () -> Unit,
            onSkip: () -> Unit,
            onPause: () -> Unit){
    val textColor = if (current.color.luminance() >= 0.5f) Color.Black else Color.White

    val progress = animateFloatAsState(targetValue = current.overallProgress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)).value

    Column(
        modifier
            .fillMaxWidth()
            .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = current.interval, color = textColor, fontSize = dpToSp(dp = 56.dp))
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Next: ${current.next}", color = textColor, fontSize = dpToSp(dp = 16.dp))
        Spacer(modifier = Modifier.size(16.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .padding(start = 8.dp, end = 8.dp)
            .clip(shape = RoundedCornerShape(50))
            .border(width = 1.dp, shape = RoundedCornerShape(50), color = textColor)) {
            weights.forEach { 
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .background(color = it.second)
                    .weight(it.first))
            }
        }
        BoxWithConstraints(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)) {
            Box(modifier = Modifier
                .offset(this.maxWidth * progress)
                .size(16.dp)
                .clip(shape = PointerShape())
                .background(color = textColor))
        }

        Spacer(modifier = Modifier.size(12.dp))
        if (current == FINISH_EVENT) {Text(text = "---", color = textColor, fontSize = dpToSp(dp = 20.dp))}
        else Row {
            AnimatedContent(targetState = current.overallSeconds, transitionSpec = {
                (slideInVertically(initialOffsetY = { height -> height }) + fadeIn() with
                        slideOutVertically(targetOffsetY =  { height -> -height }) + fadeOut())
                    .using(sizeTransform = SizeTransform(clip = false))
            }) { target ->
                Text(text = target.toString(), color = textColor, fontSize = dpToSp(dp = 20.dp))
            }
            Text(text = " of ${current.overallDuration}", color = textColor, fontSize = dpToSp(dp = 20.dp))
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text(text = current.timer, color = textColor, fontSize = dpToSp(dp = 16.dp))
        Spacer(modifier = Modifier
            .size(12.dp)
            .weight(1f))
        Row {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = textColor, shape = CircleShape)
                    .clickable { onQuit() }, contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Exit", tint = textColor)
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = textColor, shape = CircleShape)
                    .clickable { onSkip() }, contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Skip", tint = textColor)
            }
            Spacer(modifier = Modifier.weight(1f)) //todo icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = textColor, shape = CircleShape)
                    .clickable { onPause() }, contentAlignment = Alignment.Center
            ) {
                Icon(painter = if (paused) painterResource(id = R.drawable.ic_play)
                else painterResource(id = R.drawable.is_pause),
                    contentDescription = "Pause",
                    tint = textColor)
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
    }
}

fun pointerPath(size: Size) = Path().apply {
    moveTo(size.width / 2f, 0f)
    lineTo(size.width / 4f, size.height / 2f)
    quadraticBezierTo(size.width / 8f, 3f * size.height / 4f, size.width / 4f, 7f * size.height / 8f)
    quadraticBezierTo(size.width / 2f, 9f * size.height / 8f, 3f * size.width / 4f, 7f * size.height / 8f)
    quadraticBezierTo(7f * size.width / 8f, 3f * size.height / 4f, 3f * size.width / 4f, size.height / 2f)
    close()
}

class PointerShape(): Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(path = pointerPath(size = size))
    }
}