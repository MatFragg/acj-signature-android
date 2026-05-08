package acj.soluciones.acjsignature.presentation.certificados

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJEmptyState
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.theme.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import kotlinx.coroutines.launch
import acj.soluciones.acjsignature.domain.model.Certificado

/**
 * Pantalla para la gestión de certificados digitales del usuario.
 * Permite visualizar certificados importados, eliminarlos mediante gestos de deslizamiento
 * e importar nuevos certificados desde archivos .p12 o .pfx.
 *
 * @param viewModel ViewModel que maneja el estado y las acciones de los certificados.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificadosScreen(
    viewModel: CertificadosViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()


    // Deletion confirmation state
    var certificateToDelete by remember { mutableStateOf<Certificado?>(null) }

    // MIME types expandidos para asegurar que los `.p12` o `.pfx` siempre sean seleccionables
    val p12MimeTypes = arrayOf(
        "application/x-pkcs12",
        "application/pkcs-12",
        "application/x-pem-file",
        "application/octet-stream",
        "application/*",
        "*/*" 
    )

    val p12Picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            // Verificar extensión del archivo
            val name = it.lastPathSegment ?: ""
            // sin extensión = confiar en MIME

            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                viewModel.onP12Selected(it, bytes)
            }
        }
    }

    // Launcher para pedir permiso de almacenamiento en runtime
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            p12Picker.launch(p12MimeTypes)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Se necesita permiso de almacenamiento para importar certificados"
                )
            }
        }
    }

    /**
     * Inicia el selector de archivos tras verificar los permisos necesarios.
     */
    fun launchP12Picker() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33+: SAF no necesita permisos
            p12Picker.launch(p12MimeTypes)
        } else {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            when (ContextCompat.checkSelfPermission(context, permission)) {
                PackageManager.PERMISSION_GRANTED -> p12Picker.launch(p12MimeTypes)
                else -> storagePermissionLauncher.launch(permission)
            }
        }
    }

    LaunchedEffect(state.error, state.importSuccess) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
        state.importSuccess?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    // Password dialog
    if (state.showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelImport() },
            title = { Text("Contraseña del Certificado", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(
                        "Ingresa la contraseña del archivo .p12/.pfx",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBody,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            },
            confirmButton = {
                ACJPrimaryButton(
                    text = "Importar",
                    onClick = {
                        viewModel.onPasswordConfirmed(password)
                        password = ""
                    },
                )
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onCancelImport()
                    password = ""
                }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
        )
    }


    // Delete confirmation dialog
    if (certificateToDelete != null) {
        AlertDialog(
            onDismissRequest = { certificateToDelete = null },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(32.dp),
                )
            },
            title = {
                Text(
                    "¿Estás seguro?",
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Text(
                    "¿Estás seguro de realizar esta acción? El certificado se eliminará permanentemente de este dispositivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBody,
                )
            },
            confirmButton = {
                ACJPrimaryButton(
                    text = "Eliminar",
                    onClick = {
                        certificateToDelete?.let { viewModel.eliminarCertificado(it.alias) }
                        certificateToDelete = null
                    },
                )
            },
            dismissButton = {
                TextButton(onClick = { certificateToDelete = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Warning Banner ───────────────────────────
            /*Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = YellowWarning),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = YellowWarningText,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    /*Text(
                        text = "Validación Pendiente",
                        style = MaterialTheme.typography.labelMedium,
                        color = YellowWarningText,
                    )*/
                }
            }*/

            Spacer(modifier = Modifier.height(8.dp))

            // ── Title ────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = DeepPurple)) { append("Mis ") }
                    withStyle(SpanStyle(color = Magenta)) { append("Certificados") }
                },
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Gestiona tus certificados digitales para firma digital",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Import Button ────────────────────────────
            ACJPrimaryButton(
                text = "Importar Certificado (.p12 / .pfx)",
                onClick = { launchP12Picker() },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Certificates List or Empty State ─────────
            if (state.certificados.isEmpty()) {
                ACJEmptyState(
                    icon = Icons.Filled.Shield,
                    title = "No tienes Certificados",
                    description = "Importa un certificado .p12 para comenzar a firmar documentos digitalmente",
                )
            } else {
                state.certificados.forEach { cert ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                certificateToDelete = cert
                            }
                            false // Snap back while waiting for confirmation
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.EndToStart -> Error.copy(alpha = 0.8f)
                                SwipeToDismissBoxValue.StartToEnd -> Error.copy(alpha = 0.8f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color),
                                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) 
                                    Alignment.CenterStart else Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                /*Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (cert.estaVigente) GreenOverlay else Error.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        if (cert.estaVigente) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = if (cert.estaVigente) GreenActive else Error,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }*/
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        cert.etiquetaDisplay,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = DeepPurple,
                                        letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                                    )
                                    Text(
                                        cert.organizacion,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextBody,
                                    )
                                    Text(
                                        "Emisor: ${cert.emisor}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}