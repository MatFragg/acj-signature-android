package acj.soluciones.acjsignature.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJDocumentItem
import acj.soluciones.acjsignature.shared.ui.components.ACJFeatureCard
import acj.soluciones.acjsignature.shared.ui.components.ACJTopAppBar
import acj.soluciones.acjsignature.shared.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToFirmar: () -> Unit,
    onNavigateToCertificados: () -> Unit,
    onNavigateToDocumentos: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { ACJTopAppBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToFirmar,
                containerColor = Magenta,
                contentColor = White,
                shape = CircleShape,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Firmar documento")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Badge ────────────────────────────────────────
            Box(
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Hero Title ───────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = DeepPurple)) { append("Menú ") }
                    withStyle(SpanStyle(color = Magenta)) { append("Principal") }
                },
                style = MaterialTheme.typography.displayMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gestiona tus documentos con firma digitals",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Feature Cards ────────────────────────────────
            ACJFeatureCard(
                title = "Firmar Documento",
                description = "Firma digital certificada",
                icon = Icons.Filled.Draw,
                onClick = onNavigateToFirmar,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ACJFeatureCard(
                title = "Certificados",
                description = "Gestión de certificados digitales",
                icon = Icons.Filled.VerifiedUser,
                onClick = onNavigateToCertificados,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ACJFeatureCard(
                title = "Documentos Firmados",
                description = "Historial y trazabilidad completa",
                icon = Icons.Filled.Description,
                onClick = onNavigateToDocumentos,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ACJFeatureCard(
                title = "Configuración",
                description = "Parámetros de firma y seguridad",
                icon = Icons.Filled.Settings,
                onClick = onNavigateToConfiguracion,
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Security Architecture Card ───────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepPurple),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                /*Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = GreenActive,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Arquitectura de Seguridad",
                            style = MaterialTheme.typography.titleMedium,
                            color = White,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("AES-256", "RSA-4096", "SHA-512").forEach { badge ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(White.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = GreenActive,
                                        modifier = Modifier.size(12.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PinkLight,
                                    )
                                }
                            }
                        }
                    }
                }*/
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Recent Documents ─────────────────────────────
            Text(
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
                            onMore = { },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
