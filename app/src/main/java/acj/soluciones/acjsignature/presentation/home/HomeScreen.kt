package acj.soluciones.acjsignature.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import acj.soluciones.acjsignature.shared.ui.components.ACJFeatureCard
import acj.soluciones.acjsignature.shared.ui.theme.*

/**
 * Pantalla principal de la aplicación que actúa como panel de control.
 * Ofrece acceso directo a las funcionalidades de firma, validación, gestión de certificados e historial.
 *
 * @param onNavigateToFirmar Navegación al flujo de firma de documentos.
 * @param onNavigateToCertificados Navegación a la gestión de identidades digitales.
 * @param onNavigateToDocumentos Navegación al historial de archivos procesados.
 * @param onNavigateToConfiguracion Navegación a las preferencias de la aplicación.
 * @param onNavigateToValidar Navegación al flujo de verificación de firmas.
 * @param viewModel Modelo de vista para el estado de la pantalla de inicio.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */

@Composable
fun HomeScreen(
    onNavigateToFirmar: () -> Unit,
    onNavigateToCertificados: () -> Unit,
    onNavigateToDocumentos: () -> Unit,
    onNavigateToAjustes: () -> Unit,
    onNavigateToValidar: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold{ padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Badge ────────────────────────────────────────
            /*Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GreenOverlay)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GreenActive),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SECURITY PROTOCOL ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenDark,
                    )
                }
            }*/

            Spacer(modifier = Modifier.height(16.dp))

            // Titulo
            Text(
                text = "Inicio",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Gestiona tus documentos con firma digital",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Opciones
            ACJFeatureCard(
                title = "Firmar Documento",
                description = "Firma digital",
                icon = Icons.Filled.Draw,
                onClick = onNavigateToFirmar,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ACJFeatureCard(
                title = "Validar Documento",
                description = "Verifica la integridad y firmas de un documento",
                icon = Icons.Filled.Security,
                onClick = onNavigateToValidar,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ACJFeatureCard(
                title = "Certificados",
                description = "Gestión de certificados digitales",
                icon = Icons.Filled.VerifiedUser,
                onClick = onNavigateToCertificados,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ACJFeatureCard(
                title = "Documentos Firmados",
                description = "Historial y trazabilidad completa",
                icon = Icons.Filled.Description,
                onClick = onNavigateToDocumentos,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ACJFeatureCard(
                title = "Ajustes",
                description = "Preferencias de firma y seguridad",
                icon = Icons.Filled.Settings,
                onClick = onNavigateToAjustes,
            )

            Spacer(modifier = Modifier.height(28.dp))


            // ── Recent Documents ─────────────────────────────
            /*Text(
                text = "Resumen Reciente",
                style = MaterialTheme.typography.titleLarge,
                color = DeepPurple,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.documentosRecientes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                ) {
                    Text(
                        text = "No hay documentos recientes. ¡Firma tu primer documento!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(20.dp),
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                ) {
                    state.documentosRecientes.forEach { doc ->
                        ACJDocumentItem(
                            nombre = doc.nombre,
                            tamano = doc.tamano,
                            tipo = doc.tipoDocumento,
                            onView = { },
                            onShare = { },
                            onDownload = { },
                            onDetails = { },
                            onDelete = { },
                        )
                    }
                }
            }*/

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
