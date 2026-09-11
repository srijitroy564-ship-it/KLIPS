package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KlipzDarkColorScheme = darkColorScheme(
    primary = ElectricIndigo,
    onPrimary = Color.White,
    primaryContainer = ElectricIndigoDark,
    onPrimaryContainer = Color.White,
    secondary = BrandViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF332050),
    onSecondaryContainer = Color(0xFFE2D1FF),
    tertiary = BrandMagenta,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF38384A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Klipz is an immersive pro video editor, strictly themed with the signature dark canvas
    MaterialTheme(
        colorScheme = KlipzDarkColorScheme,
        typography = Typography,
        content = content
    )
}
