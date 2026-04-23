package acj.soluciones.acjsignature.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Magenta,
    onPrimary = White,
    primaryContainer = PinkLight,
    onPrimaryContainer = DeepPurple,
    secondary = DeepPurple,
    onSecondary = White,
    secondaryContainer = PinkSoft,
    onSecondaryContainer = DeepPurple,
    tertiary = GreenActive,
    onTertiary = White,
    tertiaryContainer = GreenOverlay,
    surface = SurfaceBg,
    onSurface = TextPrimary,
    surfaceVariant = CardBg,
    onSurfaceVariant = TextBody,
    background = White,
    onBackground = TextPrimary,
    outline = BorderDashed,
    outlineVariant = BorderLight,
    error = Error,
    onError = White,
    inverseSurface = DeepPurple,
    inverseOnSurface = PinkLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = MagentaLight,
    onPrimary = DeepPurple,
    primaryContainer = Magenta,
    onPrimaryContainer = PinkLight,
    secondary = PinkLight,
    onSecondary = DeepPurple,
    surface = DeepPurple,
    onSurface = White,
    surfaceVariant = GradientDarkEnd,
    onSurfaceVariant = PinkLight,
    background = DeepPurple,
    onBackground = White,
    outline = BorderDashed,
    outlineVariant = BorderMedium,
    error = Error,
    onError = White,
)

@Composable
fun ACJSignatureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}