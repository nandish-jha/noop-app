package com.noop.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Flat Boop-style progress ring — track + arc, no liquid fill / gloss / glow.
 * [fraction] is 0..1; empty rings still draw the track.
 */
@Composable
fun StatRing(
    fraction: Double?,
    tint: Color,
    diameter: Dp,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = (diameter.value * 0.085f).coerceIn(2.5f, 10f).dp,
    trackColor: Color = Palette.surfaceOverlay,
) {
    val sweep = ((fraction ?: 0.0).coerceIn(0.0, 1.0) * 360.0).toFloat()
    Canvas(modifier = modifier.size(diameter)) {
        val stroke = strokeWidth.toPx()
        val d = size.minDimension - stroke
        val topLeft = Offset((size.width - d) / 2f, (size.height - d) / 2f)
        val arcSize = Size(d, d)
        val style = Stroke(width = stroke, cap = StrokeCap.Round)
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style,
        )
        if (sweep > 0f) {
            drawArc(
                color = tint,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = style,
            )
        }
    }
}
