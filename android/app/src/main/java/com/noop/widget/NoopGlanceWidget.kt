package com.noop.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.noop.R
import com.noop.ui.MainActivity
import com.noop.ui.PaletteFamily
import com.noop.ui.PaletteTokens
import com.noop.ui.resolveBoopTokens
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Home-screen widget: today's three top scores (Rest · Charge · Effort, Charge centred), with live HR
 * and strap battery at a glance (#516). Colours follow the app's Appearance + Boop palette family
 * via [resolveBoopTokens] (same path as [com.noop.ui.NoopTheme]).
 */
class NoopGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snap = runCatching { WidgetSnapshotStore.load(context) }.getOrDefault(WidgetSnapshot())
        val prefs = context.getSharedPreferences("noop_prefs", Context.MODE_PRIVATE)
        val dark = runCatching {
            when (prefs.getString("theme.appearance", "system")) {
                "light" -> false
                "dark" -> true
                else -> (context.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }.getOrDefault(true)
        val family = PaletteFamily.fromStorage(prefs.getString("theme.palette_family", null))
        val tokens = resolveBoopTokens(family, dark)
        provideContent { WidgetContent(snap, tokens) }
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

/** Push a theme refresh to every NOOP widget (Appearance / palette changes in Settings). */
object WidgetThemeRefresh {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun request(context: Context) {
        val app = context.applicationContext
        scope.launch {
            runCatching { NoopGlanceWidget().updateAll(app) }
        }
    }
}

@Composable
private fun WidgetContent(snap: WidgetSnapshot, tokens: PaletteTokens) {
    val surface = ColorProvider(tokens.surfaceBase)
    val textPrimary = ColorProvider(tokens.textPrimary)
    val textSecondary = ColorProvider(tokens.textSecondary)
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(surface)
            .cornerRadius(16.dp)
            .clickable(actionStartActivity<MainActivity>())
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.Bottom,
        ) {
            ScoreCell(
                label = "REST",
                pct = snap.restPct,
                color = snap.restPct?.let { ColorProvider(bandColor(it, tokens)) } ?: textSecondary,
                valueSize = 22.sp,
                textSecondary = textSecondary,
                modifier = GlanceModifier.defaultWeight(),
            )
            ScoreCell(
                label = "CHARGE",
                pct = snap.recoveryPct,
                color = snap.recoveryPct?.let { ColorProvider(bandColor(it, tokens)) } ?: textSecondary,
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
                style = TextStyle(color = textPrimary, fontSize = 13.sp),
            )
            Spacer(modifier = GlanceModifier.width(10.dp))
            Text(
                text = snap.batteryPct?.let { "⚡ $it%" } ?: "⚡ - ",
                style = TextStyle(color = textPrimary, fontSize = 13.sp),
            )
        }
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = when {
                snap.connected -> "Connected"
                snap.updatedAtMs > 0L ->
                    DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(snap.updatedAtMs))
                else -> "Open NOOP to connect"
            },
            style = TextStyle(color = textSecondary, fontSize = 11.sp),
        )
    }
}

/** Recovery-band colour using the active Boop scheme's recovery ramp (67 / 34 cuts). */
private fun bandColor(recovery: Int, tokens: PaletteTokens): Color = when {
    recovery >= 78 -> tokens.recovery100
    recovery >= 67 -> tokens.recovery078
    recovery >= 55 -> tokens.recovery055
    recovery >= 34 -> tokens.recovery030
    else -> tokens.recovery000
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
            style = TextStyle(color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium),
        )
        Text(
            text = pct?.let { "$it%" } ?: "—",
            style = TextStyle(color = color, fontSize = valueSize, fontWeight = FontWeight.Bold),
        )
    }
}
