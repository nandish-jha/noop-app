package com.noop.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Force-stop / reopen helpers for the official WHOOP app (`com.whoop.android`).
 *
 * Strap bonding is exclusive: the official app must be Force-stopped briefly so NOOP can bond.
 * After NOOP connects, [launchIfInstalled] reopens WHOOP so it isn't left killed forever — the same
 * handoff the original companion flow used. Android cannot Force-stop another app programmatically;
 * [openAppInfo] deep-links to the system app-info screen where the user can.
 */
object WhoopAppHandoff {
    const val PACKAGE = "com.whoop.android"

    fun isInstalled(context: Context): Boolean =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    PACKAGE,
                    PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(PACKAGE, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }

    /** Open WHOOP's app-info screen so the user can Force stop it before pairing. */
    fun openAppInfo(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", PACKAGE, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    /**
     * Relaunch the official WHOOP app after NOOP has bonded. Returns false if WHOOP isn't installed
     * or the launcher intent isn't available.
     */
    fun launchIfInstalled(context: Context): Boolean {
        val launch = context.packageManager.getLaunchIntentForPackage(PACKAGE) ?: return false
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching {
            context.startActivity(launch)
            true
        }.getOrDefault(false)
    }
}
