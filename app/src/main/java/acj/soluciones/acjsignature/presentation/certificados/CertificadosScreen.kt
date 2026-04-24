package acj.soluciones.acjsignature.presentation.certificados

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJEmptyState
import acj.soluciones.acjsignature.shared.ui.components.ACJFeatureCard
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.components.ACJTopAppBar
import acj.soluciones.acjsignature.shared.ui.theme.*
import androidx.compose.material.icons.automirrored.filled.Help
import kotlinx.coroutines.launch

@Composable
fun CertificadosScreen(
    viewModel: CertificadosViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // PIN creation state
    var pin by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

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
     * Verifica permiso y abre el picker.
     * - Android 13+ (API 33): No necesita permiso → abre directo.
     * - Android <13: Pide READ_EXTERNAL_STORAGE si no está concedido.
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

    // PIN creation dialog
    if (state.showPinDialog) {
        val pinIsValid = pin.length == 6 && pin.all { it.isDigit() }
        val pinsMatch = pin == pinConfirm
        val canConfirm = pinIsValid && pinsMatch

        AlertDialog(
            onDismissRequest = {
                viewModel.onCancelPin()
                pin = ""
                pinConfirm = ""
                pinError = null
            },
            icon = {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = DeepPurple,
                    modifier = Modifier.size(32.dp),
                )
            },
            title = {
                Text(
                    "Crear PIN de Seguridad",
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Column {
                    Text(
                        "Crea un PIN de 6 dígitos numéricos para proteger el uso de este certificado. Se te solicitará cada vez que firmes un documento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBody,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { newValue ->
                            // Solo permitir dígitos, máximo 6
                            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                                pin = newValue
                                pinError = null
                            }
                        },
                        label = { Text("PIN (6 dígitos)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = pin.isNotEmpty() && !pinIsValid,
                        supportingText = {
                            if (pin.isNotEmpty() && !pinIsValid) {
                                Text("El PIN debe tener exactamente 6 dígitos", color = Error)
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pinConfirm,
                        onValueChange = { newValue ->
                            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                                pinConfirm = newValue
                                pinError = null
                            }
                        },
                        label = { Text("Confirmar PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = pinConfirm.isNotEmpty() && !pinsMatch,
                        supportingText = {
                            if (pinConfirm.isNotEmpty() && !pinsMatch) {
                                Text("Los PINs no coinciden", color = Error)
                            }
                        },
                    )

                    pinError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(it, color = Error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                ACJPrimaryButton(
                    text = "Confirmar PIN",
                    onClick = {
                        if (canConfirm) {
                            viewModel.onPinConfirmed(pin)
                            pin = ""
                            pinConfirm = ""
                            pinError = null
                        }
                    },
                    enabled = canConfirm,
                )
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onCancelPin()
                    pin = ""
                    pinConfirm = ""
                    pinError = null
                }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
        )
    }

    Scaffold(
        topBar = { ACJTopAppBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Warning Banner ───────────────────────────
            Card(
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
                    Text(
                        text = "Validación Pendiente",
                        style = MaterialTheme.typography.labelMedium,
                        color = YellowWarningText,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Title ────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = DeepPurple)) { append("Mis ") }
                    withStyle(SpanStyle(color = Magenta)) { append("Certificados") }
                },
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                            Box(
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
                            }
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
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}