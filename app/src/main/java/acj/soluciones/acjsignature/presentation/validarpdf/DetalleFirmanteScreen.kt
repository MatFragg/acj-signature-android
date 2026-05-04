package acj.soluciones.acjsignature.presentation.validarpdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla que muestra los metadatos técnicos y de auditoría de una firma específica.
 * Desglosa información del firmante, la entidad emisora del certificado, vigencia,
 * formato de firma y marcas de tiempo (timestamping) si están presentes.
 *
 * @param firmaIndex Índice de la firma dentro de los resultados de validación.
 * @param onNavigateBack Callback para regresar al listado de firmas.
 * @param viewModel ViewModel que contiene los datos del análisis forense.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun DetalleFirmanteScreen(
    firmaIndex: Int,
    onNavigateBack: () -> Unit,
    viewModel: ValidarPdfViewModel
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val resultado = state.resultado ?: return
    val firma = resultado.firmas.getOrNull(firmaIndex) ?: return
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Detalle del firmante", color = DeepPurple) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = DeepPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                // 1. DATOS DEL FIRMANTE
                DetalleCard(title = "DATOS DEL FIRMANTE") {
                    firma.subjectDn?.let { 
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = DeepPurple,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    
                    val estado = if (firma.esValida) "VÁLIDO" else "INDETERMINADO"
                    DetalleItem("Estado", estado)
                    
                    firma.dniRuc?.let { DetalleItem("DNI / RUC / ID", it) }
                    
                    if (!firma.mensajeError.isNullOrBlank()) {
                        Text(
                            text = firma.mensajeError,
                            style = MaterialTheme.typography.bodySmall,
                            color = DeepPurple,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. DATOS DEL CERTIFICADO
                DetalleCard(title = "DATOS DEL CERTIFICADO") {
                    firma.empresa?.let { DetalleItem("Empresa", it) }
                    firma.unidad?.let { DetalleItem("Unidad Organizacional", it) }
                    firma.emisorCertificado?.let { DetalleItem("Emitido por", it) }
                    firma.serialCertificado?.let { DetalleItem("Número de serie", it) }
                    firma.fechaEmision?.let { DetalleItem("Fecha de emisión", dateFormat.format(it)) }
                    firma.fechaExpiracion?.let { DetalleItem("Fecha de expiración", dateFormat.format(it)) }
                    firma.formatoCertificado?.let { DetalleItem("Formato firma", it) }
                    firma.fechaFirma?.let { DetalleItem("Fecha de firma", dateFormat.format(it)) }
                }

                // 3. SELLO DE TIEMPO
                if (firma.selloMarcaDeHora != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DetalleCard(title = "SELLO DE TIEMPO") {
                        firma.selloEmitidoPor?.let { DetalleItem("Emitido por", it) }
                        firma.selloMarcaDeHora.let { DetalleItem("Marca de Hora", dateFormat.format(it)) }
                        firma.selloValidoHasta?.let { DetalleItem("Válido Hasta", dateFormat.format(it)) }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Contenedor visual para agrupar campos de información relacionada bajo un título.
 */
@Composable
private fun DetalleCard(title: String, content: @Composable ColumnScope.() -> Unit) {

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = Shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = DeepPurple
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * Muestra un par etiqueta-valor con estilo estandarizado.
 */
@Composable
private fun DetalleItem(label: String, value: String) {

    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = DeepPurple
        )
    }
}
