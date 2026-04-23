package acj.soluciones.acjsignature.presentation.firma

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.components.ACJProgressIndicator
import acj.soluciones.acjsignature.shared.ui.theme.*
import acj.soluciones.acjsignature.shared.util.getFileName
import acj.soluciones.acjsignature.shared.util.getFileSize
import acj.soluciones.acjsignature.shared.util.toFormattedSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirmaScreen(
    onBack: () -> Unit,
    onNavigateToPosicionar: (Long) -> Unit,
    viewModel: FirmaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val borderColor = BorderDashed

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            val name = it.getFileName(context)
            val size = it.getFileSize(context)
            viewModel.onFileSelected(it, name, size)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Firmar Documento", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            // ── Progress ─────────────────────────────────────
            ACJProgressIndicator(currentStep = 1, totalSteps = 3)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Hero ─────────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = DeepPurple)) { append("Prepara tu\n") }
                    withStyle(SpanStyle(color = Magenta)) { append("Firma Digital") }
                },
                style = MaterialTheme.typography.displayMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sube tu documento PDF para iniciar el proceso de firma digital certificada",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Feature badges ───────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Filled.Security, null, tint = Magenta, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Seguridad Máxima", style = MaterialTheme.typography.labelMedium, color = DeepPurple)
                        Text("Cifrado AES-256", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Filled.Timeline, null, tint = Magenta, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Trazabilidad Total", style = MaterialTheme.typography.labelMedium, color = DeepPurple)
                        Text("Registro completo", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Upload Zone ──────────────────────────────────
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
                if (state.fileName != null) {
                    // File selected state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        /*Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = GreenActive,
                            modifier = Modifier.size(48.dp),
                        )*/
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.fileName!!,
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
                            onClick = { filePicker.launch(arrayOf("application/pdf")) },
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
                            text = "Máximo 25 MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ACJPrimaryButton(
                            text = "Examinar Archivos",
                            onClick = { filePicker.launch(arrayOf("application/pdf")) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Next Step ────────────────────────────────────
            ACJPrimaryButton(
                text = "Siguiente Paso",
                onClick = { viewModel.guardarYContinuar(onNavigateToPosicionar) },
                enabled = state.fileName != null && !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Magenta,
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}