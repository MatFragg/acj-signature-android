package acj.soluciones.acjsignature.presentation.validarpdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJOutlinedButton
import acj.soluciones.acjsignature.shared.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla que presenta el resumen detallado del análisis de un documento PDF.
 * Muestra el estado global de integridad y desglosa cada firma encontrada,
 * permitiendo acceder a los detalles técnicos de cada certificado involucrado.
 *
 * @param onNavigateBack Callback para regresar al selector de archivos.
 * @param onNavigateToDetalle Callback para visualizar los pormenores de una firma específica.
 * @param viewModel ViewModel que contiene el resultado del análisis.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadoValidacionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetalle: (Int) -> Unit,
    viewModel: ValidarPdfViewModel
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val resultado = state.resultado ?: return
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    val totalFirmas = resultado.firmas.size
    val firmasValidas = resultado.firmas.count { it.esValida }
    val esValidoGlobal = resultado.esValido

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado de Validación", color = DeepPurple) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Estado global
                Icon(
                    imageVector = if (esValidoGlobal) Icons.Filled.CheckCircle else Icons.Filled.Error,
                    contentDescription = null,
                    tint = if (esValidoGlobal) GreenActive else Error,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (esValidoGlobal) "FIRMA VÁLIDA" else "FIRMA INVÁLIDA",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (esValidoGlobal) GreenDark else Error
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "$firmasValidas firma(s) válida(s) de $totalFirmas",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                
                // Mensaje General si lo hay
                if (resultado.mensajeGeneral.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultado.mensajeGeneral,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Error,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // Documento Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = Shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DOCUMENTO",
                            style = MaterialTheme.typography.labelSmall,
                            color = Magenta
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.fileName ?: "Documento",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepPurple
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dateFormat.format(Date()),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Firmas
            itemsIndexed(resultado.firmas) { index, firma ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = Shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (firma.esValida) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (firma.esValida) GreenActive else Error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = firma.firmante,
                                style = MaterialTheme.typography.titleSmall,
                                color = DeepPurple,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                color = if (firma.esValida) GreenOverlay else Error.copy(alpha = 0.1f),
                                shape = Shapes.small
                            ) {
                                Text(
                                    text = if (firma.esValida) "VÁLIDO >" else "INVÁLIDO >",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (firma.esValida) GreenDark else Error,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        if (!firma.esValida && !firma.mensajeError.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = firma.mensajeError,
                                style = MaterialTheme.typography.bodySmall,
                                color = Error
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ACJOutlinedButton(
                            text = "Ver detalles del certificado",
                            onClick = { onNavigateToDetalle(index) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
