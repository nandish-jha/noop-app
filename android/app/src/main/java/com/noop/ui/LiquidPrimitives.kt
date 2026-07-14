package com.noop.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Flat Boop-style circular progress — same call signature as the old liquid vessel
 * (no gloss / slosh / bloom). Call sites keep [LiquidVessel] without edits.
 */
@Composable
fun LiquidVessel(
    value: Double?,
    tint: Color,
    animated: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val frac = (value ?: 0.0).coerceIn(0.0, 1.0)
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val stroke = (size.minDimension * 0.085f).coerceIn(2.5.dp.toPx(), 10.dp.toPx())
        val d = size.minDimension - stroke
        val topLeft = Offset((size.width - d) / 2f, (size.height - d) / 2f)
        val arcSize = Size(d, d)
        val style = Stroke(width = stroke, cap = StrokeCap.Round)
        drawArc(
            color = Palette.surfaceOverlay,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style,
        )
        val sweep = (frac * 360.0).toFloat()
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

/** Flat Boop-style horizontal fill bar — replaces the liquid tube. */
@Composable
fun LiquidTube(
    frac: Double,
    tint: Color,
    height: Dp = 14.dp,
    animated: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val clamped = frac.coerceIn(0.0, 1.0).toFloat()
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val radius = CornerRadius(size.height / 2f, size.height / 2f)
        drawRoundRect(color = Palette.surfaceOverlay, cornerRadius = radius)
        if (clamped > 0f) {
            drawRoundRect(
                color = tint,
                size = Size(size.width * clamped, size.height),
                cornerRadius = radius,
            )
        }
    }
}

/** Fixed brand HR tint used by [LiquidThread] callers. */
val liquidHeartPink: Color = Color(red = 1f, green = 107f / 255f, blue = 129f / 255f, alpha = 1f)

/** Simple HR sparkline (no liquid glint / pulse). */
@Composable
fun LiquidThread(
    bpm: List<Double>,
    tint: Color = liquidHeartPink,
    height: Dp = 96.dp,
    animated: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        if (bpm.size < 2) return@Canvas
        val min = bpm.minOrNull() ?: return@Canvas
        val max = bpm.maxOrNull() ?: return@Canvas
        val span = (max - min).takeIf { it > 1e-6 } ?: 1.0
        val strokeW = 2.5.dp.toPx()
        val path = Path()
        bpm.forEachIndexed { i, v ->
            val x = size.width * (i / (bpm.size - 1f))
            val y = size.height * (1f - ((v - min) / span).toFloat()).coerceIn(0.05f, 0.95f)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))
    }
}

/**
 * Press response for tappable cards — scale + dim. Kept for call-site compat;
 * no liquid splash.
 */
fun Modifier.liquidPress(interactionSource: InteractionSource): Modifier = composed {
    val reduced = rememberReduceMotion()
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = if (reduced) tween(0) else tween(durationMillis = 160, easing = Motion.easeOut),
        label = "liquidPressScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (pressed) 0.86f else 1f,
        animationSpec = if (reduced) tween(0) else tween(durationMillis = 160, easing = Motion.easeOut),
        label = "liquidPressAlpha",
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

@Composable
fun CountUpNumber(
    value: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = NoopType.number(26f),
    color: Color = Palette.textPrimary,
) {
    CountUpText(
        value = value,
        format = { "${Math.round(it)}" },
        style = style,
        color = color,
        modifier = modifier,
    )
}
