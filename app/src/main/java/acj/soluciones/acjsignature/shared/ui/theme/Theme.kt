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

/**
 * Configuración del tema global de la aplicación basado en Material Design 3.
 * Define las paletas de colores para modo claro y oscuro, tipografía y formas personalizadas.
 *
 * @param darkTheme Indica si se debe aplicar el esquema de colores oscuro.
 * @param content El árbol de componentes (Composables) que se renderizará bajo este tema.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
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