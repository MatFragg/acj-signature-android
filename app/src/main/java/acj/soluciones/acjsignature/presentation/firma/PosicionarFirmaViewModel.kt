package acj.soluciones.acjsignature.presentation.firma

import acj.soluciones.acjsignature.data.local.datastore.ConfigDataStore
import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.FirmaVisible
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.domain.usecase.FirmarDocumentoUseCase
import acj.soluciones.acjsignature.data.local.storage.FileStorageManager
import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.shared.util.AppLogger
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

/**
 * ViewModel que orquestador la interactividad de posicionamiento y el proceso criptográfico de firma.
 * Gestiona el renderizado de páginas PDF, la carga de identidades digitales y la ejecución
 * del caso de uso de firma digital con representación visual personalizada.
 *
 * @property context Contexto de la aplicación.
 * @property documentoRepository Repositorio para la gestión de documentos.
 * @property firmaRepository Repositorio para la gestión de identidades digitales.
 * @property configDataStore Almacén de configuraciones visuales.
 * @property firmarDocumentoUseCase Caso de uso para la firma criptográfica.
 * @property fileStorageManager Gestor de archivos y rutas.
 * @property logger Logger para registrar auditoría del proceso.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@HiltViewModel
class PosicionarFirmaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentoRepository: DocumentoRepository,
    private val firmaRepository: FirmaRepository,
    private val configDataStore: ConfigDataStore,
    private val firmarDocumentoUseCase: FirmarDocumentoUseCase,
    private val fileStorageManager: FileStorageManager,
    private val logger: AppLogger
) : ViewModel() {


    private val _state = MutableStateFlow(PosicionarFirmaState())
    val state = _state.asStateFlow()

    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var currentPage: PdfRenderer.Page? = null

    /**
     * Carga la información del documento y prepara el entorno de visualización.
     * @param docId ID del documento en la base de datos local.
     */
    fun loadDocument(docId: Long) {
        logger.info("Preparando posicionamiento de firma para documento ID: $docId")
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
                    logger.info("Documento ${file.name} cargado para previsualización.")
                    initPdfRenderer(file)
                    _state.update { 
                        it.copy(
                            archivoPdf = file,
                            isLoading = false,
                            error = if (certificados.isEmpty()) "Debe importar un certificado en el menú Certificados." else null
                        ) 
                    }
                } else {
                    logger.error("Archivo no encontrado físicamente: ${documento.rutaOriginal}")
                    _state.update { it.copy(isLoading = false, error = "El archivo PDF ya no existe en la ruta.") }
                }
            } else {
                logger.error("Documento con ID $docId no encontrado en DB.")
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
                logger.debug("PDF Renderer inicializado: ${file.name} ($total páginas)")
                _state.update { it.copy(totalPages = total) }
                
                renderPage(0)
            } catch (e: Exception) {
                logger.error("Error inicializando PdfRenderer", e)
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

    /**
     * Avanza a la siguiente página del documento si está disponible.
     */
    fun nextPage() {

        if (_state.value.currentPage < _state.value.totalPages) {
            viewModelScope.launch { renderPage(_state.value.currentPage) }
        }
    }

    /**
     * Retrocede a la página anterior del documento.
     */
    fun previousPage() {

        if (_state.value.currentPage > 1) {
            viewModelScope.launch { renderPage(_state.value.currentPage - 2) }
        }
    }

    /**
     * Salta directamente a una página específica.
     * @param page Número de página (1-indexed).
     */
    fun jumpToPage(page: Int) {

        val totalPages = _state.value.totalPages
        if (page in 1..totalPages) {
            viewModelScope.launch { renderPage(page - 1) }
        }
    }

    /**
     * Inicia el proceso de firma digital en las coordenadas especificadas.
     * @param certificado Identidad digital seleccionada.
     * @param pdfX Coordenada X en puntos PDF.
     * @param pdfY Coordenada Y en puntos PDF.
     * @param anchoVisible Ancho del sello de firma.
     * @param altoVisible Alto del sello de firma.
     */
    fun solicitarFirma(certificado: Certificado, pdfX: Int, pdfY: Int, anchoVisible: Int, altoVisible: Int, destinationUri: android.net.Uri) {
        logger.info("Solicitud de firma iniciada por el usuario en página ${_state.value.currentPage}")
        firmarDocumento(certificado, pdfX, pdfY, anchoVisible, altoVisible, destinationUri)
    }

    private fun firmarDocumento(certificado: Certificado, pdfX: Int, pdfY: Int, anchoVisible: Int, altoVisible: Int, destinationUri: android.net.Uri) {
        val currentState = _state.value
        val archivo = currentState.archivoPdf ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSigning = true, error = null) }
            
            // Determinar la página donde va la firma
            val paginaFirma = currentState.currentPage

            // Determinar la ruta segura usando el storage manager
            val outputDir = fileStorageManager.getSignedOutputDir()

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
                    rutaImagen = currentState.logoUri?.takeIf { it.isNotBlank() && java.io.File(it).exists() } 
                        ?: fileStorageManager.getDefaultLogoPath(),
                    apariencia = "I", // Siempre usar imagen (la personalizada o el escudo por defecto)
                    incluirCargo = currentState.includeCargo,
                    incluirEmpresa = currentState.includeEmpresa
                )
            )

            logger.info("Enviando parámetros a FirmarDocumentoUseCase para ${archivo.name}")
            when (val result = firmarDocumentoUseCase(documentoFirma)) {
                is Result.Success -> {
                    logger.info("Firma criptográfica completada con éxito para ${archivo.name}")
                    
                    try {
                        context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                            result.data.archivoFirmado.inputStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        
                        // Update the Documento repository with the signed file path
                        documentoRepository.actualizarEstado(
                            id = currentState.documentoId,
                            estado = "FIRMADO",
                            rutaFirmado = result.data.archivoFirmado.absolutePath
                        )
                        _state.update { it.copy(isSigning = false, firmaExitosa = true) }
                    } catch (e: Exception) {
                        logger.error("Error al exportar el archivo firmado a la URI seleccionada", e)
                        _state.update { it.copy(isSigning = false, error = "Error al guardar el archivo: ${e.message}") }
                    }
                }
                is Result.Error -> {
                    logger.error("Firma fallida para ${archivo.name}: ${result.message}", result.cause)
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

    /**
     * Limpia los errores registrados en el estado.
     */
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

