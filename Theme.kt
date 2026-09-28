package com.lectorvoz.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CremaFondo = Color(0xFFF6F1E4)
private val VerdeMenta = Color(0xFF9CC5A1)
private val VerdeMentaOscuro = Color(0xFF6B8F71)
private val GrisSuave = Color(0xFF5B5A54)
private val PastelAcento = Color(0xFFE7C9A9)

private val LightColors = lightColorScheme(
    primary = VerdeMentaOscuro,
    onPrimary = Color.White,
    secondary = PastelAcento,
    background = CremaFondo,
    surface = Color(0xFFFFFDF7),
    onBackground = GrisSuave,
    onSurface = GrisSuave,
    tertiary = VerdeMenta
)

private val DarkColors = darkColorScheme(
    primary = VerdeMenta,
    onPrimary = Color(0xFF16241A),
    secondary = PastelAcento,
    background = Color(0xFF1E1D1A),
    surface = Color(0xFF262520),
    onBackground = Color(0xFFEDE8DA),
    onSurface = Color(0xFFEDE8DA)
)

@Composable
fun LectorVozTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
