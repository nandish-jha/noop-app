package com.noop.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

// MARK: - NOOP redesign tokens (from Noop Redesign - Standalone.html)
//
// Warm terracotta canvas, coral Charge accent, Manrope/Outfit-inspired typography weights.

object Redesign {
    // Aligned with Boop terracotta dark (`boopTerracottaDarkPalette`).
    val canvas = Color(0xFF141413)
    val canvasDeep = Color(0xFF141413)
    val phone = Color(0xFF1A1918)
    val card = Color(0xFF30302E)
    val cardAlt = Color(0xFF252320)
    val cream = Color(0xFFFAF9F5)
    val peach = Color(0xFFE8A898)
    val coral = Color(0xFFE88868)
    val coralActive = Color(0xFFE88868)
    val amber = Color(0xFFD46E48)
    val effort = Color(0xFFE8A898)
    val effortBg = Color(0x28E8A898)
    val rest = Color(0xFFC77E92)
    val restBg = Color(0x29C77E92)
    val strapBg = Color(0x14FAF9F5)
    val ringTrack = Color(0xFF3D3D3A)
    val positive = Color(0xFFB7D18A)
    val positiveBg = Color(0x299CB86B)
    val headerGradientTop = Color(0xFF252320)
    val navBar = Color(0xD91A1918)
    val navBorder = Color(0x24E8A898)
    val muted = Color(0xFFB0AEA5)
    val navUnselected = Color(0xFF8A8480)
    val heroRadius = 28.dp
    val cardRadius = 16.dp
    val pillRadius = 100.dp
    val dockRadius = 22.dp
}

@Composable
fun RedesignTopBar(
    batteryPct: Double?,
    onOpenSettings: () -> Unit,
    onOpenDevices: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Redesign.headerGradientTop, Redesign.phone),
                ),
            )
            .padding(top = 58.dp, start = 20.dp, end = 20.dp, bottom = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RedesignBatteryPill(
                batteryPct = batteryPct,
                onClick = onOpenDevices,
            )
            Text(
                "noop",
                style = NoopType.title2.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                ),
                color = Redesign.cream,
            )
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Redesign.amber)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenSettings,
                    )
                    .semantics { contentDescription = "Profile and settings" },
                contentAlignment = Alignment.Center,
            ) {
                ProfileAvatar(size = 34.dp)
            }
        }
    }
}

@Composable
private fun RedesignBatteryPill(batteryPct: Double?, onClick: () -> Unit) {
    val label = batteryPct?.let { "${it.roundToInt()}%" } ?: "Strap"
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Redesign.pillRadius))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics {
                contentDescription = batteryPct?.let { "Strap battery $it percent" } ?: "Strap battery"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.Transparent)
                    .then(
                        Modifier
                            .padding(0.dp)
                    ),
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawRoundRect(
                        color = Redesign.peach,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                        style = Stroke(width = 1.4.dp.toPx()),
                    )
                    drawRoundRect(
                        color = Redesign.peach,
                        topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                        size = Size(size.width - 4.dp.toPx(), size.height - 2.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx()),
                    )
                }
            }
            Text(
                label,
                style = NoopType.caption.copy(fontWeight = FontWeight.SemiBold),
                color = Redesign.peach,
            )
    }
}

@Composable
fun RedesignHeroRow(
    charge: Double?,
    chargeCaption: String?,
    effort: Double?,
    effortScale: EffortScale,
    rest: Double?,
    strapBattery: Double?,
    onChargeTap: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val effortOut = if (effortScale == EffortScale.WHOOP) 21.0 else 100.0
    val effortVal = effort?.let { UnitFormatter.effortValue(it, effortScale) }
    val effortLabel = effortVal?.let {
        if (effortScale == EffortScale.WHOOP) String.format(Locale.US, "%.1f", it)
        else it.toInt().toString()
    } ?: "—"

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .width(186.dp)
                .clip(RoundedCornerShape(Redesign.heroRadius))
                .background(Redesign.card)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .then(
                    if (onChargeTap != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onChargeTap,
                        )
                    } else Modifier
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                RedesignCoralRing(
                    fraction = ((charge ?: 0.0) / 100.0).coerceIn(0.0, 1.0).toFloat(),
                    size = 140.dp,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        charge?.roundToInt()?.toString() ?: "—",
                        style = NoopType.number(34f, weight = FontWeight.ExtraBold),
                        color = Redesign.cream,
                        lineHeight = 34.sp,
                    )
                    Text(
                        "Charge",
                        style = NoopType.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = Redesign.muted,
                    )
                }
            }
            chargeCaption?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    style = NoopType.caption,
                    color = Redesign.muted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            RedesignSideStat(label = "Effort", value = effortLabel, bg = Redesign.effortBg, accent = Redesign.effort)
            RedesignSideStat(
                label = "Rest",
                value = rest?.roundToInt()?.toString() ?: "—",
                bg = Redesign.restBg,
                accent = Redesign.rest,
            )
            RedesignSideStat(
                label = "Strap",
                value = strapBattery?.roundToInt()?.let { "$it%" } ?: "—",
                bg = Redesign.strapBg,
                accent = Redesign.muted,
            )
        }
    }
}

@Composable
private fun RedesignCoralRing(fraction: Float, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val stroke = 12.dp.toPx()
        val radius = (this.size.minDimension - stroke) / 2f
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2f, radius * 2f)
        val cap = Stroke(width = stroke, cap = StrokeCap.Round)
        drawCircle(color = Redesign.ringTrack, radius = radius, center = center, style = cap)
        if (fraction > 0f) {
            drawArc(
                color = Redesign.coral,
                startAngle = -90f,
                sweepAngle = 360f * fraction,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = cap,
            )
        }
    }
}

@Composable
private fun RedesignSideStat(label: String, value: String, bg: Color, accent: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(Redesign.cardRadius))
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(label, style = NoopType.caption.copy(fontWeight = FontWeight.SemiBold), color = accent)
        Text(
            value,
            style = NoopType.number(22f, weight = FontWeight.ExtraBold),
            color = Redesign.cream,
        )
    }
}

@Composable
fun RedesignActionRow(
    onStart: () -> Unit,
    onSecondary: () -> Unit,
    secondaryLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(Redesign.pillRadius))
                .background(Redesign.coral)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onStart,
                )
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Redesign.canvas, modifier = Modifier.size(18.dp))
                Text("Start", style = NoopType.body.copy(fontWeight = FontWeight.Bold), color = Redesign.canvas)
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(Redesign.pillRadius))
                .background(Redesign.cardAlt)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSecondary,
                )
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(secondaryLabel, style = NoopType.body.copy(fontWeight = FontWeight.Bold), color = Redesign.peach)
        }
    }
}

@Composable
fun RedesignSectionHeader(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            title,
            style = NoopType.title2.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
            color = Redesign.cream,
        )
        if (action != null && onAction != null) {
            Text(
                action,
                style = NoopType.subhead.copy(fontWeight = FontWeight.Bold),
                color = Redesign.coralActive,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAction,
                ),
            )
        }
    }
}

@Composable
fun RedesignChipRow(
    chips: List<Pair<String, ImageVector>>,
    onChip: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        chips.forEachIndexed { index, (label, icon) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(Redesign.cardRadius))
                    .background(Redesign.card)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onChip(index) },
                    )
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(icon, contentDescription = null, tint = Redesign.peach, modifier = Modifier.size(22.dp))
                Text(label, style = NoopType.caption.copy(fontWeight = FontWeight.SemiBold), color = Redesign.muted)
            }
        }
    }
}

@Composable
fun RedesignListCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(Redesign.cardRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Redesign.card)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                } else Modifier
            )
            .padding(14.dp),
    ) {
        content()
    }
}

fun chargeRecoveryCaption(score: Double?): String? = when {
    score == null -> null
    score >= 67 -> "Well recovered, ready to push"
    score >= 34 -> "Moderate recovery — pace yourself"
    else -> "Low recovery — prioritize rest"
}
