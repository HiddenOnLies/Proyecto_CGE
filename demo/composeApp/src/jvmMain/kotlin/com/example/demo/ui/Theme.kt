package com.example.demo.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de colores personalizada para el Tema Oscuro.
private val DarkColorPalette = darkColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212), // Fondo casi negro.
    surface = Color(0xFF1E1E1E), // Color de las tarjetas (un poco más claro).
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

// Paleta de colores personalizada para el Tema Claro.
private val LightColorPalette = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color.White, // Fondo blanco.
    surface = Color(0xFFF2F2F2), // Color de las tarjetas (gris claro).
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

/**
 * Composable del tema principal de la aplicación.
 * Envuelve el contenido y aplica la paleta de colores correcta.
 *
 * @param darkTheme Indica si se debe usar el tema oscuro (true) o claro (false).
 * @param content El contenido de la UI (la App) al que se aplicará el tema.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    // Selecciona la paleta de colores (Oscura o Clara)
    // basándose en el parámetro 'darkTheme'.
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    // Aplica el tema de Material Design (colores, tipografía, formas)
    // al 'content' (contenido) de la aplicación.
    MaterialTheme(
        colors = colors,
        content = content
    )
}