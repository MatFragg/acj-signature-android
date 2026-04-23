package acj.soluciones.acjsignature.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import acj.soluciones.acjsignature.R

// ── Font Families ───────────────────────────────────────────────────────

val Poppins = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.poppins_regular, FontWeight.Normal),
    androidx.compose.ui.text.font.Font(R.font.poppins_medium, FontWeight.Medium),
    androidx.compose.ui.text.font.Font(R.font.poppins_semibold, FontWeight.SemiBold),
    androidx.compose.ui.text.font.Font(R.font.poppins_bold, FontWeight.Bold),
)

val PlusJakartaSans = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.plusjakartasans_bold, FontWeight.Bold),
    androidx.compose.ui.text.font.Font(R.font.plusjakartasans_extrabold, FontWeight.ExtraBold),
)

val Manrope = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.manrope_semibold, FontWeight.SemiBold),
    androidx.compose.ui.text.font.Font(R.font.manrope_bold, FontWeight.Bold),
)

// ── Typography Scale (mapped to Figma) ──────────────────────────────────
val Typography = Typography(
    // Display — Plus Jakarta Sans ExtraBold 48sp (hero titles)
    displayLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 53.sp,
        letterSpacing = (-1.2).sp,
    ),
    // Display Medium — Plus Jakarta Sans ExtraBold 36sp (section titles)
    displayMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 45.sp,
        letterSpacing = (-1.8).sp,
    ),
    // Headline Large — Poppins SemiBold 30sp (page titles)
    headlineLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.75).sp,
    ),
    // Headline Medium — Plus Jakarta Sans Bold 24sp (card titles, app bar)
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.6).sp,
    ),
    // Headline Small — Poppins Bold 20sp (card headers)
    headlineSmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    // Title Large — Poppins SemiBold 18sp (section headers)
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    ),
    // Title Medium — Plus Jakarta Sans Bold 16sp (sub-headers)
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    // Title Small — Poppins SemiBold 14sp (small headers)
    titleSmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.4.sp,
    ),
    // Body Large — Poppins Regular 18sp (hero descriptions)
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 29.sp,
    ),
    // Body Medium — Poppins Regular 14sp (standard body)
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    // Body Small — Poppins Regular 12sp (captions)
    bodySmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    // Label Large — Poppins SemiBold 16sp (buttons)
    labelLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    // Label Medium — Manrope Bold 12sp (nav labels, badges)
    labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    // Label Small — Poppins Bold 10sp (uppercase labels)
    labelSmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 15.sp,
        letterSpacing = 1.sp,
    ),
)