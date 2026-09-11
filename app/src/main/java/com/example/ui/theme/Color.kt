package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Klipz Brand Palette
val BrandDeepBlue = Color(0xFF2B3AD8)
val BrandViolet = Color(0xFF8B2FD8)
val BrandMagenta = Color(0xFFC82FA0)
val ElectricIndigo = Color(0xFF5B4CFF)
val ElectricIndigoLight = Color(0xFF7A6EFF)
val ElectricIndigoDark = Color(0xFF3A2FC4)

// Dark Studio Canvas Surfaces
val DarkBg = Color(0xFF0E0E12)
val DarkSurface = Color(0xFF17171D)
val DarkSurfaceElevated = Color(0xFF202028)
val DarkCard = Color(0xFF262633)
val DarkBorder = Color(0xFF2D2D3D)

// Text & Accents
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9A9AA5)
val TextMuted = Color(0xFF6C6C7A)

// Timeline Track Colors
val VideoTrackColor = Color(0xFF3843D0)
val OverlayTrackColor = Color(0xFF8A38D0)
val TextTrackColor = Color(0xFFC83890)
val AudioTrackColor = Color(0xFF1CB586)
val SfxTrackColor = Color(0xFFE58824)
val PlayheadColor = Color(0xFFFF3366)
val PlayheadGuide = Color(0x66FF3366)
val SelectionGold = Color(0xFFFFD15C)
val BeatMarkerColor = Color(0xFF4EE2EC)

// Gradients
val KlipzPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(BrandDeepBlue, BrandViolet, BrandMagenta)
)

val KlipzVerticalGradient = Brush.verticalGradient(
    colors = listOf(BrandDeepBlue, BrandViolet, BrandMagenta)
)

val KlipzAccentGradient = Brush.horizontalGradient(
    colors = listOf(ElectricIndigoDark, ElectricIndigo, ElectricIndigoLight)
)

val SurfaceGlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0x332B3AD8), Color(0x1117171D))
)
