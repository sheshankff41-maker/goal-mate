package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Purple & Electric Blue gradient palette with deep obsidian black
val DeepBlack = Color(0xFF090611)
val ObsidianDark = Color(0xFF100A20)
val DarkSurface = Color(0xFF18102E)
val DarkSurfaceElevated = Color(0xFF22173E)
val DarkSurfaceBorder = Color(0xFF33245C)

// Electric violet and cyan/blue accents
val PurplePrimary = Color(0xFF9061F9)
val PurpleAccent = Color(0xFFA855F7)
val PurpleLight = Color(0xFFC084FC)
val PurpleDark = Color(0xFF6B21A8)

val BluePrimary = Color(0xFF38BDF8)
val BlueAccent = Color(0xFF0EA5E9)
val BlueDark = Color(0xFF0284C7)

val NeonPink = Color(0xFFF43F5E)
val NeonGreen = Color(0xFF10B981)
val NeonAmber = Color(0xFFF59E0B)

// Light theme colors
val LightBackground = Color(0xFFF8F9FE)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFF1F3FB)
val LightSurfaceBorder = Color(0xFFE2E6F2)
val TextPrimaryLight = Color(0xFF1E1B2E)
val TextSecondaryLight = Color(0xFF64748B)

// Dark theme texts
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextMutedDark = Color(0xFF64748B)

// Gradients
val PrimaryGradient = Brush.linearGradient(
    colors = listOf(PurplePrimary, BluePrimary)
)

val AccentGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFEC4899), PurplePrimary, BluePrimary)
)

val CardDarkGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1E1438), Color(0xFF130D26))
)

val CardLightGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF5F7FF))
)

val GlowPurpleGradient = Brush.radialGradient(
    colors = listOf(PurplePrimary.copy(alpha = 0.25f), Color.Transparent)
)

val GoldenBadgeGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
)
