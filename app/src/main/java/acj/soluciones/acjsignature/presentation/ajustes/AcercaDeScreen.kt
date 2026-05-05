package acj.soluciones.acjsignature.presentation.ajustes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import acj.soluciones.acjsignature.R
import acj.soluciones.acjsignature.shared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcercaDeScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de", color = DeepPurple, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = DeepPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = SurfaceBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // App Logo Placeholder (could use a real icon if available)
            Surface(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = DeepPurple,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ACJ", color = White, style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ACJSignature",
                style = MaterialTheme.typography.headlineSmall,
                color = DeepPurple,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Versión 1.0.0 (Beta)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Solución integral de firma digital para dispositivos móviles Android, garantizando la integridad y autenticidad de tus documentos.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextBody,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "© 2026 ACJ Soluciones Digitales\nTodos los derechos reservados",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
