package acj.soluciones.acjsignature.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import acj.soluciones.acjsignature.shared.ui.theme.CardBg
import acj.soluciones.acjsignature.shared.ui.theme.DeepPurple
import acj.soluciones.acjsignature.shared.ui.theme.TextBody

/**
 * Tarjeta informativa para mostrar métricas o estadísticas rápidas.
 * Presenta un título, un valor numérico resaltado y un subtítulo descriptivo.
 *
 * @param title Etiqueta superior de la estadística.
 * @param value Valor principal a resaltar (ej. "25", "100%").
 * @param subtitle Texto inferior que provee contexto al valor.
 * @param modifier Modificador de diseño opcional.
 * @param valueColor Color aplicado al texto del valor principal.
 * @param containerColor Color de fondo de la tarjeta.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun ACJStatsCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: Color = DeepPurple,
    containerColor: Color = CardBg,
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextBody,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                color = valueColor,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextBody,
            )
        }
    }
}
