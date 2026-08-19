package com.goldmine.uncc.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = GoldMineColors.CharlotteGreenLight,
    onPrimary = Color(0xFF00281B),
    primaryContainer = GoldMineColors.CharlotteGreen,
    onPrimaryContainer = Color.White,
    secondary = GoldMineColors.NinerGold,
    onSecondary = Color.Black,
    secondaryContainer = GoldMineColors.CharlotteGreen,
    onSecondaryContainer = Color.White,
    tertiary = GoldMineColors.NinerGold,
    onTertiary = Color.Black,
    tertiaryContainer = GoldMineColors.CharlotteGreen,
    onTertiaryContainer = Color.White,
    background = GoldMineColors.AppDarkBackground,
    onBackground = Color.White,
    surface = GoldMineColors.DarkCard,
    onSurface = Color.White,
    surfaceVariant = GoldMineColors.DarkSurfaceAlt,
    onSurfaceVariant = Color(0xFFBDBDBD),
    surfaceContainer = GoldMineColors.DarkCard,
    surfaceContainerHigh = GoldMineColors.DarkCardElevated,
    surfaceContainerHighest = GoldMineColors.DarkSurfaceAlt,
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF3A3A3C),
    error = GoldMineColors.AccentRed,
    onError = Color.White,
)

private val LightColors = lightColorScheme(
    primary = GoldMineColors.CharlotteGreen,
    onPrimary = Color.White,
    primaryContainer = GoldMineColors.CharlotteGreen,
    onPrimaryContainer = Color.White,
    secondary = GoldMineColors.NinerGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFE0EFE8),
    onSecondaryContainer = GoldMineColors.CharlotteGreen,
    tertiary = GoldMineColors.CharlotteGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCDE9DD),
    onTertiaryContainer = GoldMineColors.CharlotteGreen,
    background = GoldMineColors.LightBackground,
    onBackground = Color.Black,
    surface = GoldMineColors.LightCard,
    onSurface = Color.Black,
    surfaceVariant = GoldMineColors.SystemGray6Light,
    onSurfaceVariant = Color(0xFF4A4A4A),
    surfaceContainer = GoldMineColors.LightCard,
    surfaceContainerHigh = GoldMineColors.LightCard,
    surfaceContainerHighest = GoldMineColors.SystemGray6Light,
    outline = Color(0xFFD1D1D6),
    outlineVariant = Color(0xFFE5E5EA),
    error = GoldMineColors.AccentRed,
    onError = Color.White,
)

/**
 * Semantic colours that do not map cleanly onto the Material 3 scheme but are needed
 * to reproduce the iOS design exactly (glow colours, secondary text, card fills…).
 */
data class GoldMineExtraColors(
    val isDark: Boolean,
    val cardBackground: Color,
    val screenBackground: Color,
    val secondaryText: Color,
    val goldGlow: Color,
    val iconCircleFill: Color,
    val accent: Color,
)

val LocalGoldMineColors = staticCompositionLocalOf {
    GoldMineExtraColors(
        isDark = true,
        cardBackground = GoldMineColors.DarkCard,
        screenBackground = GoldMineColors.AppDarkBackground,
        secondaryText = GoldMineColors.AccentGray,
        goldGlow = GoldMineColors.NinerGold,
        iconCircleFill = Color.White,
        accent = GoldMineColors.CharlotteGreenLight,
    )
}

@Composable
fun GoldMineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val extras = GoldMineExtraColors(
        isDark = darkTheme,
        cardBackground = if (darkTheme) GoldMineColors.DarkCard else Color.White,
        screenBackground = if (darkTheme) GoldMineColors.AppDarkBackground else GoldMineColors.LightBackground,
        secondaryText = GoldMineColors.AccentGray,
        goldGlow = if (darkTheme) GoldMineColors.NinerGold.copy(alpha = 0.6f) else Color.Transparent,
        iconCircleFill = if (darkTheme) Color.White else GoldMineColors.SystemGray6Light,
        // iOS uses flat #005035 in both appearances; on a near-black background that fails
        // WCAG contrast, so dark mode uses the lightened brand green for interactive text.
        accent = if (darkTheme) GoldMineColors.CharlotteGreenLight else GoldMineColors.CharlotteGreen,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalGoldMineColors provides extras) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GoldMineTypography,
            content = content,
        )
    }
}
