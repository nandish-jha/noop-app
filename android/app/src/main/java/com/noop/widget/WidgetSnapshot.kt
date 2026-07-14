package com.noop.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll

/**
 * The handful of numbers the home-screen widgets show, persisted to SharedPreferences so Glance
 * can recompose after a process restart (from disk, not from app memory).
 */
data class WidgetSnapshot(
    /** Today's recovery / Charge 0–100, null until NOOP has scored enough nights (honest-blank). */
    val recoveryPct: Int? = null,
    /** Today's Rest 0–100 (the sleep_performance composite), null until last night is scored. */
    val restPct: Int? = null,
    /** Today's Effort 0–100 (the day's strain on the 0–100 scale). */
    val effortPct: Int? = null,
    /** Live heart rate, null when not streaming. */
    val heartRate: Int? = null,
    /** Strap battery 0–100, null until the strap reports it. */
    val batteryPct: Int? = null,
    val connected: Boolean = false,
    /** Asleep minutes from the anchored day row (totalSleepMin). */
    val sleepMin: Int? = null,
    /** Overnight average HRV (ms) from the anchored day row. */
    val hrvMs: Int? = null,
    /** Overnight resting HR from the anchored day row. */
    val restingHr: Int? = null,
    /** Steps for the anchored day, when available. */
    val steps: Int? = null,
    /** Wall-clock millis of the last push, so widgets can show honest staleness. */
    val updatedAtMs: Long = 0L,
)

/**
 * Persists snapshots and tells Glance to recompose. Both producers funnel through [push]:
 * [com.noop.ble.WhoopConnectionService] and [com.noop.ui.AppViewModel].
 *
 * Throttled by [PushGate]. CALLER CONTRACT (#82): collect with `conflate()` + `collect`, never
 * `collectLatest` — push suspends in Glance longer than the live-HR interval, so collectLatest
 * cancels every push mid-flight and widgets starve on stale prefs while the strap streams.
 */
object WidgetSnapshotStore {
    private const val FILE = "noop_widget"

    suspend fun push(context: Context, snap: WidgetSnapshot) {
        val app = context.applicationContext
        // Cheap, non-suspending gate FIRST — at live-HR cadence (~1/s) almost every call ends here.
        if (!PushGate.admit(snap)) return

        // Persist before anything suspending, and only THEN mark the gate (#82).
        // Saving even with no widget placed means a widget added later renders fresh data instantly.
        save(app, snap)
        PushGate.markPushed(snap)

        val manager = GlanceAppWidgetManager(app)
        val widgets = listOf(
            NoopGlanceWidget() to NoopGlanceWidget::class.java,
            NoopChargeGlanceWidget() to NoopChargeGlanceWidget::class.java,
            NoopLiveGlanceWidget() to NoopLiveGlanceWidget::class.java,
            NoopNightGlanceWidget() to NoopNightGlanceWidget::class.java,
            NoopVitalsGlanceWidget() to NoopVitalsGlanceWidget::class.java,
        )
        for ((widget, cls) in widgets) {
            val ids = runCatching { manager.getGlanceIds(cls) }.getOrDefault(emptyList())
            if (ids.isNotEmpty()) {
                runCatching { widget.updateAll(app) }
            }
        }
    }

    fun save(context: Context, snap: WidgetSnapshot) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putInt("recovery", snap.recoveryPct ?: -1)
            .putInt("rest", snap.restPct ?: -1)
            .putInt("effort", snap.effortPct ?: -1)
            .putInt("hr", snap.heartRate ?: -1)
            .putInt("battery", snap.batteryPct ?: -1)
            .putBoolean("connected", snap.connected)
            .putInt("sleepMin", snap.sleepMin ?: -1)
            .putInt("hrvMs", snap.hrvMs ?: -1)
            .putInt("restingHr", snap.restingHr ?: -1)
            .putInt("steps", snap.steps ?: -1)
            .putLong("updatedAt", snap.updatedAtMs)
            .apply()
    }

    fun load(context: Context): WidgetSnapshot {
        val p = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return WidgetSnapshot(
            recoveryPct = p.getInt("recovery", -1).takeIf { it >= 0 },
            restPct = p.getInt("rest", -1).takeIf { it >= 0 },
            effortPct = p.getInt("effort", -1).takeIf { it >= 0 },
            heartRate = p.getInt("hr", -1).takeIf { it > 0 },
            batteryPct = p.getInt("battery", -1).takeIf { it >= 0 },
            connected = p.getBoolean("connected", false),
            sleepMin = p.getInt("sleepMin", -1).takeIf { it >= 0 },
            hrvMs = p.getInt("hrvMs", -1).takeIf { it >= 0 },
            restingHr = p.getInt("restingHr", -1).takeIf { it > 0 },
            steps = p.getInt("steps", -1).takeIf { it >= 0 },
            updatedAtMs = p.getLong("updatedAt", 0L),
        )
    }
}

/**
 * Push-throttle: meaningful score/connection/HR-presence/battery-bucket/vitals changes admit
 * immediately; otherwise refresh at most once per [HR_REFRESH_MS].
 */
internal object PushGate {
    private const val HR_REFRESH_MS = 60_000L

    private var lastKey: String? = null
    private var lastPushAtMs = 0L

    private fun keyOf(snap: WidgetSnapshot): String =
        "${snap.recoveryPct}|${snap.restPct}|${snap.effortPct}|" +
            "${snap.sleepMin}|${snap.hrvMs}|${snap.restingHr}|${snap.steps}|" +
            "${snap.batteryPct?.div(5)}|${snap.connected}|${snap.heartRate != null}"

    fun admit(snap: WidgetSnapshot): Boolean =
        keyOf(snap) != lastKey || snap.updatedAtMs - lastPushAtMs >= HR_REFRESH_MS

    fun markPushed(snap: WidgetSnapshot) {
        lastKey = keyOf(snap)
        lastPushAtMs = snap.updatedAtMs
    }

    fun resetForTest() {
        lastKey = null
        lastPushAtMs = 0L
    }
}
