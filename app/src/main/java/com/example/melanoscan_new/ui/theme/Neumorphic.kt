package com.example.melanoscan_new.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neumorphic(
    cornerRadius: Dp = 16.dp,
    lightShadowColor: Color = Color.White,
    darkShadowColor: Color = Color(0xFFA8B5C7),
    shadowOffset: Dp = 6.dp
) = this.then(
    Modifier.drawBehind {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        val lightShadowOffset = shadowOffset.toPx()
        val darkShadowOffset = shadowOffset.toPx()

        // Light shadow (top-left)
        frameworkPaint.color = lightShadowColor.toArgb()
        frameworkPaint.setShadowLayer(
            10f,
            -lightShadowOffset,
            -lightShadowOffset,
            lightShadowColor.toArgb()
        )
        drawIntoCanvas {
            it.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = cornerRadius.toPx(),
                radiusY = cornerRadius.toPx(),
                paint = paint
            )
        }

        // Dark shadow (bottom-right)
        frameworkPaint.color = darkShadowColor.toArgb()
        frameworkPaint.setShadowLayer(
            15f,
            darkShadowOffset,
            darkShadowOffset,
            darkShadowColor.toArgb()
        )
        drawIntoCanvas {
            it.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = cornerRadius.toPx(),
                radiusY = cornerRadius.toPx(),
                paint = paint
            )
        }
    }
)
