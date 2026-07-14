package com.noop.ui

/**
 * Menu search corpus + matching for the More page.
 *
 * Matches against localized titles, route ids, enum names, synonyms, and Settings deep labels so
 * typing “alarm”, “theme”, “strain”, or “battery” finds the right row even when the on-screen title
 * is different.
 */
internal object MoreSearch {

    data class Hit(
        /** Nav route to open (destination route, or [Destination.Settings] for Settings rows). */
        val route: String,
        /** Display title (already localized / English for Settings deep links). */
        val title: String,
        /** True when this is a Settings section shortcut rather than a top-level destination. */
        val settingsDeepLink: Boolean = false,
    )

    /** Extra synonyms per destination route (english tokens; matching is case-insensitive). */
    private val destinationKeywords: Map<String, List<String>> = mapOf(
        "today" to listOf("home", "dashboard", "charge", "recovery", "effort", "rest", "morning"),
        "sleep" to listOf("night", "bed", "stages", "hypnogram", "asleep", "rem", "deep"),
        "trends" to listOf("strain", "effort", "charts", "graph", "history", "week"),
        "live" to listOf("heart", "hr", "bpm", "workout", "exercise", "zone", "realtime"),
        "workouts" to listOf("activity", "exercise", "training", "sessions", "gps", "run"),
        "intervals" to listOf("zones", "splits", "hr zones"),
        "stress" to listOf("calm", "tension", "daytime", "pressure"),
        "breathe" to listOf("breathing", "breath", "box", "relax", "meditation"),
        "smart_alarm" to listOf("alarm", "alarms", "wake", "window", "wind down", "wind-down", "morning"),
        "automations" to listOf("rules", "automation", "triggers"),
        "rhythm" to listOf("circadian", "schedule", "body clock"),
        "health" to listOf("body", "vitals", "wellness", "medical"),
        "hydration" to listOf("water", "drink", "fluid"),
        "vital_signs" to listOf("vitals", "rhr", "hrv", "spo2", "respiratory", "temperature"),
        "lab_book" to listOf("labs", "blood", "markers", "labbook"),
        "insights_hub" to listOf("moves", "what moves you", "journal hub", "correlations"),
        "intelligence" to listOf("overview", "scores explained", "how scores work"),
        "coach" to listOf("advice", "tips", "guidance"),
        "insights" to listOf("journal", "notes", "diary", "tags"),
        "explore" to listOf("browse", "discover", "metrics"),
        "compare" to listOf("side by side", "versus", "vs"),
        "devices" to listOf("strap", "whoop", "pair", "pairing", "bluetooth", "ble", "connect"),
        "data_sources" to listOf("import", "export", "csv", "apple health", "files"),
        "backup_sync" to listOf("backup", "restore", "sync", "cloud", "migrate"),
        "fused_record" to listOf("merge", "fused", "arbitration", "sources"),
        "notifications" to listOf("alerts", "push", "reminders", "quiet hours"),
        "settings" to listOf("prefs", "preferences", "options", "config", "configuration"),
        "test_centre" to listOf("debug", "diagnostics", "qa", "test", "testing"),
        "coupled_view" to listOf("coupled", "whoop view", "day read"),
    )

    /**
     * Settings section shortcuts shown in Menu search. Opening any of these navigates to Settings;
     * the label helps people find the right control by everyday words.
     */
    val settingsItems: List<Pair<String, List<String>>> = listOf(
        "Profile" to listOf("profile", "weight", "height", "age", "sex", "hr max", "body", "avatar", "photo"),
        "Appearance" to listOf("appearance", "theme", "dark", "light", "system", "palette", "color", "colour", "look"),
        "Units" to listOf("units", "metric", "imperial", "kg", "lbs", "celsius", "fahrenheit"),
        "Strap" to listOf(
            "strap", "battery", "battery saver", "connected", "background", "continuous hrv",
            "overnight", "debug logging", "rename", "share log",
        ),
        "Health & wellness" to listOf("health", "wellness", "illness", "sleep staging", "experimental"),
        "Backup & restore" to listOf("backup", "restore", "import backup", "export backup"),
        "About" to listOf("about", "version", "what's new", "whats new", "changelog", "terms", "support", "attribution"),
        "App icon" to listOf("icon", "app icon", "launcher"),
        "Notifications settings" to listOf("notification", "alert", "quiet"),
    )

    /** Bottom-bar destinations that are not listed in more drawer groups but should still be findable. */
    val tabExtras: List<String> = listOf("today", "sleep", "trends")

    fun matches(query: String, haystacks: List<String>): Boolean {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return true
        val tokens = q.split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return true
        val corpus = haystacks.joinToString("\u0000") { it.lowercase() }
        // Whole-query substring first (handles “smart alarm”, “what moves”).
        if (corpus.contains(q.replace(' ', ' ')) || haystacks.any { it.lowercase().contains(q) }) {
            // Prefer token AND if multi-word so "sleep stage" doesn't mass-match on "sleep" alone —
            // still allow full phrase hit.
            if (tokens.size == 1) return true
            if (haystacks.any { it.lowercase().contains(q) }) return true
        }
        return tokens.all { token ->
            haystacks.any { field ->
                val f = field.lowercase()
                f.contains(token) || fuzzyToken(f, token)
            }
        }
    }

    /** Tiny fuzzy: one-char typo / singular-plural on short tokens. */
    private fun fuzzyToken(field: String, token: String): Boolean {
        if (token.length < 4) return false
        if (field.contains(token.removeSuffix("s")) || field.contains(token + "s")) return true
        // Prefix of a word in the field
        return field.split(Regex("[^a-z0-9]+")).any { word ->
            word.startsWith(token) || token.startsWith(word) && word.length >= 4 ||
                editDistanceOne(word, token)
        }
    }

    private fun editDistanceOne(a: String, b: String): Boolean {
        if (kotlin.math.abs(a.length - b.length) > 1) return false
        if (a == b) return true
        val (shorter, longer) = if (a.length <= b.length) a to b else b to a
        if (longer.length - shorter.length == 1) {
            var i = 0
            var j = 0
            var skipped = false
            while (i < shorter.length && j < longer.length) {
                if (shorter[i] == longer[j]) {
                    i++; j++
                } else if (!skipped) {
                    skipped = true; j++
                } else return false
            }
            return true
        }
        var diffs = 0
        for (i in a.indices) {
            if (a[i] != b[i]) {
                diffs++
                if (diffs > 1) return false
            }
        }
        return diffs == 1
    }

    fun destinationHaystack(
        title: String,
        route: String,
        enumName: String,
    ): List<String> = buildList {
        add(title)
        add(route)
        add(enumName)
        add(route.replace('_', ' '))
        destinationKeywords[route]?.let { addAll(it) }
    }

    fun settingsHaystack(label: String, keywords: List<String>): List<String> =
        listOf(label) + keywords
}
