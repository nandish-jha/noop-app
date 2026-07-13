package com.noop.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Boop color-palette families — mirrors nandish-jha/boop-app `PaletteFamily`.
 * Chrome (canvas / cards / accent / nav) follows the selected scheme; classic chart
 * ramps stay available via ChartStylePrefs when encoding data.
 */
enum class PaletteFamily(val storageKey: String, val label: String) {
    AMOLED("amoled", "AMOLED"),
    TERRACOTTA("terracotta", "Terracotta"),
    ROSE("rose", "Rose"),
    SLATE("slate", "Slate"),
    FOREST("forest", "Forest"),
    OCEAN("ocean", "Ocean"),
    ;

    companion object {
        fun fromStorage(value: String?) = entries.find { it.storageKey == value } ?: AMOLED
    }
}

object PaletteFamilyPrefs {
    private const val FILE = "noop_prefs"
    private const val KEY = "theme.palette_family"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    var family by mutableStateOf(PaletteFamily.AMOLED)
        private set

    fun load(ctx: Context) {
        family = PaletteFamily.fromStorage(prefs(ctx).getString(KEY, PaletteFamily.AMOLED.storageKey))
    }

    fun set(ctx: Context, value: PaletteFamily) {
        family = value
        prefs(ctx).edit().putString(KEY, value.storageKey).apply()
    }
}

/** Swatch preview colors (bg / surface / accent / muted) for the Settings picker. */
fun paletteFamilyPreview(family: PaletteFamily, dark: Boolean): List<Color> {
    val t = resolveBoopTokens(family, dark)
    return listOf(t.surfaceBase, t.surfaceRaised, t.accent, t.textSecondary)
}

fun resolveBoopTokens(family: PaletteFamily, dark: Boolean): PaletteTokens {
    val chrome = boopChrome(family, dark)
    return tokensFromChrome(chrome, dark)
}

private data class BoopChrome(
    val background: Color,
    val phoneBg: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val onBackground: Color,
    val muted: Color,
    val accent: Color,
    val accentGlow: Color,
    val accentOn: Color,
    val navUnselected: Color,
    val topbarBg: Color,
    val chipBg: Color,
    val surfaceBorder: Color,
    val danger: Color,
    val monochrome: Boolean,
)

private fun boopChrome(family: PaletteFamily, dark: Boolean): BoopChrome = when (family) {
    PaletteFamily.AMOLED -> if (dark) BoopChrome(
        background = Color(0xFF000000), phoneBg = Color(0xFF000000),
        surface = Color(0xFF1A1A1A), surfaceVariant = Color(0xFF141414), surfaceElevated = Color(0xFF222222),
        onBackground = Color(0xFFFFFFFF), muted = Color(0xFF9E9E9E),
        accent = Color(0xFFE0E0E0), accentGlow = Color(0xFFBDBDBD), accentOn = Color(0xFF000000),
        navUnselected = Color(0xFF8A8A8A), topbarBg = Color(0xD9000000), chipBg = Color(0xFF1F1F1F),
        surfaceBorder = Color(0x1AFFFFFF), danger = Color(0xFFE57373), monochrome = true,
    ) else BoopChrome(
        background = Color(0xFFF2F2F2), phoneBg = Color(0xFFFFFFFF),
        surface = Color(0xFFFFFFFF), surfaceVariant = Color(0xFFEDEDED), surfaceElevated = Color(0xFFFFFFFF),
        onBackground = Color(0xFF121212), muted = Color(0xFF757575),
        accent = Color(0xFF3A3A3A), accentGlow = Color(0xFF9E9E9E), accentOn = Color(0xFFFFFFFF),
        navUnselected = Color(0xFF9E9E9E), topbarBg = Color(0xEBFFFFFF), chipBg = Color(0xFFEDEDED),
        surfaceBorder = Color(0x14121212), danger = Color(0xFFC45850), monochrome = true,
    )
    PaletteFamily.TERRACOTTA -> if (dark) BoopChrome(
        background = Color(0xFF141413), phoneBg = Color(0xFF1A1918),
        surface = Color(0xFF30302E), surfaceVariant = Color(0xFF252320), surfaceElevated = Color(0xFF3D3D3A),
        onBackground = Color(0xFFFAF9F5), muted = Color(0xFFB0AEA5),
        accent = Color(0xFFE88868), accentGlow = Color(0xFFE8A898), accentOn = Color(0xFFFFFFFF),
        navUnselected = Color(0xFF8A8480), topbarBg = Color(0xD91A1918), chipBg = Color(0xFF252320),
        surfaceBorder = Color(0x1AFAF9F5), danger = Color(0xFFE07A6A), monochrome = false,
    ) else BoopChrome(
        background = Color(0xFFFAF9F5), phoneBg = Color(0xFFFFFDF8),
        surface = Color(0xFFF5F4ED), surfaceVariant = Color(0xFFE8E6DC), surfaceElevated = Color(0xFFEFE9DE),
        onBackground = Color(0xFF141413), muted = Color(0xFF87867F),
        accent = Color(0xFFD46E48), accentGlow = Color(0xFFE8A898), accentOn = Color(0xFFFFFFFF),
        navUnselected = Color(0xFF9A9288), topbarBg = Color(0xEBFFFDF8), chipBg = Color(0xFFE8E6DC),
        surfaceBorder = Color(0x14141313), danger = Color(0xFFC45850), monochrome = false,
    )
    PaletteFamily.ROSE -> if (dark) BoopChrome(
        background = Color(0xFF141012), phoneBg = Color(0xFF1A1518),
        surface = Color(0xFF2A2226), surfaceVariant = Color(0xFF231C20), surfaceElevated = Color(0xFF352C31),
        onBackground = Color(0xFFFFF7F8), muted = Color(0xFFB5A4A8),
        accent = Color(0xFFE08A9A), accentGlow = Color(0xFFF0B0BA), accentOn = Color(0xFF1A1014),
        navUnselected = Color(0xFF8A7A80), topbarBg = Color(0xD91A1518), chipBg = Color(0xFF231C20),
        surfaceBorder = Color(0x1AFFF7F8), danger = Color(0xFFE07A6A), monochrome = false,
    ) else BoopChrome(
        background = Color(0xFFFFF7F8), phoneBg = Color(0xFFFFFBFC),
        surface = Color(0xFFF8EEEF), surfaceVariant = Color(0xFFF0E2E5), surfaceElevated = Color(0xFFF5E8EA),
        onBackground = Color(0xFF1A1214), muted = Color(0xFF8A757A),
        accent = Color(0xFFC45A6E), accentGlow = Color(0xFFE08A9A), accentOn = Color(0xFFFFFFFF),
        navUnselected = Color(0xFF9A888C), topbarBg = Color(0xEBFFFBFC), chipBg = Color(0xFFF0E2E5),
        surfaceBorder = Color(0x141A1214), danger = Color(0xFFC45850), monochrome = false,
    )
    PaletteFamily.SLATE -> if (dark) BoopChrome(
        background = Color(0xFF101216), phoneBg = Color(0xFF14171C),
        surface = Color(0xFF22262C), surfaceVariant = Color(0xFF1B1F24), surfaceElevated = Color(0xFF2C3138),
        onBackground = Color(0xFFE8ECF0), muted = Color(0xFF9AA3AD),
        accent = Color(0xFF8FA8C4), accentGlow = Color(0xFFA8BDD4), accentOn = Color(0xFF101216),
        navUnselected = Color(0xFF7A838C), topbarBg = Color(0xD914171C), chipBg = Color(0xFF1B1F24),
        surfaceBorder = Color(0x1AE8ECF0), danger = Color(0xFFE07A6A), monochrome = false,
    ) else BoopChrome(
        background = Color(0xFFF2F4F7), phoneBg = Color(0xFFF8FAFC),
        surface = Color(0xFFECEFF3), surfaceVariant = Color(0xFFE2E6EC), surfaceElevated = Color(0xFFE8ECF1),
        onBackground = Color(0xFF14181E), muted = Color(0xFF6F7884),
        accent = Color(0xFF4F6F8F), accentGlow = Color(0xFF8FA8C4), accentOn = Color(0xFFFFFFFF),
        navUnselected = Color(0xFF8A929C), topbarBg = Color(0xEBF8FAFC), chipBg = Color(0xFFE2E6EC),
        surfaceBorder = Color(0x1414181E), danger = Color(0xFFC45850), monochrome = false,
    )
    PaletteFamily.FOREST -> if (dark) BoopChrome(
        background = Color(0xFF0F1410), phoneBg = Color(0xFF141A15),
        surface = Color(0xFF222A23), surfaceVariant = Color(0xFF1A221C), surfaceElevated = Color(0xFF2C352E),
        onBackground = Color(0xFFEFF5EF), muted = Color(0xFF9AAB9C),
        accent = Color(0xFF7DB88A), accentGlow = Color(0xFFA0CFA8), accentOn = Color(0xFF0F1410),
        navUnselected = Color(0xFF7A8A7C), topbarBg = Color(0xD9141A15), chipBg = Color(0xFF1A221C),
        surfaceBorder = Color(0x1AEFF5EF), danger = Color(0xFFE07A6A), monochrome = false,
    ) else BoopChrome(
        background = Color(0xFFF3F7F3), phoneBg = Color(0xFFF8FBF8),
        surface = Color(0xFFE8EFE8), surfaceVariant = Color(0xFFDCE6DC), surfaceElevated = Color(0xFFE4ECE4),
        onBackground = Color(0xFF121814), muted = Color(0xFF6F7E70),
        accent = Color(0xFF4F8A5C), accentGlow = Color(0xFF7DB88A), accentOn = Color(0xFFFFFFFF),
        navUnselected = Color(0xFF889488), topbarBg = Color(0xEBF8FBF8), chipBg = Color(0xFFDCE6DC),
        surfaceBorder = Color(0x14121814), danger = Color(0xFFC45850), monochrome = false,
    )
    PaletteFamily.OCEAN -> if (dark) BoopChrome(
        background = Color(0xFF0E1418), phoneBg = Color(0xFF121A20),
        surface = Color(0xFF1E2830), surfaceVariant = Color(0xFF182028), surfaceElevated = Color(0xFF25323C),
        onBackground = Color(0xFFE8F2F8), muted = Color(0xFF9AADB8),
        accent = Color(0xFF6FA8C8), accentGlow = Color(0xFF92C0D8), accentOn = Color(0xFF0E1418),
        navUnselected = Color(0xFF7A8A94), topbarBg = Color(0xD9121A20), chipBg = Color(0xFF182028),
        surfaceBorder = Color(0x1AE8F2F8), danger = Color(0xFFE07A6A), monochrome = false,
    ) else BoopChrome(
        background = Color(0xFFF0F5F8), phoneBg = Color(0xFFF6FAFC),
        surface = Color(0xFFE4ECF2), surfaceVariant = Color(0xFFD6E2EA), surfaceElevated = Color(0xFFDEEAF2),
        onBackground = Color(0xFF101820), muted = Color(0xFF6A7C88),
        accent = Color(0xFF3F7A9A), accentGlow = Color(0xFF6FA8C8), accentOn = Color(0xFFFFFFFF),
        navUnselected = Color(0xFF8896A0), topbarBg = Color(0xEBF6FAFC), chipBg = Color(0xFFD6E2EA),
        surfaceBorder = Color(0x14101820), danger = Color(0xFFC45850), monochrome = false,
    )
}

private fun tokensFromChrome(c: BoopChrome, dark: Boolean): PaletteTokens {
    val domainAccent = c.accent
    val domainGlow = c.accentGlow
    val domainDeep = if (c.monochrome) c.muted else c.accent.copy(alpha = 1f).let {
        // Darken-ish stand-in: blend toward background for "deep".
        Color(
            red = (it.red * 0.55f + c.background.red * 0.45f).coerceIn(0f, 1f),
            green = (it.green * 0.55f + c.background.green * 0.45f).coerceIn(0f, 1f),
            blue = (it.blue * 0.55f + c.background.blue * 0.45f).coerceIn(0f, 1f),
            alpha = 1f,
        )
    }
    return PaletteTokens(
        surfaceBase = c.background,
        surfaceRaised = c.surface,
        surfaceOverlay = c.surfaceElevated,
        surfaceInset = c.surfaceVariant,
        hairline = c.surfaceBorder,
        hairlineStrong = c.surfaceBorder.copy(alpha = (c.surfaceBorder.alpha * 2f).coerceAtMost(1f)),
        textPrimary = c.onBackground,
        textSecondary = c.muted,
        textTertiary = c.navUnselected,
        glowAmbient = c.chipBg,
        accent = c.accent,
        accentHover = c.accentGlow,
        accentMuted = c.chipBg,
        focusRing = c.accent,
        // Chart / status: keep classic-readable ramps; monochrome softens stress/strain.
        recovery000 = if (c.monochrome) Color(0xFFE57373) else c.danger,
        recovery030 = if (c.monochrome) Color(0xFFFFB74D) else c.accentGlow,
        recovery055 = if (c.monochrome) Color(0xFFFFF176) else c.accent,
        recovery078 = Color(0xFFAED581),
        recovery100 = Color(0xFF81C784),
        strain000 = if (c.monochrome) Color(0xFF757575) else domainDeep,
        strain033 = if (c.monochrome) Color(0xFF9E9E9E) else c.muted,
        strain066 = if (c.monochrome) Color(0xFFBDBDBD) else c.accentGlow,
        strain100 = if (c.monochrome) Color(0xFFE0E0E0) else c.accent,
        sleepAwake = c.accentGlow,
        sleepLight = c.muted,
        sleepDeep = c.navUnselected,
        sleepREM = c.accent,
        zone1 = c.muted,
        zone2 = c.accentGlow,
        zone3 = c.accent,
        zone4 = c.accentGlow,
        zone5 = c.danger,
        statusPositive = Color(0xFF81C784),
        statusWarning = Color(0xFFFFB74D),
        statusCritical = c.danger,
        metricCyan = c.muted,
        metricPurple = c.navUnselected,
        metricAmber = c.accentGlow,
        metricRose = c.danger,
        chargeColor = domainAccent,
        chargeDeep = domainDeep,
        chargeBright = domainGlow,
        chargeGlow = domainAccent,
        effortColor = if (c.monochrome) c.muted else c.accentGlow,
        effortDeep = domainDeep,
        effortBright = domainGlow,
        effortGlow = c.accentGlow,
        restColor = c.muted,
        restDeep = domainDeep,
        restBright = c.accentGlow,
        restGlow = c.muted,
        stressColor = c.accentGlow,
        stressDeep = domainDeep,
        stressBright = c.danger,
        stressGlow = c.accentGlow,
        scenicCenter = c.phoneBg,
        scenicEdge = c.background,
        scenicStar = c.accentGlow,
        cardFillTop = c.surface,
        cardFillBottom = c.background,
        gold = c.accent,
        goldLight = c.accentGlow,
        goldDeep = domainDeep,
        goldDeepText = c.accentOn,
        signalYellow = c.accentGlow,
        titaniumTop = c.onBackground,
        titaniumMid = c.accentGlow,
        titaniumLow = c.muted,
        titaniumDeep = c.navUnselected,
        tipCore = if (dark) Color(0xFFFFFFFF) else c.onBackground,
    )
}
