package acj.soluciones.acjsignature.presentation.ajustes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import acj.soluciones.acjsignature.shared.ui.components.ACJFeatureCard
import acj.soluciones.acjsignature.shared.ui.theme.*

/**
 * Pantalla principal de ajustes que sirve como menú de navegación para
 * diversas configuraciones y utilidades de la aplicación.
 */
@Composable
fun AjustesScreen(
    onNavigateToAcercaDe: () -> Unit,
    onNavigateToConfiguracionFirma: () -> Unit,
    onNavigateToTSL: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        containerColor = SurfaceBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = DeepPurple)) { append("Ajustes") }
                },
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gestiona tus preferencias de firma y seguridad.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Menú de opciones
            ACJFeatureCard(
                title = "Acerca de ACJSignature",
                description = "Versión, términos y privacidad",
                icon = Icons.Default.Info,
                onClick = onNavigateToAcercaDe,
                iconBgColor = White
            )

            Spacer(modifier = Modifier.height(16.dp))

            ACJFeatureCard(
                title = "Configuración de firma",
                description = "Apariencia y certificados por defecto",
                icon = Icons.Default.Edit,
                onClick = onNavigateToConfiguracionFirma,
                iconBgColor = White
            )

            Spacer(modifier = Modifier.height(16.dp))

            ACJFeatureCard(
                title = "Configurar TSL",
                description = "Opciones de sellado de tiempo y confianza",
                icon = Icons.Default.Security,
                onClick = onNavigateToTSL,
                iconBgColor = White
            )

            Spacer(modifier = Modifier.height(16.dp))

            ACJFeatureCard(
                title = "Logs de Auditoría",
                description = "Historial de acciones y firmas",
                icon = Icons.Default.ListAlt,
                onClick = onNavigateToLogs,
                iconBgColor = White
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Logout / Cerrar sesión
            TextButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar sesión",
                        color = Error,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
