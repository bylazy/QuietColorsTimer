package com.bylazy.quietcolorstimer.ui.utils

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ListItemCard(modifier: Modifier = Modifier, content: @Composable () -> Unit){
    Card(
        modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = 2.dp) {
        content()
    }
}

@Composable
fun RepeatingClickableButton(
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    initialDelay: Long = 200,
    minDelay: Long = 20,
    delayDecay: Float = 0.9F,
    content: @Composable RowScope.() -> Unit
) {
    val currentLongClickListener by rememberUpdatedState(newValue = onLongClick)

    Button(
        onClick = { },
        modifier.pointerInput(interactionSource, enabled) {
            forEachGesture {
                coroutineScope {
                    awaitPointerEventScope {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val job = launch {
                            var currentDelay = initialDelay
                            while (down.pressed && enabled) {
                                currentLongClickListener()
                                delay(currentDelay)
                                currentDelay = (currentDelay.toFloat() * delayDecay)
                                    .toLong().coerceAtLeast(minDelay)
                            }
                        }
                        waitForUpOrCancellation()
                        job.cancel()
                    }
                }
            }
        },
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun ExpandableBlock(expanded: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(visible = expanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        content()
    }
}

@ExperimentalAnimationApi
@Composable
fun FadingBlock(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(visible = visible,
        enter = fadeIn(), exit = fadeOut()) {
        content()
    }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

@Composable
fun ColorDialog(color: Color, onCancel: () -> Unit, onOk: (Color) -> Unit) {
    var r by remember { mutableStateOf(color.red) }
    var g by remember { mutableStateOf(color.green) }
    var b by remember { mutableStateOf(color.blue) }
    Dialog(onDismissRequest = onCancel) {
        Card(elevation = 4.dp, shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(4.dp)) {
                Text(text = "Select Custom Color:", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Red: ${(r * 255).toInt()}")
                Spacer(modifier = Modifier.size(2.dp))
                Slider(value = r, onValueChange = { r = it }, valueRange = 0F..1F)
                Spacer(modifier = Modifier.size(2.dp))
                Text(text = "Green: ${(g * 255).toInt()}")
                Spacer(modifier = Modifier.size(2.dp))
                Slider(value = g, onValueChange = { g = it }, valueRange = 0F..1F)
                Spacer(modifier = Modifier.size(2.dp))
                Text(text = "Blue: ${(b * 255).toInt()}")
                Spacer(modifier = Modifier.size(2.dp))
                Slider(value = b, onValueChange = { b = it }, valueRange = 0F..1F)
                Spacer(modifier = Modifier.size(10.dp))
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color(r, g, b))
                )
                Spacer(modifier = Modifier.size(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onCancel) {
                        Text(text = "Cancel")
                    }
                    Spacer(
                        modifier = Modifier
                            .size(4.dp)
                            .weight(1F)
                    )
                    Button(onClick = { onOk(Color(r, g, b)) }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}



@Composable
fun CombinedColorDialog(color: Color,
                        onChange: (Color) -> Unit,
                        onCancel: () -> Unit) {
    var hsvMode by remember { mutableStateOf(false) }
    var currentColor by remember { mutableStateOf(color) }
    Dialog(onDismissRequest = onCancel) {
        Card(shape = RoundedCornerShape(12.dp), elevation = 4.dp) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Select Color:")
                Spacer(modifier = Modifier.size(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier
                        .size(8.dp))
                    RadioButton(selected = !hsvMode, onClick = { hsvMode = false })
                    Spacer(modifier = Modifier
                        .size(8.dp))
                    Text(text = "RGB")
                    Spacer(modifier = Modifier
                        .size(8.dp)
                        .weight(1f))
                    RadioButton(selected = hsvMode, onClick = { hsvMode = true })
                    Spacer(modifier = Modifier
                        .size(8.dp))
                    Text(text = "HSV")
                    Spacer(modifier = Modifier
                        .size(8.dp))
                }
                Spacer(modifier = Modifier.size(8.dp))
                if (hsvMode) {
                    Text("Under construction") // todo here we go
                }
                else {
                    Text("Under construction")
                }
                Spacer(modifier = Modifier.size(8.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(color = currentColor))
                Spacer(modifier = Modifier.size(8.dp))
                Row {
                    Spacer(modifier = Modifier
                        .size(8.dp)
                        .weight(1f))
                    Button(onClick = onCancel) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier
                        .size(8.dp)
                        .weight(1f))
                    Button(onClick = { onChange(currentColor) }) {
                        Text(text = "Select")
                    }
                    Spacer(modifier = Modifier
                        .size(8.dp)
                        .weight(1f))
                }
            }
        }
    }
}

//---v2---design---end

@Composable
fun FilledIconButton(modifier: Modifier,
                     onClick: () -> Unit,
                     text: String,
                     imageVector: ImageVector,
                     contentDescription: String = "") {
    Row(modifier
        .height(40.dp)
        .clip(RoundedCornerShape(50))
        .clickable { onClick() }
        .background(color = MaterialTheme.colors.secondary, shape = RoundedCornerShape(50))
        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = text, style = MaterialTheme.typography.button)
    }
}

@Composable
fun RoundIconButton(onClick: () -> Unit, painter: Painter, desc: String) {
    Box(modifier = Modifier
        .size(40.dp)
        .clip(shape = CircleShape)
        .clickable {
            onClick()
        }
        .background(
            color = MaterialTheme.colors.secondary,
            shape = CircleShape
        ),
        contentAlignment = Alignment.Center) {
        Icon(
            painter = painter,
            contentDescription = desc,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun RoundIconButton(onClick: () -> Unit, imageVector: ImageVector, desc: String) {
    Box(modifier = Modifier
        .size(40.dp)
        .clip(shape = CircleShape)
        .clickable {
            onClick()
        }
        .background(
            color = MaterialTheme.colors.secondary,
            shape = CircleShape
        ),
        contentAlignment = Alignment.Center) {
        Icon(
            imageVector = imageVector,
            contentDescription = desc,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ColorScale(modifier: Modifier, hue: Float, onChange: (Float) -> Unit) {
    val spectre = remember {Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF0000),
            Color(0xFFFFFF00), Color(0xFF00FF00), Color(0xFF00FFFF),
            Color(0xFF0000FF), Color(0xFFFF00FF), Color(0xFFFF0000)
        )
    )}
    Canvas(modifier = modifier
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    val down = awaitFirstDown()
                    onChange(360f * down.position.y / size.height)
                    drag(down.id) { change ->
                        change.consumeAllChanges()
                        onChange(360f * change.position.y / size.height)
                    }
                }
            }
        }) {
        drawRect(brush = spectre)
        drawRect(color = Color.Black,
            topLeft = Offset(0f, this.size.height * hue / 360f - 15f),
            size = Size(this.size.width, 30f),
            style = Stroke(width = 5f)
        )}
}

@Composable
fun SVBox(modifier: Modifier = Modifier,
          hue: Float,
          s: Float,
          v: Float,
          onChange: (s: Float, v: Float) -> Unit) {
    val bwBrush = remember { Brush.verticalGradient(listOf(Color.White, Color.Black)) }
    val colorBrush = remember(hue) {
        val rgb = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f)))
        Brush.horizontalGradient(listOf(Color.White, rgb))
    }
    Canvas(modifier = modifier.pointerInput(Unit){
        forEachGesture {
            awaitPointerEventScope {
                val down = awaitFirstDown()
                onChange(1f / size.width * down.position.x,
                    1f - 1f / size.height * down.position.y)
                drag(down.id) { change ->
                    onChange(1f / size.width * change.position.x,
                        1f - 1f / size.height * change.position.y)
                }
            }
        }
    }) {
        drawRect(bwBrush)
        drawRect(colorBrush, blendMode = BlendMode.Modulate)
        val center = Offset(x = s * this.size.width, y = (1f - v) * this.size.height)
        drawCircle(color = Color.Black,
            radius = 30f,
            center = center,
            style = Stroke(width = 5f))
        drawCircle(color = Color.White,
            radius = 25f,
            center = center,
            style = Stroke(width = 5f))
    }
}

//TODO - add to dialog - v1 future

@Composable
fun ColorPicker(modifier: Modifier = Modifier, color: Color, onChange: (Color) -> Unit) {
    var hue by remember(color) { mutableStateOf(0f)}
    var s by remember(color) { mutableStateOf(0f)}
    var v by remember(color) { mutableStateOf(0f)}
    LaunchedEffect(key1 = color) {
        val hsv = floatArrayOf(0f, 0f, 0f)
        android.graphics.Color.RGBToHSV(
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt(),
            hsv
        )
        hue = hsv[0]
        s = hsv[1]
        v = hsv[2]
    }
    Column {
        Row(modifier = modifier) {
            Box(modifier = Modifier.weight(0.8f)) {
                SVBox(modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                    hue = hue,
                    s = s,
                    v = v,
                    onChange = {ns, nv ->
                        s = ns
                        v = nv
                        onChange(onColorDataChanged(hue, s, v))})
            }
            Spacer(modifier = Modifier.size(8.dp))
            Box(modifier = Modifier.weight(0.2f)) {
                ColorScale(modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                    hue = hue,
                    onChange = {hue = it; onChange(onColorDataChanged(hue, s, v))})
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, s, v)))))
    }
}

internal fun onColorDataChanged(h: Float, s: Float, v: Float): Color {
    return Color(android.graphics.Color.HSVToColor(floatArrayOf(h, s, v)))
}

//---v2---design---end

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun ColorScalePreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            Box(modifier = Modifier
                .width(40.dp)
                .height(200.dp)) {
                ColorScale(modifier = Modifier, hue = 120f, onChange = {})
            }
        }
    }
}




