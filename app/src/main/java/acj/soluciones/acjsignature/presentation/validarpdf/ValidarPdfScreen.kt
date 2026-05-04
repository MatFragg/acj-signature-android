package acj.soluciones.acjsignature.presentation.validarpdf

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.theme.*
import acj.soluciones.acjsignature.shared.util.toFormattedSize

/**
 * Pantalla que permite al usuario seleccionar un archivo PDF externo para su validación.
 * Muestra una zona de carga interactiva y gestiona el lanzamiento del selector de archivos.
 *
 * @param onNavigateBack Callback para regresar a la pantalla anterior.
 * @param onNavigateToResultados Callback para avanzar a la visualización de resultados tras el análisis.
 * @param viewModel ViewModel que gestiona la lógica de selección y validación.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidarPdfScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResultados: () -> Unit,
    viewModel: ValidarPdfViewModel
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            var name = "documento.pdf"
            var size = 0L
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex)
                    size = cursor.getLong(sizeIndex)
                }
            }
            viewModel.onFileSelected(it, name, size)
        }
    }

    Scaffold(

        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(24.dp)
            ) {
                ACJPrimaryButton(
                    text = "Validar Documento →",
                    onClick = { viewModel.validarDocumento(context, onNavigateToResultados) },
                    enabled = state.fileUri != null && !state.isValidating,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = DeepPurple)) { append("Validar\n") }
                    withStyle(SpanStyle(color = Magenta)) { append("Documento") }
                },
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selecciona un documento PDF para verificar su firma digital.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Upload Zone ──────────────────────────────────
            val borderColor = BorderDashed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRoundRect(
                            color = borderColor,
                            cornerRadius = CornerRadius(12.dp.toPx()),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                            ),
                        )
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(PinkLight.copy(alpha = 0.15f))
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (state.isValidating) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Magenta)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Validando documento...", style = MaterialTheme.typography.titleMedium, color = DeepPurple)
                    }
                } else if (state.fileUri != null) {
                    // File selected state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.fileName ?: "",
                            style = MaterialTheme.typography.titleSmall,
                            color = DeepPurple,
                            textAlign = TextAlign.Center,
                            letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                        )
                        Text(
                            text = state.fileSize.toFormattedSize(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ACJPrimaryButton(
                            text = "Cambiar archivo",
                            onClick = { launcher.launch(arrayOf("application/pdf")) },
                        )
                    }
                } else {
                    // Empty state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CloudUpload,
                            contentDescription = null,
                            tint = Magenta,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Selecciona un archivo PDF",
                            style = MaterialTheme.typography.titleSmall,
                            color = DeepPurple,
                            letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                        )
                        Text(
                            text = "Máximo 50 MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ACJPrimaryButton(
                            text = "Examinar Archivos",
                            onClick = { launcher.launch(arrayOf("application/pdf")) },
                        )
                    }
                }
            }
    }
}
}
