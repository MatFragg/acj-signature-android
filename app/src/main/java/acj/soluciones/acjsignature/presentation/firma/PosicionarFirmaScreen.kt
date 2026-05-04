package acj.soluciones.acjsignature.presentation.firma

import acj.soluciones.acjsignature.domain.model.Certificado
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.components.ACJProgressIndicator
import acj.soluciones.acjsignature.shared.ui.components.ACJPager
import acj.soluciones.acjsignature.shared.ui.theme.*
import kotlin.math.roundToInt

/**
 * Pantalla interactiva para definir la ubicación visual de la firma en un documento PDF.
 * Permite al usuario arrastrar un recuadro que representa la firma, navegar por las páginas
 * del documento y seleccionar la identidad digital con la que se realizará el estampado.
 *
 * @param docId Identificador del documento a firmar.
 * @param onBack Callback para cancelar y regresar.
 * @param onConfirm Callback invocado tras completar la firma exitosamente.
 * @param viewModel ViewModel que gestiona el renderizado y la lógica de firma.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosicionarFirmaScreen(
    docId: Long,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    viewModel: PosicionarFirmaViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Contenedor físico de la página PDF en la pantalla (medido sobre la Image)
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    // Margen de seguridad en puntos PDF (evita bleeding en los bordes del documento)
    val safeMarginPdfPts = 10f

    // Margen de seguridad convertido a píxeles de pantalla
    val safeMarginPx = if (state.pdfPageWidth > 0 && containerSize.width > 0) {
        (safeMarginPdfPts / state.pdfPageWidth) * containerSize.width
    } else 0f

    // El tamaño de la caja se calculará dinámicamente según la proporción del PDF
    val boxWidthPx = if (state.pdfPageWidth > 0 && containerSize.width > 0) {
        (state.sigIdealWidth.toFloat() / state.pdfPageWidth) * containerSize.width
    } else 0f
    val boxHeightPx = if (state.pdfPageHeight > 0 && containerSize.height > 0) {
        (state.sigIdealHeight.toFloat() / state.pdfPageHeight) * containerSize.height
    } else 0f

    val boxWidthDp = with(density) { boxWidthPx.toDp() }
    val boxHeightDp = with(density) { boxHeightPx.toDp() }

    // Límites máximos para que el sello no se salga del documento
    val minOffset = safeMarginPx
    val maxOffsetX = (containerSize.width - boxWidthPx - safeMarginPx).coerceAtLeast(safeMarginPx)
    val maxOffsetY = (containerSize.height - boxHeightPx - safeMarginPx).coerceAtLeast(safeMarginPx)

    // Posición del Box en Pixels relativos al contenedor
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Modal para el certificado si hay multiples
    var showCertDialog by remember { mutableStateOf(false) }

    LaunchedEffect(docId) {
        viewModel.loadDocument(docId)
    }

    LaunchedEffect(state.error, state.firmaExitosa) {
        state.error?.let { 
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
        if (state.firmaExitosa) {
            onConfirm() // navegar al éxito
        }
    }

    if (showCertDialog) {
        val certs = state.certificadosDisponibles
        CertificadoSelectionDialog(
            certificados = certs,
            onDismiss = { showCertDialog = false },
            onSelect = { cert: Certificado ->
                showCertDialog = false
                val margin = safeMarginPdfPts.toInt()
                val pdfX = ((offsetX / containerSize.width) * state.pdfPageWidth).roundToInt()
                    .coerceIn(margin, (state.pdfPageWidth - state.sigIdealWidth - margin).coerceAtLeast(margin))
                val pdfY = ((offsetY / containerSize.height) * state.pdfPageHeight).roundToInt()
                    .coerceIn(margin, (state.pdfPageHeight - state.sigIdealHeight - margin).coerceAtLeast(margin))
                
                viewModel.solicitarFirma(cert, pdfX, pdfY, state.sigIdealWidth, state.sigIdealHeight)
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posicionar Firma", style = MaterialTheme.typography.titleMedium, color = DeepPurple) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Volver", tint = DeepPurple) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = White, tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Confirm
                    ACJPrimaryButton(
                        text = "Siguiente Paso",
                        enabled = !state.isLoading && !state.isSigning && state.certificadosDisponibles.isNotEmpty(),
                        onClick = {
                            showCertDialog = true
                        },
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CardBg2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -- Progress Indicator --
            ACJProgressIndicator(
                currentStep = if (showCertDialog) 3 else 2,
                totalSteps = 3,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Magenta)
                } else if (state.currentPageBitmap != null) {
                    // PDF Viewer
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(White)
                    ) {
                        Image(
                            bitmap = state.currentPageBitmap!!.asImageBitmap(),
                            contentDescription = "Página PDF",
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    containerSize = coordinates.size
                                    // Posicionar centrado la primera vez, o re-ajustar si cambió de página
                                    if (offsetX == 0f && offsetY == 0f && containerSize.width > 0) {
                                        offsetX = maxOffsetX / 2f
                                        offsetY = maxOffsetY / 2f
                                    } else {
                                        offsetX = offsetX.coerceIn(minOffset, maxOffsetX)
                                        offsetY = offsetY.coerceIn(minOffset, maxOffsetY)
                                    }
                                },
                            contentScale = ContentScale.FillWidth
                        )

                        // Draggable Signature Box constraint to bounds
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .size(boxWidthDp, boxHeightDp)
                                .border(2.dp, Color.Red, RoundedCornerShape(4.dp))
                                .background(Color.Red.copy(alpha = 0.05f))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        // Constraints para no salir del PDF
                                        val newX = offsetX + dragAmount.x
                                        val newY = offsetY + dragAmount.y
                                        if (containerSize.width > 0 && containerSize.height > 0) {
                                            offsetX = newX.coerceIn(minOffset, maxOffsetX)
                                            offsetY = newY.coerceIn(minOffset, maxOffsetY)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(
                                    state.firmaNombre.ifBlank { "FIRMA DIGITAL" }.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    maxLines = 1,
                                )
                                Text("Arrastrar para mover", style = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.8), color = Color.Red.copy(0.7f))
                            }
                        }
                    }
                } else if (state.error != null) {
                    Text(state.error!!, color = Error, modifier = Modifier.padding(32.dp))
                }
                if (state.isSigning) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Magenta)
                    }
                }
            }

            // -- Elderly Friendly Pager --
            ACJPager(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                onPrevious = { viewModel.previousPage() },
                onNext = { viewModel.nextPage() },
                onJumpToPage = { viewModel.jumpToPage(it) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

/**
 * Diálogo modal para que el usuario elija entre los certificados disponibles.
 *
 * @param certificados Lista de identidades digitales filtradas.
 * @param onDismiss Callback para cerrar el diálogo sin seleccionar.
 * @param onSelect Callback invocado con el certificado elegido.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun CertificadoSelectionDialog(
    certificados: List<Certificado>,
    onDismiss: () -> Unit,
    onSelect: (Certificado) -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Certificado", style = MaterialTheme.typography.titleLarge) },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(certificados) { cert ->
                        Card(
                            onClick = { onSelect(cert) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardBg)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        cert.etiquetaDisplay,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = DeepPurple
                                    )
                                    Text(
                                        "Emisor: ${cert.emisor}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
