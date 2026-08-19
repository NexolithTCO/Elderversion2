package com.example.elderhelpprototypev01.ui.theme

import androidx.compose.ui.graphics.Color

// Global Design System Tokens
// 1. Colors
val AppBackground = Color(0xFFF5F7FA)
val AppSurface = Color(0xFFFFFFFF)
val AppPrimary = Color(0xFF0875E1)
val AppPrimaryDark = Color(0xFF075BB5)
val AppTextPrimary = Color(0xFF101828)
val AppTextSecondary = Color(0xFF667085)
val AppTextMuted = Color(0xFF98A2B3)
val AppBorder = Color(0xFFE4E7EC)
val AppSuccess = Color(0xFF16A34A)
val AppWarning = Color(0xFFF59E0B)
val AppEmergency = Color(0xFFE53935)

// Backward-compatible color aliases
val AppleBlue = AppPrimary
val AppleBlueDark = AppPrimaryDark
val AppleBlueLight = Color(0xFFEBF3FC)
val AppleBlueGlow = Color(0x250875E1)
val AppleBlueSubtle = Color(0xFFD3E6FA)

val AppleCanvasBg = AppBackground
val AppleSurfaceWhite = AppSurface
val AppleSurfaceElevated = AppSurface
val AppleBorderSubtle = AppBorder
val AppleBorderMedium = Color(0xFFD0D5DD)

val AppleTextPrimary = AppTextPrimary
val AppleTextSecondary = AppTextSecondary
val AppleTextMuted = AppTextMuted

// Voice Hero Radial Rings
val HeroMicRingOuter = Color(0x150875E1)
val HeroMicRingMedium = Color(0x300875E1)
val HeroMicListeningRingOuter = Color(0x20E53935)
val HeroMicListeningRingMedium = Color(0x40E53935)

val MicAppleGradientStart = Color(0xFF0875E1)
val MicAppleGradientEnd = Color(0xFF075BB5)
val MicAppleListeningStart = Color(0xFFE53935)
val MicAppleListeningEnd = Color(0xFFC62828)
val MicListeningGlow = Color(0x30E53935)

// Category Sub-Tints (Specified in Design System)
// Electricity -> Warm Yellow Tint
val TintElectricityBg = Color(0xFFFEF9C3)
val TintElectricityIcon = Color(0xFFCA8A04)

// Water -> Blue Tint
val TintWaterBg = Color(0xFFE0F2FE)
val TintWaterIcon = Color(0xFF0284C7)

// Mobile -> Violet Tint
val TintMobileBg = Color(0xFFF3E8FF)
val TintMobileIcon = Color(0xFF7E22CE)

// Gas -> Orange Tint
val TintGasBg = Color(0xFFFFEDD5)
val TintGasIcon = Color(0xFFC2410C)

// Category Card Icon Accents
val DoctorBlueIcon = Color(0xFF0875E1)
val DoctorBlueBg = Color(0xFFEBF3FC)
val DoctorBlueBorder = AppBorder

val BillsGreenIcon = Color(0xFF16A34A)
val BillsGreenBg = Color(0xFFDCFCE7)
val BillsGreenBorder = AppBorder

val FormsOrangeIcon = Color(0xFFD97706)
val FormsOrangeBg = Color(0xFFFEF3C7)
val FormsOrangeBorder = AppBorder

val HelpPurpleIcon = Color(0xFF7C3AED)
val HelpPurpleBg = Color(0xFFF3E8FF)
val HelpPurpleBorder = AppBorder

val SosRedIcon = AppEmergency
val SosRedBg = Color(0xFFFFEBEE)
val SosRedBorder = Color(0xFFFFCDD2)