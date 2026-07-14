package com.noop.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.noop.R
import com.noop.ui.PaletteTokens
import java.text.DateFormat
import java.util.Date

/**
 * Home-screen widget: today's three top scores (Rest · Charge · Effort, Charge centred), with live HR
 * and strap battery at a glance (#516). Colours follow Appearance + Boop palette via [resolveWidgetTokens].
 */
class NoopGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snap = runCatching { WidgetSnapshotStore.load(context) }.getOrDefault(WidgetSnapshot())
        val tokens = resolveWidgetTokens(context)
        provideContent { ScoresWidgetContent(snap, tokens) }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable,
    ) {
        runCatching {
            val rv = android.widget.RemoteViews(context.packageName, R.layout.noop_widget_error)
            android.appwidget.AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, rv)
        }
    }
}

/** Compact Charge-only tile (1×1). */
class NoopChargeGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snap = runCatching { WidgetSnapshotStore.load(context) }.getOrDefault(WidgetSnapshot())
        val tokens = resolveWidgetTokens(context)
        provideContent { ChargeWidgetContent(snap, tokens) }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable,
    ) {
        runCatching {
            val rv = android.widget.RemoteViews(context.packageName, R.layout.noop_widget_error)
            android.appwidget.AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, rv)
        }
    }
}

/** Live heart rate + strap battery (2×1). */
class NoopLiveGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snap = runCatching { WidgetSnapshotStore.load(context) }.getOrDefault(WidgetSnapshot())
        val tokens = resolveWidgetTokens(context)
        provideContent { LiveWidgetContent(snap, tokens) }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable,
    ) {
        runCatching {
            val rv = android.widget.RemoteViews(context.packageName, R.layout.noop_widget_error)
            android.appwidget.AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, rv)
        }
    }
}

/** Last night: Rest score + sleep duration (2×2). */
class NoopNightGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snap = runCatching { WidgetSnapshotStore.load(context) }.getOrDefault(WidgetSnapshot())
        val tokens = resolveWidgetTokens(context)
        provideContent { NightWidgetContent(snap, tokens) }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable,
    ) {
        runCatching {
            val rv = android.widget.RemoteViews(context.packageName, R.layout.noop_widget_error)
            android.appwidget.AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, rv)
        }
    }
}

/** Overnight vitals: HRV, resting HR, steps, Charge (2×2). */
class NoopVitalsGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snap = runCatching { WidgetSnapshotStore.load(context) }.getOrDefault(WidgetSnapshot())
        val tokens = resolveWidgetTokens(context)
        provideContent { VitalsWidgetContent(snap, tokens) }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable,
    ) {
        runCatching {
            val rv = android.widget.RemoteViews(context.packageName, R.layout.noop_widget_error)
            android.appwidget.AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, rv)
        }
    }
}

@Composable
private fun ScoresWidgetContent(snap: WidgetSnapshot, tokens: PaletteTokens) {
    val textPrimary = ColorProvider(tokens.textPrimary)
    val textSecondary = ColorProvider(tokens.textSecondary)
    WidgetShell(tokens) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.Bottom,
        ) {
            ScoreCell(
                label = "REST",
                pct = snap.restPct,
                color = snap.restPct?.let { ColorProvider(widgetBandColor(it, tokens)) } ?: textSecondary,
                valueSize = 22.sp,
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
            ScoreCell(
                label = "CHARGE",
                pct = snap.recoveryPct,
                color = snap.recoveryPct?.let { ColorProvider(widgetBandColor(it, tokens)) }
                    ?: textSecondary,
                valueSize = 30.sp,
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
            ScoreCell(
                label = "EFFORT",
                pct = snap.effortPct,
                color = snap.effortPct?.let { ColorProvider(tokens.effortColor) } ?: textSecondary,
                valueSize = 22.sp,
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = snap.heartRate?.let { "♥ $it" } ?: "♥ - ",
                style = TextStyle(color = textPrimary, fontSize = 13.sp, fontFamily = FontFamily.SansSerif),
            )
            Spacer(modifier = GlanceModifier.width(10.dp))
            Text(
                text = snap.batteryPct?.let { "⚡ $it%" } ?: "⚡ - ",
                style = TextStyle(color = textPrimary, fontSize = 13.sp, fontFamily = FontFamily.SansSerif),
            )
        }
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = statusLine(snap),
            style = TextStyle(color = textSecondary, fontSize = 11.sp, fontFamily = FontFamily.SansSerif),
        )
    }
}

@Composable
private fun ChargeWidgetContent(snap: WidgetSnapshot, tokens: PaletteTokens) {
    val textSecondary = ColorProvider(tokens.textSecondary)
    val valueColor = snap.recoveryPct?.let { ColorProvider(widgetBandColor(it, tokens)) }
        ?: textSecondary
    WidgetShell(tokens) {
        WidgetLabel("CHARGE", textSecondary)
        Spacer(modifier = GlanceModifier.height(4.dp))
        WidgetValue(snap.recoveryPct?.let { "$it%" } ?: "—", valueColor, 36.sp)
    }
}

@Composable
private fun LiveWidgetContent(snap: WidgetSnapshot, tokens: PaletteTokens) {
    val textPrimary = ColorProvider(tokens.textPrimary)
    val textSecondary = ColorProvider(tokens.textSecondary)
    val hrColor = ColorProvider(tokens.metricRose)
    val battColor = ColorProvider(tokens.metricAmber)
    WidgetShell(tokens) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WidgetLabel("HEART", textSecondary)
                WidgetValue(snap.heartRate?.toString() ?: "—", hrColor, 28.sp)
                Text(
                    text = "bpm",
                    style = TextStyle(color = textSecondary, fontSize = 10.sp, fontFamily = FontFamily.SansSerif),
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WidgetLabel("BATTERY", textSecondary)
                WidgetValue(snap.batteryPct?.let { "$it%" } ?: "—", battColor, 28.sp)
                Text(
                    text = if (snap.connected) "live" else "strap",
                    style = TextStyle(color = textSecondary, fontSize = 10.sp, fontFamily = FontFamily.SansSerif),
                )
            }
        }
        Spacer(modifier = GlanceModifier.height(6.dp))
        Text(
            text = statusLine(snap),
            style = TextStyle(color = textPrimary, fontSize = 11.sp, fontFamily = FontFamily.SansSerif),
        )
    }
}

@Composable
private fun NightWidgetContent(snap: WidgetSnapshot, tokens: PaletteTokens) {
    val textSecondary = ColorProvider(tokens.textSecondary)
    val restColor = snap.restPct?.let { ColorProvider(widgetBandColor(it, tokens)) }
        ?: ColorProvider(tokens.restColor)
    val sleepColor = ColorProvider(tokens.sleepDeep)
    WidgetShell(tokens) {
        WidgetLabel("LAST NIGHT", textSecondary)
        Spacer(modifier = GlanceModifier.height(6.dp))
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WidgetLabel("REST", textSecondary)
                WidgetValue(snap.restPct?.let { "$it%" } ?: "—", restColor, 30.sp)
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WidgetLabel("ASLEEP", textSecondary)
                WidgetValue(formatSleepDuration(snap.sleepMin), sleepColor, 22.sp)
            }
        }
        Spacer(modifier = GlanceModifier.height(6.dp))
        Text(
            text = statusLine(snap),
            style = TextStyle(color = textSecondary, fontSize = 11.sp, fontFamily = FontFamily.SansSerif),
        )
    }
}

@Composable
private fun VitalsWidgetContent(snap: WidgetSnapshot, tokens: PaletteTokens) {
    val textSecondary = ColorProvider(tokens.textSecondary)
    WidgetShell(tokens) {
        WidgetLabel("VITALS", textSecondary)
        Spacer(modifier = GlanceModifier.height(6.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            VitalCell(
                label = "HRV",
                value = formatHrv(snap.hrvMs),
                color = ColorProvider(tokens.metricCyan),
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
            VitalCell(
                label = "RHR",
                value = snap.restingHr?.let { "$it" } ?: "—",
                color = ColorProvider(tokens.metricRose),
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            VitalCell(
                label = "STEPS",
                value = snap.steps?.let { formatSteps(it) } ?: "—",
                color = ColorProvider(tokens.metricAmber),
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
            VitalCell(
                label = "CHARGE",
                value = snap.recoveryPct?.let { "$it%" } ?: "—",
                color = snap.recoveryPct?.let { ColorProvider(widgetBandColor(it, tokens)) }
                    ?: ColorProvider(tokens.chargeColor),
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
        }
    }
}

@Composable
private fun ScoreCell(
    label: String,
    pct: Int?,
    color: ColorProvider,
    valueSize: androidx.compose.ui.unit.TextUnit,
    textSecondary: ColorProvider,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = TextStyle(color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif),
        )
        Text(
            text = pct?.let { "$it%" } ?: "—",
            style = TextStyle(color = color, fontSize = valueSize, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif),
        )
    }
}

@Composable
private fun VitalCell(
    label: String,
    value: String,
    color: ColorProvider,
    textSecondary: ColorProvider,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WidgetLabel(label, textSecondary)
        WidgetValue(value, color, 20.sp)
    }
}

private fun statusLine(snap: WidgetSnapshot): String = when {
    snap.connected -> "Connected"
    snap.updatedAtMs > 0L ->
        DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(snap.updatedAtMs))
    else -> "Open NOOP to connect"
}

private fun formatSteps(steps: Int): String =
    if (steps >= 10_000) "${steps / 1000}k" else steps.toString()
