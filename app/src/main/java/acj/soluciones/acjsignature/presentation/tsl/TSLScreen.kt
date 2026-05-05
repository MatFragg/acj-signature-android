package acj.soluciones.acjsignature.presentation.tsl

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.theme.*

/**
 * Pantalla para configurar el entorno de validación TSL (Trusted Service List).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TSLScreen(
    onNavigateBack: () -> Unit,
    viewModel: TSLViewModel = hiltViewModel()
) {
    val usarTslPrueba by viewModel.usarTslPrueba.collectAsStateWithLifecycle()
    val guardado by viewModel.guardado.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(guardado) {
        if (guardado) {
            snackbarHostState.showSnackbar("Configuración TSL guardada")
            viewModel.clearGuardado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración TSL", color = DeepPurple, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = DeepPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                text = "Entorno de Confianza",
                style = MaterialTheme.typography.titleLarge,
                color = DeepPurple
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "La TSL permite validar que los certificados utilizados sean emitidos por entidades acreditadas por INDECOPI.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Usar TSL de prueba",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DeepPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Activa validaciones contra el entorno de pruebas de INDECOPI.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = usarTslPrueba,
                        onCheckedChange = { 
                            viewModel.onUsarTslPruebaChanged(it)
                            // Limpiar cache para forzar recarga si cambia el entorno
                            com.acj.firma.util.Tsl.limpiarCache(context)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Magenta,
                            checkedTrackColor = PinkLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            ACJPrimaryButton(
                text = "Guardar Cambios",
                onClick = { viewModel.guardar() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
