package com.base.androidstartertemplate.themes.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.base.androidstartertemplate.themes.AppDimens
import com.base.androidstartertemplate.themes.smallDimensions

private val DarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF263300),
    onPrimaryContainer = ElectricLime,

    secondary = NeonPurple,
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF3700B3),
    onSecondaryContainer = Color(0xFFE8DEF8),

    tertiary = NeonCyan,
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF004D40),
    onTertiaryContainer = NeonCyan,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = DarkBorder,
    error = CoralRed
)

class AppColors(
    val primary: Color = ElectricLime,
    val secondary: Color = NeonPurple,
    val transparent: Color = Color(0x00FFFFFF)
)

private val LocalAppDimens = staticCompositionLocalOf { smallDimensions }
private val LocalAppTypography = staticCompositionLocalOf { AppTypography() }
private val LocalAppColors = staticCompositionLocalOf { AppColors() }

@Composable
fun AppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val dimensions = smallDimensions
    val typography = remember { AppTypography() }
    val colors = remember { AppColors() }

    CompositionLocalProvider(
        LocalAppDimens provides dimensions,
        LocalAppTypography provides typography,
        LocalAppColors provides colors
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Material3Typography,
            content = content
        )
    }
}

@Composable
fun ProvideDimens(dimensions: AppDimens, content: @Composable () -> Unit) {
    val dimensionSet = remember { dimensions }
    CompositionLocalProvider(LocalAppDimens provides dimensionSet, content = content)
}

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    val dimens: AppDimens
        @Composable
        get() = LocalAppDimens.current
}