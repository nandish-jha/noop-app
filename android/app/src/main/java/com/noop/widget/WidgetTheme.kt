package com.noop.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.noop.analytics.RestScorer
import com.noop.ble.LiveState
import com.noop.data.DailyMetric
import com.noop.ui.MainActivity
import com.noop.ui.PaletteFamily
import com.noop.ui.PaletteTokens
import com.noop.ui.resolveBoopTokens
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Resolve the same Boop Appearance + palette family tokens the in-app theme uses. */
fun resolveWidgetTokens(context: Context): PaletteTokens {
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
    return resolveBoopTokens(family, dark)
}

/** Recovery-band colour using the active Boop scheme's recovery ramp. */
fun widgetBandColor(recovery: Int, tokens: PaletteTokens): Color = when {
    recovery >= 78 -> tokens.recovery100
    recovery >= 67 -> tokens.recovery078
    recovery >= 55 -> tokens.recovery055
    recovery >= 34 -> tokens.recovery030
    else -> tokens.recovery000
}

fun formatSleepDuration(totalMin: Int?): String {
    if (totalMin == null || totalMin <= 0) return "—"
    val h = totalMin / 60
    val m = totalMin % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

fun formatHrv(ms: Int?): String = ms?.let { "$it ms" } ?: "—"

/** Shared shell: Boop surface, corner radius, open MainActivity on tap. */
@Composable
fun WidgetShell(tokens: PaletteTokens, content: @Composable () -> Unit) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(tokens.surfaceBase))
            .cornerRadius(16.dp)
            .clickable(actionStartActivity<MainActivity>())
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
fun WidgetLabel(text: String, color: ColorProvider) {
    Text(
        text = text,
        style = TextStyle(
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
        ),
    )
}

@Composable
fun WidgetValue(
    text: String,
    color: ColorProvider,
    size: androidx.compose.ui.unit.TextUnit = 28.sp,
) {
    Text(
        text = text,
        style = TextStyle(
            color = color,
            fontSize = size,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
        ),
    )
}

/** Build one snapshot from the shared day anchor + live link (both widget producers use this). */
fun buildWidgetSnapshot(anchorRow: DailyMetric?, live: LiveState): WidgetSnapshot =
    WidgetSnapshot(
        recoveryPct = anchorRow?.recovery?.roundToInt(),
        // Rest = sleep_performance composite (honest-null until last night is scored); Effort = 0–100 strain.
        restPct = anchorRow?.let { RestScorer.restFromDaily(it)?.roundToInt() },
        effortPct = anchorRow?.strain?.roundToInt(),
        heartRate = live.heartRate,
        batteryPct = live.batteryPct?.roundToInt(),
        connected = live.connected,
        sleepMin = anchorRow?.totalSleepMin?.roundToInt(),
        hrvMs = anchorRow?.avgHrv?.roundToInt(),
        restingHr = anchorRow?.restingHr,
        steps = anchorRow?.steps,
        updatedAtMs = System.currentTimeMillis(),
    )

/** Refresh every registered NOOP Glance widget after Appearance / palette changes. */
object WidgetThemeRefresh {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val allWidgets: List<() -> GlanceAppWidget> = listOf(
        { NoopGlanceWidget() },
        { NoopChargeGlanceWidget() },
        { NoopLiveGlanceWidget() },
        { NoopNightGlanceWidget() },
        { NoopVitalsGlanceWidget() },
    )

    fun request(context: Context) {
        val app = context.applicationContext
        scope.launch { updateAllWidgets(app) }
    }

    suspend fun updateAllWidgets(app: Context) {
        allWidgets.forEach { factory ->
            runCatching { factory().updateAll(app) }
        }
    }
}
