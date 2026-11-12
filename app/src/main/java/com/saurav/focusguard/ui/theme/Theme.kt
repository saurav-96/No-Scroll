package com.saurav.focusguard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val DeepSpaceNavy = Color(0xFF050A18)
val TopRadialMagenta = Color(0xFF280B29)
val CardNavy = Color(0xFF0B1831)
val CardNavyElevated = Color(0xFF10203F)
val InnerDarkNavy = Color(0xFF071022)
val CardStrokeBlue = Color(0xFF1A2A4A)

val PrimaryMagenta = Color(0xFFEC0D66)
val HotCrimson = Color(0xFFEC0D41)
val ElectricIndigo = Color(0xFF3742FF)
val CuriousBlue = Color(0xFF4263EB)
val ActiveTabMaroon = Color(0xFF8A124C)
val ActivePlanPlum = Color(0xFF3B0C2E)
val PlanBannerPlum = Color(0xFF270D2B)
val PauseAmber = Color(0xFFFFA726)
val EmeraldWatchtime = Color(0xFF0B5E46)
val CollapseLavender = Color(0xFFD9E2FF)
val TextPrimaryWhite = Color(0xFFFFFFFF)
val TextSecondarySlate = Color(0xFF9EABC2)
val TextMutedGrey = Color(0xFF6C7A94)
val BottomBarNavy = Color(0xFF081226)

val PinkStatsGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFEC0D4B), Color(0xFFEC0D6E))
)

val PremiumPinkPurpleGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFEC0D50), Color(0xFF3742FF))
)

val DiamondCircleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFEC0D66), Color(0xFF4B39EF))
)

private val FocusGuardColorScheme = darkColorScheme(
    primary = PrimaryMagenta,
    secondary = ElectricIndigo,
    tertiary = PauseAmber,
    background = DeepSpaceNavy,
    surface = CardNavy,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimaryWhite,
    onSurface = TextPrimaryWhite
)

@Composable
fun FocusGuardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FocusGuardColorScheme,
        content = content
    )
}
