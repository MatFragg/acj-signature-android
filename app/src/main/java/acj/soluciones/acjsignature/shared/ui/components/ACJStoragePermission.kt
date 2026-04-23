package acj.soluciones.acjsignature.shared.ui.components

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import acj.soluciones.acjsignature.shared.ui.theme.*

/**
 * Determina qué permisos de almacenamiento necesita solicitar la app
 * según la versión de Android del dispositivo.
 *
 * - Android 13+ (API 33): No necesita permisos de almacenamiento
 *   (SAF + app-specific storage son suficientes)
 * - Android 10-12 (API 29-32): READ_EXTERNAL_STORAGE solo si se
 *   necesita leer fuera de app-specific dirs
 * - Android 8-9 (API 26-28): READ_EXTERNAL_STORAGE + WRITE_EXTERNAL_STORAGE
 */
private fun getRequiredPermissions(): List<String> = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> emptyList() // API 33+
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> listOf(            // API 29-32
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    else -> listOf(                                                       // API 26-28
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
}

/**
 * Composable gate que verifica permisos de almacenamiento al arrancar.
 *
 * Si ya están concedidos o no se necesitan (API 33+), renderiza [content] directamente.
 * Si faltan, muestra una pantalla branded pidiendo al usuario que los conceda.
 *
 * @param content El composable principal de la app (ej. AppNavGraph)
 */
@Composable
fun ACJStoragePermissionGate(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val requiredPermissions = remember { getRequiredPermissions() }

    // Si no se necesitan permisos, renderizar contenido directamente
    if (requiredPermissions.isEmpty()) {
        content()
        return
    }

    var allGranted by remember {
        mutableStateOf(
            requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        allGranted = permissions.values.all { it }
        permissionDenied = !allGranted
    }

    if (allGranted) {
        content()
    } else {
        // ── Pantalla de Solicitud de Permiso ─────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PinkLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Magenta,
                        modifier = Modifier.size(40.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = DeepPurple)) { append("Permiso de ") }
                        withStyle(SpanStyle(color = Magenta)) { append("Almacenamiento") }
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ACJ Signature necesita acceder a tu almacenamiento para gestionar los documentos PDF que deseas firmar digitalmente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBody,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Reason cards
                PermissionReasonItem(
                    icon = Icons.Filled.Folder,
                    title = "Lectura de documentos",
                    description = "Acceder a los archivos PDF desde tu dispositivo",
                )
                Spacer(modifier = Modifier.height(12.dp))
                PermissionReasonItem(
                    icon = Icons.Filled.Lock,
                    title = "Almacenamiento seguro",
                    description = "Guardar documentos firmados de forma local y protegida",
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Warning if denied
                if (permissionDenied) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = YellowWarning),
                    ) {
                        Text(
                            text = "⚠️ Sin este permiso no podrás firmar documentos. Puedes habilitarlo desde Ajustes > Apps > ACJ Signature > Permisos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = YellowWarningText,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action button
                ACJPrimaryButton(
                    text = if (permissionDenied) "Reintentar" else "Conceder Permiso",
                    onClick = {
                        permissionLauncher.launch(requiredPermissions.toTypedArray())
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                if (!permissionDenied) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tus archivos nunca salen de tu dispositivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionReasonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(White),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Magenta,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = DeepPurple,
                    letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextBody,
                )
            }
        }
    }
}
