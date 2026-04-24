package acj.soluciones.acjsignature.presentation.firma

import acj.soluciones.acjsignature.data.local.datastore.ConfigDataStore
import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.FirmaVisible
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.domain.usecase.FirmarDocumentoUseCase
import acj.soluciones.acjsignature.shared.domain.Result
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class PosicionarFirmaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentoRepository: DocumentoRepository,
    private val firmaRepository: FirmaRepository,
    private val configDataStore: ConfigDataStore,
    private val firmarDocumentoUseCase: FirmarDocumentoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PosicionarFirmaState())
    val state = _state.asStateFlow()

    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var currentPage: PdfRenderer.Page? = null

    fun loadDocument(docId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, documentoId = docId) }

            // 1. Cargar lista de certificados para saber los nombres y RUC
            val certsResult = firmaRepository.listarCertificados()
            val certificados = if (certsResult is Result.Success) certsResult.data else emptyList()
            _state.update { it.copy(certificadosDisponibles = certificados) }

            // 2. Cargar Configuración de Vista Previa y calcular altura considerando el nombre + RUC
            val config = configDataStore.configuracion.first()
            val certInicial = certificados.firstOrNull()
            val nombreParaPrevio = certInicial?.etiquetaDisplay?.ifBlank { config.nombreUsuario } ?: config.nombreUsuario.ifBlank { "FIRMA DIGITAL" }
            val rucParaPrevio = certInicial?.serial ?: ""

            _state.update {
                it.copy(
                    logoUri = config.logoUri,
                    firmaNombre = config.nombreUsuario.ifBlank { "FIRMA DIGITAL" },
                    firmaCargo = config.cargoUsuario,
                    firmaEmpresa = config.empresaUsuario,
                    includeCargo = config.incluirCargo,
                    includeEmpresa = config.incluirEmpresa,
                    includeTelefono = config.incluirTelefono,
                    sigIdealWidth = 210,
                    sigIdealHeight = calculateIdealHeight(
                        nombre = nombreParaPrevio, 
                        ruc = rucParaPrevio, 
                        empresa = if (config.incluirEmpresa) config.empresaUsuario else null, 
                        cargo = if (config.incluirCargo) config.cargoUsuario else null, 
                        fontSize = 8
                    )
                )
            }

            // 3. Cargar Documento Original para previsualizacion
            val documento = documentoRepository.getById(docId)
            if (documento != null) {
                val file = File(documento.rutaOriginal)
                if (file.exists()) {
                    initPdfRenderer(file)
                    _state.update { 
                        it.copy(
                            archivoPdf = file,
                            isLoading = false,
                            error = if (certificados.isEmpty()) "Debe importar un certificado en el menú Certificados." else null
                        ) 
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "El archivo PDF ya no existe en la ruta.") }
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Documento no encontrado.") }
            }
        }
    }

    private suspend fun initPdfRenderer(file: File) {
        withContext(Dispatchers.IO) {
            try {
                fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfRenderer = PdfRenderer(fileDescriptor!!)
                
                val total = pdfRenderer?.pageCount ?: 1
                _state.update { it.copy(totalPages = total) }
                
                renderPage(0)
            } catch (e: Exception) {
                _state.update { it.copy(error = "No se pudo cargar el visor de PDF: ${e.message}") }
            }
        }
    }

    private suspend fun renderPage(pageIndex: Int) {
        withContext(Dispatchers.IO) {
            currentPage?.close()
            pdfRenderer?.let { renderer ->
                if (pageIndex < 0 || pageIndex >= renderer.pageCount) return@let
                val page = renderer.openPage(pageIndex)
                currentPage = page

                // Render at a high density but avoiding overly huge bitmaps
                val density = 2f // roughly translates to higher DPI zoom for better reading text
                val bitmapWidth = (page.width * density).toInt()
                val bitmapHeight = (page.height * density).toInt()
                
                val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
                // Usamos fondo blanco
                bitmap.eraseColor(android.graphics.Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                _state.update { 
                    it.copy(
                        currentPage = pageIndex + 1,
                        currentPageBitmap = bitmap,
                        pdfPageWidth = page.width,
                        pdfPageHeight = page.height
                    ) 
                }
            }
        }
    }

    fun nextPage() {
        if (_state.value.currentPage < _state.value.totalPages) {
            viewModelScope.launch { renderPage(_state.value.currentPage) }
        }
    }

    fun previousPage() {
        if (_state.value.currentPage > 1) {
            viewModelScope.launch { renderPage(_state.value.currentPage - 2) }
        }
    }

    fun jumpToPage(page: Int) {
        val totalPages = _state.value.totalPages
        if (page in 1..totalPages) {
            viewModelScope.launch { renderPage(page - 1) }
        }
    }

    // ─── PIN-gated signing flow ─────────────────────────────────────────────

    /**
     * Parámetros temporales de firma. Se guardan mientras el usuario
     * ingresa su PIN en el diálogo de verificación.
     */
    private data class FirmaParams(
        val certificado: Certificado,
        val pdfX: Int,
        val pdfY: Int,
        val anchoVisible: Int,
        val altoVisible: Int,
    )

    private var pendingFirmaParams: FirmaParams? = null

    /**
     * Punto de entrada público: en vez de firmar directamente,
     * abre el diálogo de PIN para verificar la identidad del usuario.
     */
    fun solicitarFirma(certificado: Certificado, pdfX: Int, pdfY: Int, anchoVisible: Int, altoVisible: Int) {
        pendingFirmaParams = FirmaParams(certificado, pdfX, pdfY, anchoVisible, altoVisible)
        _state.update { it.copy(showPinDialog = true, pinError = null) }
    }

    /**
     * Callback del diálogo de PIN. Verifica el PIN contra el almacenado
     * y procede con la firma si es correcto.
     */
    fun onPinVerificado(pin: String) {
        val params = pendingFirmaParams ?: return

        viewModelScope.launch {
            val valid = firmaRepository.verificarPinCertificado(params.certificado.alias, pin)
            if (valid) {
                _state.update { it.copy(showPinDialog = false, pinError = null) }
                pendingFirmaParams = null
                firmarDocumento(params.certificado, params.pdfX, params.pdfY, params.anchoVisible, params.altoVisible)
            } else {
                _state.update { it.copy(pinError = "PIN incorrecto") }
            }
        }
    }

    fun onCancelPin() {
        pendingFirmaParams = null
        _state.update { it.copy(showPinDialog = false, pinError = null) }
    }

    private fun firmarDocumento(certificado: Certificado, pdfX: Int, pdfY: Int, anchoVisible: Int, altoVisible: Int) {
        val currentState = _state.value
        val archivo = currentState.archivoPdf ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSigning = true, error = null) }
            
            // Determinar la página donde va la firma
            val paginaFirma = currentState.currentPage

            // Determinar la ruta segura en el sandbox interno del app
            val outputDir = File(context.filesDir, "firmados")
            if (!outputDir.exists()) outputDir.mkdirs()

            val documentoFirma = DocumentoFirma(
                archivo = archivo,
                rutaDestino = outputDir.absolutePath,  
                aliasCertificado = certificado.alias,
                motivo = "Firma desde ACJ Signature App",
                lugar = "Ecuador",
                firmaVisible = FirmaVisible(
                    pagina = paginaFirma,
                    x = pdfX,
                    y = pdfY,
                    ancho = anchoVisible,
                    alto = altoVisible,
                    titulo = estadoNombre(currentState),
                    rutaImagen = currentState.logoUri,
                    apariencia = if (currentState.logoUri != null) "I" else "S",
                    incluirCargo = currentState.includeCargo,
                    incluirEmpresa = currentState.includeEmpresa
                )
            )

            when (val result = firmarDocumentoUseCase(documentoFirma)) {
                is Result.Success -> {
                    // Update the Documento repository with the signed file path
                    documentoRepository.actualizarEstado(
                        id = currentState.documentoId,
                        estado = "FIRMADO",
                        rutaFirmado = result.data.archivoFirmado.absolutePath
                    )
                    _state.update { it.copy(isSigning = false, firmaExitosa = true) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isSigning = false, error = result.message) }
                }
                is Result.Loading -> {
                    // Loading is handled via isSigning boolean on execute
                }
            }
        }
    }

    private fun estadoNombre(state: PosicionarFirmaState): String {
        return state.firmaNombre.ifBlank { "FIRMA DIGITAL" }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        currentPage?.close()
        pdfRenderer?.close()
        fileDescriptor?.close()
    }
    private fun calculateIdealHeight(nombre: String, ruc: String, empresa: String?, cargo: String?, fontSize: Int): Int {
        var nombreFinal = nombre
        if (ruc.isNotEmpty() && !nombreFinal.contains(ruc)) {
            nombreFinal += " RUC:$ruc"
        }
        
        // Helper para contar líneas con el nuevo límite de 30 (balanceado)
        fun countLines(text: String?): Int {
            if (text.isNullOrEmpty()) return 0
            return if (text.length <= 30) 1 else (text.length + 29) / 30
        }
        
        val nLines = if (nombreFinal.isEmpty()) 1 else countLines(nombreFinal)
        val eLines = countLines(empresa)
        val cLines = countLines(cargo)
        
        val lineH = fontSize + 2
        // Título + Nombre + Empresa + Cargo + Fecha + Pie
        return (lineH * (1 + nLines + eLines + cLines + 1 + 1) + 5)
    }
}

