package acj.soluciones.acjsignature.presentation.configuracion

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.components.ACJSecondaryButton
import acj.soluciones.acjsignature.shared.ui.theme.*
import android.annotation.SuppressLint
import androidx.core.net.toUri

/**
 * Pantalla de configuración que permite al usuario personalizar su firma digital.
 * Provee opciones para subir un logo, seleccionar campos visibles y alternar entre entornos TSL.
 *
 * @param viewModel Instancia del ViewModel para gestionar el estado y eventos.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@SuppressLint("LocalContextResourcesRead")
@Composable
fun ConfiguracionScreen(
    viewModel: ConfiguracionViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val borderColor = BorderDashed
    val context = LocalContext.current

    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        viewModel.onLogoChanged(uri?.toString())
    }

    // Load logo bitmap from URI or default shield
    val logoBitmap: ImageBitmap? by produceState<ImageBitmap?>(
        initialValue = null,
        key1 = state.logoUri,
    ) {
        val customBitmap = state.logoUri?.let { uriString ->
            runCatching {
                val uri = uriString.toUri()
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
        
        value = customBitmap ?: runCatching {
            BitmapFactory.decodeResource(context.resources, acj.soluciones.acjsignature.R.raw.escudo_peru)?.asImageBitmap()
        }.getOrNull()
    }

    LaunchedEffect(state.guardado) {
        if (state.guardado) {
            snackbarHostState.showSnackbar("Configuración guardada")
            viewModel.clearGuardado()
        }
    }

    Scaffold(
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

            // ── Title ────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = DeepPurple)) { append("Configuración\nde ") }
                    withStyle(SpanStyle(color = Magenta)) { append("Firma") }
                },
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Personaliza la apariencia de tu firma digital",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Logo Upload ──────────────────────────────
            Text(
                text = "Logo para la firma",
                style = MaterialTheme.typography.titleLarge,
                color = DeepPurple,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    append("Se recomienda un logo de ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("84 × 84 px")
                    }
                    append(" para mejor resultado")
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (logoBitmap != null) 200.dp else 180.dp)
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
                    .background(PinkLight.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (logoBitmap != null) {
                        // Logo preview
                        Image(
                            bitmap = logoBitmap!!,
                            contentDescription = "Vista previa del logo",
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.logoUri != null) "Logo personalizado ✓" else "Logo por defecto (Escudo)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.logoUri != null) GreenDark else TextMuted,
                        )
                    } else {
                        Icon(Icons.Filled.CloudUpload, null, tint = Magenta, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Subir logo", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ACJPrimaryButton(
                        text = if (state.logoUri != null) "Cambiar logo" else "Seleccionar personalizado",
                        onClick = { logoPicker.launch("image/*") },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Field Checkboxes ─────────────────────────
            Text(
                text = "Datos del certificado a mostrar",
                style = MaterialTheme.typography.titleLarge,
                color = DeepPurple,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Los valores se obtienen automáticamente del certificado digital",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(8.dp))
            CheckboxItem("Empresa", state.incluirEmpresa, viewModel::onIncluirEmpresaChanged)
            CheckboxItem("Cargo", state.incluirCargo, viewModel::onIncluirCargoChanged)

            Spacer(modifier = Modifier.height(24.dp))

            // ── TSL Configuration ────────────────────────
            Text(
                text = "Validación (TSL)",
                style = MaterialTheme.typography.titleLarge,
                color = DeepPurple,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecciona el entorno de confianza para validar firmas",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(8.dp))
            CheckboxItem("Usar TSL de prueba (INDECOPI)", state.usarTslPrueba) {
                viewModel.onUsarTslPruebaChanged(it)
                com.acj.firma.util.Tsl.limpiarCache(context)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Signature Preview ────────────────────────
            Text(
                text = "Vista previa",
                style = MaterialTheme.typography.titleLarge,
                color = DeepPurple,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeepPurple, Magenta),
                            )
                        )
                        .padding(20.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Logo thumbnail on the left (if selected)
                        if (logoBitmap != null) {
                            Image(
                                bitmap = logoBitmap!!,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Fit,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "FIRMA DIGITAL CERTIFICADA",
                                style = MaterialTheme.typography.labelSmall,
                                color = PinkLight,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nombre del firmante",
                                style = MaterialTheme.typography.titleMedium,
                                color = White,
                            )
                            if (state.incluirCargo) {
                                Text(
                                    text = "Empresa",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PinkLight,
                                )
                            }
                            if (state.incluirEmpresa) {
                                Text(
                                    text = "Cargo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PinkLight,
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.QrCode,
                                contentDescription = null,
                                tint = PinkLight,
                                modifier = Modifier.size(48.dp),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Icon(
                                Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = GreenActive,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Action Buttons ───────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ACJSecondaryButton(
                    text = "Descartar",
                    onClick = { viewModel.descartar() },
                    modifier = Modifier.weight(1f),
                )
                ACJPrimaryButton(
                    text = "Guardar",
                    onClick = { viewModel.guardar() },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Componente interno para mostrar una opción de selección con checkbox y etiqueta.
 *
 * @param label Texto descriptivo de la opción.
 * @param checked Estado actual de selección.
 * @param onCheckedChange Callback para notificar cambios en la selección.
 */
@Composable
private fun CheckboxItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Magenta,
                checkmarkColor = White,
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = DeepPurple,
        )
    }
}
