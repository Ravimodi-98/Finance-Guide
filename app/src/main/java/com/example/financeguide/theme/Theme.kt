package com.example.financeguide.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryContainer,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryContainer,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryContainer,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = SecondaryContainer,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryContainer,
    tertiaryContainer = TertiaryDark,
    onTertiaryContainer = TertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = OnSurfaceDarkVariant,
    outline = OutlineDark,
    surfaceContainerLowest = SurfaceDark,
    surfaceContainerLow = SurfaceDarkVariant,
    surfaceContainer = SurfaceDarkVariant,
    surfaceContainerHigh = SurfaceDarkElevated,
    surfaceContainerHighest = SurfaceDarkElevated
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = OnSurfaceLightVariant,
    outline = OutlineLight
)

private val OnePieceColorScheme = DarkColorScheme

private val SpidermanColorScheme = darkColorScheme(
    primary = SpideyPrimary,
    onPrimary = SpideyOnPrimary,
    primaryContainer = SpideyPrimaryContainer,
    onPrimaryContainer = SpideyOnPrimaryContainer,
    secondary = SpideySecondary,
    onSecondary = SpideyOnSecondary,
    secondaryContainer = SpideySecondaryContainer,
    onSecondaryContainer = SpideyOnSecondaryContainer,
    tertiary = SpideySecondary,
    onTertiary = SpideyOnSecondary,
    tertiaryContainer = SpideySecondaryContainer,
    onTertiaryContainer = SpideyOnSecondaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = SpideySurface,
    onBackground = SpideyOnSurface,
    surface = SpideySurface,
    onSurface = SpideyOnSurface,
    surfaceVariant = SpideySurfaceVariant,
    onSurfaceVariant = SpideyOnSurfaceVariant,
    outline = SpideyOutline,
    surfaceContainerLowest = SpideySurface,
    surfaceContainerLow = SpideySurfaceVariant,
    surfaceContainer = SpideySurfaceVariant,
    surfaceContainerHigh = SpideySurfaceElevated,
    surfaceContainerHighest = SpideySurfaceElevated
)

@Composable
fun FinanceGuideTheme(
    themeMode: String = "system",
    dynamicColor: Boolean = false, // Use our premium palette by default
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        "onepiece" -> OnePieceColorScheme
        "spiderman" -> SpidermanColorScheme
        "dark" -> OnePieceColorScheme
        "light" -> LightColorScheme
        else -> {
            if (systemDark) OnePieceColorScheme else LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
