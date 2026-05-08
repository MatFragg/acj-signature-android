package acj.soluciones.acjsignature.presentation.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.theme.*
import acj.soluciones.acjsignature.shared.util.AppLogger

/**
 * Pantalla de visualización de logs de auditoría.
 * Permite filtrar por nivel de log, compartir el historial y limpiar los registros.
 *
 * @param onNavigateBack Callback para regresar a la pantalla anterior.
 * @param viewModel Instancia del ViewModel de logs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsAuditoriaScreen(
    onNavigateBack: () -> Unit,
    viewModel: LogViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs de Auditoría", color = DeepPurple, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = DeepPurple)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshLogs() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = DeepPurple)
                    }
                    IconButton(onClick = { viewModel.clearLogs(true) }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Limpiar", tint = Error)
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
        ) {
            // Lista de Logs
            if (logs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay logs registrados", color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(DeepPurple.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { entry ->
                        LogEntryItem(entry)
                    }
                }
            }
        }
    }
}

@Composable
fun LogEntryItem(entry: AppLogger.LogEntry) {
    val color = when (entry.level) {
        AppLogger.LogLevel.INFO -> GreenDark
        AppLogger.LogLevel.WARNING -> YellowWarningText
        AppLogger.LogLevel.ERROR -> Error
        AppLogger.LogLevel.DEBUG -> TextMuted
    }

    Text(
        text = entry.format(),
        color = color,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            lineHeight = 14.sp
        ),
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    )
}
