package acj.soluciones.acjsignature.presentation.validacion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJDocumentItem
import acj.soluciones.acjsignature.shared.ui.components.ACJEmptyState
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.components.ACJStatsCard
import acj.soluciones.acjsignature.shared.ui.components.ACJTopAppBar
import acj.soluciones.acjsignature.shared.ui.theme.*

@Composable
fun ValidacionScreen(
    onNavigateToFirmar: () -> Unit,
    viewModel: ValidacionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { ACJTopAppBar() },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                Spacer(modifier = Modifier.height(24.dp))
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
                        onView = { },
                        onMore = { },
                    )
                    Divider(color = DividerLight)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}