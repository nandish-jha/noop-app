package com.noop.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MoreSearchTest {

    @Test
    fun findsAlarmSynonym() {
        val hay = MoreSearch.destinationHaystack("Alarms", "smart_alarm", "SmartAlarm")
        assertTrue(MoreSearch.matches("alarm", hay))
        assertTrue(MoreSearch.matches("wake window", hay))
    }

    @Test
    fun findsTrendsAsStrain() {
        val hay = MoreSearch.destinationHaystack("Strain", "trends", "Trends")
        assertTrue(MoreSearch.matches("strain", hay))
        assertTrue(MoreSearch.matches("trends", hay))
    }

    @Test
    fun findsSettingsAppearance() {
        val (label, keys) = MoreSearch.settingsItems.first { it.first == "Appearance" }
        assertTrue(MoreSearch.matches("theme", MoreSearch.settingsHaystack(label, keys)))
        assertTrue(MoreSearch.matches("palette", MoreSearch.settingsHaystack(label, keys)))
    }

    @Test
    fun findsBatterySaverInStrap() {
        val (label, keys) = MoreSearch.settingsItems.first { it.first == "Strap" }
        assertTrue(MoreSearch.matches("battery saver", MoreSearch.settingsHaystack(label, keys)))
    }

    @Test
    fun rejectsUnrelated() {
        val hay = MoreSearch.destinationHaystack("Live", "live", "Live")
        assertFalse(MoreSearch.matches("backup restore xyzzy", hay))
    }

    @Test
    fun toleratesSmallTypo() {
        val hay = MoreSearch.destinationHaystack("Devices", "devices", "Devices")
        assertTrue(MoreSearch.matches("devces", hay)) // one deletion from devices
    }
}
