package com.example.financeguide.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFC62828), // Pirate Crimson Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2C1012),
    onPrimaryContainer = Color(0xFFFFCDD2),
    secondary = Color(0xFFF6C445), // Golden Treasure Gold
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF2E240D),
    onSecondaryContainer = Color(0xFFFFF1C5),
    tertiary = Color(0xFF1E88E5), // Ocean Blue
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF0F2642),
    onTertiaryContainer = Color(0xFFBBDEFB),
    error = Color(0xFFC62828),
    onError = Color.White,
    errorContainer = Color(0xFF2C1012),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF0F172A), // Deep Navy
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F172A), // Deep Navy
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B), // Deep Slate Navy Card
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    surfaceContainerLowest = Color(0xFF0F172A),
    surfaceContainerLow = Color(0xFF1E293B),
    surfaceContainer = Color(0xFF1E293B),
    surfaceContainerHigh = Color(0xFF28364D),
    surfaceContainerHighest = Color(0xFF28364D)
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

private val OnePieceColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5), // Ocean Blue Primary Accent (#1E88E5)
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0F2642), // Deep Navy Blue Container
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFFF6C445), // Golden Treasure Secondary (#F6C445)
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF2E240D),
    onSecondaryContainer = Color(0xFFFFF1C5),
    tertiary = Color(0xFFC62828), // Pirate Crimson Red (#C62828)
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2C1012),
    onTertiaryContainer = Color(0xFFFFCDD2),
    error = Color(0xFFC62828),
    onError = Color.White,
    errorContainer = Color(0xFF2C1012),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF0F172A), // Deep Navy Background (#0F172A)
    onBackground = Color(0xFFF8FAFC), // Pearl White Text
    surface = Color(0xFF0F172A), // Deep Navy Surface
    onSurface = Color(0xFFF8FAFC), // Pearl White Text
    surfaceVariant = Color(0xFF1E293B), // Deep Slate Navy Card Surface (#1E293B)
    onSurfaceVariant = Color(0xFF94A3B8), // Soft Slate Text
    outline = Color(0xFFF6C445).copy(alpha = 0.4f), // Gold Highlight Border
    surfaceContainerLowest = Color(0xFF0F172A),
    surfaceContainerLow = Color(0xFF1E293B),
    surfaceContainer = Color(0xFF1E293B),
    surfaceContainerHigh = Color(0xFF28364D),
    surfaceContainerHighest = Color(0xFF28364D)
)

private val SpidermanColorScheme = darkColorScheme(
    primary = SpideyPrimary, // Deep Crimson Red (#C1121F)
    onPrimary = Color.White,
    primaryContainer = SpideyPrimaryContainer, // Matte Dark Red Container
    onPrimaryContainer = SpideyOnPrimaryContainer,
    secondary = SpideySecondary, // Royal Electric Blue (#1D4ED8)
    onSecondary = Color.White,
    secondaryContainer = SpideySecondaryContainer, // Matte Dark Blue Container
    onSecondaryContainer = SpideyOnSecondaryContainer,
    tertiary = Color(0xFF3B82F6), // Electric Blue Accent
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF0F1B3D),
    onTertiaryContainer = Color.White,
    error = Color(0xFFC1121F),
    onError = Color.White,
    errorContainer = Color(0xFF2C0A0D),
    onErrorContainer = Color(0xFFFFC5C8),
    background = SpideyBackground, // Matte Charcoal Black (#0A0A0A)
    onBackground = SpideyOnSurface, // Pure Crisp White Text
    surface = SpideySurface, // Matte Dark Charcoal Card Surface (#121212)
    onSurface = SpideyOnSurface, // Pure Crisp White Text
    surfaceVariant = SpideySurfaceVariant, // Sleek Glass Slate Card (#161B26)
    onSurfaceVariant = SpideyOnSurfaceVariant, // Soft Gray Subtitles (#CBD5E1)
    outline = SpideyOutline, // Ambient Web Outline Border (#2B3A5E)
    surfaceContainerLowest = SpideyBackground,
    surfaceContainerLow = SpideySurface,
    surfaceContainer = SpideySurfaceVariant,
    surfaceContainerHigh = SpideySurfaceElevated,
    surfaceContainerHighest = SpideySurfaceElevated
)

private val FinanceProColorScheme = darkColorScheme(
    primary = ProPrimary,               // Indigo-500
    onPrimary = ProOnPrimary,
    primaryContainer = ProPrimaryContainer,
    onPrimaryContainer = ProOnPrimaryContainer,
    secondary = ProSecondary,           // Emerald-500
    onSecondary = ProOnSecondary,
    secondaryContainer = ProSecondaryContainer,
    onSecondaryContainer = ProOnSecondaryContainer,
    tertiary = Color(0xFFF59E0B),       // Amber — expense / warning
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF3B2800),
    onTertiaryContainer = Color(0xFFFDE68A),
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFF3B0E0E),
    onErrorContainer = Color(0xFFFCA5A5),
    background = ProBackground,
    onBackground = ProOnSurface,
    surface = ProSurface,
    onSurface = ProOnSurface,
    surfaceVariant = ProSurfaceVariant,
    onSurfaceVariant = ProOnSurfaceVariant,
    outline = ProOutline,
    surfaceContainerLowest = ProBackground,
    surfaceContainerLow = ProSurface,
    surfaceContainer = ProSurfaceVariant,
    surfaceContainerHigh = ProSurfaceElevated,
    surfaceContainerHighest = ProSurfaceElevated
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
        "basic" -> FinanceProColorScheme
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
