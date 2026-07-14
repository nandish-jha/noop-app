package com.noop.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/** Manifest entry for the Rest · Charge · Effort scores widget. */
class NoopWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NoopGlanceWidget()
}

/** Manifest entry for the Charge-only compact widget. */
class NoopChargeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NoopChargeGlanceWidget()
}

/** Manifest entry for the live HR + battery widget. */
class NoopLiveWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NoopLiveGlanceWidget()
}

/** Manifest entry for last-night Rest + sleep duration. */
class NoopNightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NoopNightGlanceWidget()
}

/** Manifest entry for overnight vitals (HRV / RHR / steps / Charge). */
class NoopVitalsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NoopVitalsGlanceWidget()
}
