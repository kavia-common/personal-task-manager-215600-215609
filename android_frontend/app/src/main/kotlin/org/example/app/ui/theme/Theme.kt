package org.example.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ocean Professional Colors
val BluePrimary = Color(0xFF2563EB)
val AmberSecondary = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFEF4444)
val Background = Color(0xFFF9FAFB)
val Surface = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF111827)

private val LightColors: ColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = AmberSecondary,
    background = Background,
    surface = Surface,
    error = ErrorRed,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = Color.White
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = AmberSecondary,
    background = Color(0xFF0B1220),
    surface = Color(0xFF0F172A),
    error = ErrorRed,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB),
    onError = Color.White
)

// PUBLIC_INTERFACE
@Composable
fun OceanProfessionalTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    /** Material3 theme wrapper for the Ocean Professional palette. */
    val colors = if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
