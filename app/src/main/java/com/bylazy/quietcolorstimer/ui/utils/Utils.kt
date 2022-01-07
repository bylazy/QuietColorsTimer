package com.bylazy.quietcolorstimer.ui.utils

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bylazy.quietcolorstimer.ui.theme.QuietColorsTimerTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ListBlock(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .border(
                shape = MaterialTheme.shapes.medium, width = 1.dp,
                color = MaterialTheme.colors.onSurface
            )
            .padding(4.dp)
    ) {
        content()
    }
}

@Composable
fun ListItemCard(modifier: Modifier = Modifier, content: @Composable () -> Unit){
    Card(
        modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = MaterialTheme.shapes.large,
        elevation = 4.dp) {
        Column(modifier.padding(4.dp)) {
            content()
        }
    }
}

@Composable
fun RoundIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    description: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier
            .size(50.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = MaterialTheme.colors.secondary.copy(alpha = 0.3F)
                ),
                onClick = onClick
            )
            .clip(shape = CircleShape)
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = imageVector,
            contentDescription = description,
            tint = MaterialTheme.colors.onSecondary)
    }
}

@Composable
fun RoundIconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    description: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier
            .size(50.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = MaterialTheme.colors.secondary.copy(alpha = 0.3F)
                ),
                onClick = onClick
            )
            .clip(shape = CircleShape)
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painter,
            contentDescription = description,
            tint = MaterialTheme.colors.onSecondary)
    }
}

@Composable
fun OvalIconButton(
    modifier: Modifier = Modifier,
    caption: String,
    imageVector: ImageVector,
    description: String = "",
    onClick: () -> Unit
){
    Box(
        modifier
            .height(50.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = MaterialTheme.colors.secondary.copy(alpha = 0.3F)
                ),
                onClick = onClick
            )
            .clip(shape = RoundedCornerShape(50))
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = imageVector,
                contentDescription = description,
                tint = MaterialTheme.colors.onSecondary)
            Spacer(modifier = Modifier.size(2.dp))
            Text(text = caption, style = MaterialTheme.typography.button)
        }
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
                        Text(text = "Ok")
                    }
                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun BlockPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            ListBlock {
                Text(text = "Some Text")
                Spacer(modifier = Modifier.size(4.dp))
                Button(onClick = { }) {
                    Text(text = "Some Button")
                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun RoundButtonPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            RoundIconButton(imageVector = Icons.Default.Build) {}
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun OvalButtonPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            OvalIconButton(caption = "Button", imageVector = Icons.Default.Build) {}
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            ListItemCard() {                
                Column() {
                    Text(text = "Some Text")
                    Spacer(modifier = Modifier.size(4.dp))
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Some Button")
                    }
                }
            }
        }
    }
}

