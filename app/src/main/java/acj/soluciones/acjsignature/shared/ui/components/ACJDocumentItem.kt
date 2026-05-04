package acj.soluciones.acjsignature.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import acj.soluciones.acjsignature.shared.ui.theme.DeepPurple
import acj.soluciones.acjsignature.shared.ui.theme.PinkLight
import acj.soluciones.acjsignature.shared.ui.theme.Magenta
import acj.soluciones.acjsignature.shared.ui.theme.TextBody
import acj.soluciones.acjsignature.shared.ui.theme.TextMuted
import acj.soluciones.acjsignature.shared.ui.theme.Error
import acj.soluciones.acjsignature.shared.util.toFormattedSize

/**
 * Ítem de lista que representa un documento PDF con sus metadatos y acciones rápidas.
 * Incluye un menú desplegable para operaciones avanzadas como compartir, descargar y eliminar.
 *
 * @param nombre Nombre del archivo a mostrar.
 * @param tamano Tamaño del archivo en bytes para ser formateado.
 * @param tipo Descripción del tipo o estado del documento.
 * @param onView Callback para visualizar el documento.
 * @param onShare Callback para compartir el archivo.
 * @param onDownload Callback para descargar/exportar el archivo.
 * @param onDetails Callback para mostrar el panel de detalles.
 * @param onDelete Callback para solicitar la eliminación del registro.
 * @param modifier Modificador de diseño opcional.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun ACJDocumentItem(
    nombre: String,
    tamano: Long,
    tipo: String,
    onView: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    onDetails: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PinkLight.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PictureAsPdf,
                contentDescription = null,
                tint = Magenta,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.titleSmall,
                color = DeepPurple,
                maxLines = 1,
                letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = tamano.toFormattedSize(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
                Text(
                    text = tipo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextBody,
                )
            }
        }
        // View action
        IconButton(onClick = onView, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Filled.Visibility,
                contentDescription = "Ver",
                tint = TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
        // More actions dropdown
        Box {
            IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Más opciones",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Compartir") },
                    onClick = {
                        menuExpanded = false
                        onShare()
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                )
                DropdownMenuItem(
                    text = { Text("Guardar en Descargas") },
                    onClick = {
                        menuExpanded = false
                        onDownload()
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.SaveAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                )
                DropdownMenuItem(
                    text = { Text("Ver detalles") },
                    onClick = {
                        menuExpanded = false
                        onDetails()
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                )
                DropdownMenuItem(
                    text = { Text("Eliminar", color = Error) },
                    onClick = {
                        menuExpanded = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Error, modifier = Modifier.size(20.dp))
                    },
                )
            }
        }
    }
}
