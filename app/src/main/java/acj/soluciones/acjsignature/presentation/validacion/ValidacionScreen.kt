package acj.soluciones.acjsignature.presentation.validacion

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.data.local.db.EstadoDocumento
import acj.soluciones.acjsignature.shared.ui.components.ACJDocumentItem
import acj.soluciones.acjsignature.shared.ui.components.ACJEmptyState
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.components.ACJStatsCard
import acj.soluciones.acjsignature.shared.ui.theme.*
import acj.soluciones.acjsignature.shared.util.toFormattedSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de historial y validación que muestra la lista de documentos procesados.
 * Permite filtrar documentos mediante una barra de búsqueda, visualizar estadísticas rápidas
 * y acceder a acciones de gestión como compartir, descargar, ver detalles o eliminar.
 *
 * @param onNavigateToFirmar Callback para iniciar un nuevo proceso de firma.
 * @param viewModel ViewModel que gestiona el estado del repositorio de documentos.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidacionScreen(
    onNavigateToFirmar: () -> Unit,
    viewModel: ValidacionViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes en snackbar
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMensaje()
        }
    }

    // ── Diálogo de confirmación de eliminación ───────
    if (state.showEliminarDialog && state.documentoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarEliminar() },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(32.dp),
                )
            },
            title = {
                Text(
                    "Eliminar documento",
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Text(
                    "¿Estás seguro de eliminar \"${state.documentoAEliminar!!.nombre}\"? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBody,
                )
            },
            confirmButton = {
                ACJPrimaryButton(
                    text = "Eliminar",
                    onClick = { viewModel.onConfirmarEliminar() },
                )
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelarEliminar() }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
        )
    }

    // ── Bottom Sheet de detalles ─────────────────────
    if (state.showDetallesSheet && state.documentoSeleccionado != null) {
        val doc = state.documentoSeleccionado!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

        ModalBottomSheet(
            onDismissRequest = { viewModel.onDismissDetalles() },
            sheetState = sheetState,
            containerColor = White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
            ) {
                // Título
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = Magenta,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Detalles del Documento",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepPurple,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Filas de detalle
                DetalleRow("Nombre", doc.nombre)
                DetalleRow("Tamaño", doc.tamano.toFormattedSize())
                DetalleRow("Tipo", doc.tipoDocumento)
                DetalleRow(
                    "Estado",
                    when (doc.estado) {
                        EstadoDocumento.FIRMADO -> "✅ Firmado"
                        EstadoDocumento.ERROR -> "❌ Error en firma"
                        EstadoDocumento.PENDIENTE -> "⏳ Pendiente"
                        EstadoDocumento.VALIDADO -> "✔️ Validado"
                        else -> doc.estado
                    },
                )
                doc.aliasCertificado?.let {
                    DetalleRow("Certificado", it)
                }
                DetalleRow(
                    "Fecha de creación",
                    dateFormat.format(Date(doc.fechaCreacion)),
                )
                doc.fechaFirma?.let {
                    DetalleRow(
                        "Fecha de firma",
                        dateFormat.format(Date(it)),
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceBg,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 20.dp),
        ) {
            // ── Header ───────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = DeepPurple)) { append("Documentos\n") }
                            withStyle(SpanStyle(color = Magenta)) { append("Firmados") }
                        },
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    ACJPrimaryButton(
                        text = "Firmar",
                        onClick = onNavigateToFirmar,
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── Stats Cards ──────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ACJStatsCard(
                        title = "Total Documentos",
                        value = "${state.estadisticas.total}",
                        subtitle = "documentos registrados",
                        modifier = Modifier.weight(1f),
                    )
                    ACJStatsCard(
                        title = "Firmas Fallidas",
                        value = "${state.estadisticas.fallidos}",
                        subtitle = "errores detectados",
                        modifier = Modifier.weight(1f),
                        valueColor = Magenta,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                ACJStatsCard(
                    title = "Seguridad",
                    value = "${state.estadisticas.porcentajeSeguridad}%",
                    subtitle = "documentos firmados",
                    valueColor = GreenDark,
                )
                Spacer(modifier = Modifier.height(48.dp))
            }

            // ── Search ───────────────────────────────────
            item {
                Text(
                    text = "Repositorio de Documentos",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepPurple,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar documentos...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextMuted) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Magenta,
                    ),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Document List ────────────────────────────
            if (state.documentos.isEmpty()) {
                item {
                    ACJEmptyState(
                        icon = Icons.Filled.Description,
                        title = "Sin documentos",
                        description = "Firma tu primer documento para verlo aquí",
                    )
                }
            } else {
                items(state.documentos, key = { it.id }) { doc ->
                    ACJDocumentItem(
                        nombre = doc.nombre,
                        tamano = doc.tamano,
                        tipo = doc.tipoDocumento,
                        onView = {
                            val uri = viewModel.getDocumentoUri(doc, context)
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(
                                    Intent.createChooser(intent, "Abrir con")
                                )
                            }
                        },
                        onShare = {
                            val uri = viewModel.getDocumentoUri(doc, context)
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(
                                    Intent.createChooser(intent, "Compartir documento")
                                )
                            }
                        },
                        onDownload = {
                            viewModel.descargarADescargas(doc, context)
                        },
                        onDetails = {
                            viewModel.onVerDetalles(doc)
                        },
                        onDelete = {
                            viewModel.onSolicitarEliminar(doc)
                        },
                    )
                    Divider(color = DividerLight)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

/**
 * Fila utilitaria para mostrar un par etiqueta-valor dentro de los detalles del documento.
 */
@Composable
private fun DetalleRow(label: String, value: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            modifier = Modifier.weight(0.4f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = DeepPurple,
            modifier = Modifier.weight(0.6f),
        )
    }
    Divider(color = DividerLight)
}