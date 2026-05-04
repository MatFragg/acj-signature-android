package acj.soluciones.acjsignature.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import acj.soluciones.acjsignature.shared.ui.theme.DeepPurple
import acj.soluciones.acjsignature.shared.ui.theme.GradientMagentaEnd
import acj.soluciones.acjsignature.shared.ui.theme.GradientMagentaStart
import acj.soluciones.acjsignature.shared.ui.theme.Magenta
import acj.soluciones.acjsignature.shared.ui.theme.TextMuted
import acj.soluciones.acjsignature.shared.ui.theme.White

/**
 * Botón principal con el color magenta de la marca.
 * Utilizado para acciones afirmativas primarias.
 *
 * @param text Etiqueta de texto a mostrar en mayúsculas.
 * @param onClick Acción a ejecutar al presionar.
 * @param modifier Modificador de diseño opcional.
 * @param enabled Indica si el botón responde a la interacción.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun ACJPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Magenta,
            contentColor = White,
            disabledContainerColor = Magenta.copy(alpha = 0.4f),
            disabledContentColor = White.copy(alpha = 0.7f),
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = MaterialTheme.typography.titleSmall.letterSpacing,
        )
    }
}

/**
 * Botón con fondo degradado (Magenta a MagentaLight).
 * Provee una estética premium para acciones destacadas.
 *
 * @param text Etiqueta de texto.
 * @param onClick Acción al presionar.
 * @param modifier Modificador de diseño.
 * @param enabled Habilitación del botón.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun ACJGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GradientMagentaStart, GradientMagentaEnd),
                    ),
                    shape = RoundedCornerShape(12.dp),
                )
                .height(52.dp)
                .then(modifier),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = White,
        )
    }
}

/**
 * Botón con borde (outlined) y texto en color DeepPurple.
 * Utilizado para acciones secundarias o alternativas.
 *
 * @param text Texto del botón.
 * @param onClick Acción al presionar.
 * @param modifier Modificador de diseño.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun ACJOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = DeepPurple,
        )
    }
}

/**
 * Botón secundario con texto atenuado (Muted).
 * Ideal para acciones de "Cancelar" o menos importantes.
 *
 * @param text Texto del botón.
 * @param onClick Acción al presionar.
 * @param modifier Modificador de diseño.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun ACJSecondaryButton(

    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
        )
    }
}
