package com.bylazy.quietcolorstimer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.bylazy.quietcolorstimer.db.test_timer_1_intervals
import com.bylazy.quietcolorstimer.ui.screens.IntervalBox

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(2.dp)
)

class CustomShape(private val radius: Dp) : Shape{
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = density.run { radius.toPx() }
        return Outline.Generic(
            path = Path().apply {
                reset()
                moveTo(0f,0f)
                lineTo(size.width/2 - r, 0f)
                arcTo(
                    rect = Rect(topLeft = Offset(size.width/2 - r, -r),
                        bottomRight = Offset(size.width/2 + r, r)),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        )
    }
}

@Composable
fun MyShape() {
    Box(modifier = Modifier.fillMaxWidth().height(50.dp)
        .background(color = Color.Green, shape = CustomShape(25.dp)))
}

@Preview(showBackground = false)
@Composable
fun IntervalBoxPreview() {
    QuietColorsTimerTheme {
        Surface(color = MaterialTheme.colors.background) {
            MyShape()
        }
    }
}