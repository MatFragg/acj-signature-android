package acj.soluciones.acjsignature.presentation.validacion

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.model.DocumentoFirmado
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel que gestiona el historial de documentos y las acciones de validación.
 * Permite buscar documentos, visualizar detalles, compartir archivos y gestionar
 * la exportación de documentos firmados a la carpeta de descargas pública.
 *
 * @property documentoRepository Repositorio para la consulta y gestión de documentos.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ValidacionViewModel @Inject constructor(
    private val documentoRepository: DocumentoRepository,
) : ViewModel() {


    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(ValidacionUiState())

    private val documentos = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) documentoRepository.getDocumentos()
        else documentoRepository.buscar(query)
    }

    val state = combine(
        documentos,
        documentoRepository.getEstadisticas(),
        _searchQuery,
        _uiState,
    ) { docs, stats, query, ui ->
        ValidacionState(
            documentos = docs,
            estadisticas = stats,
            searchQuery = query,
            documentoSeleccionado = ui.documentoSeleccionado,
            showDetallesSheet = ui.showDetallesSheet,
            showEliminarDialog = ui.showEliminarDialog,
            documentoAEliminar = ui.documentoAEliminar,
            mensaje = ui.mensaje,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ValidacionState(isLoading = true),
    )

    /**
     * Actualiza el filtro de búsqueda del historial.
     */
    fun onSearchQueryChanged(query: String) {

        _searchQuery.value = query
    }

    // ── Previsualizar / Compartir ────────────────────

    /**
     * Genera un URI seguro para compartir o visualizar el documento.
     * @param doc Documento a procesar.
     * @param context Contexto para el FileProvider.
     * @return URI del archivo o null si no existe.
     */
    fun getDocumentoUri(doc: DocumentoFirmado, context: Context): android.net.Uri? {

        val path = doc.rutaFirmado ?: doc.rutaOriginal
        val file = File(path)
        if (!file.exists()) return null
        return FileProvider.getUriForFile(
            context,
            "acj.soluciones.acjsignature.fileprovider",
            file,
        )
    }

    // ── Guardar en Descargas ─────────────────────────

    /**
     * Copia el documento seleccionado a la carpeta de Descargas del sistema.
     * @param doc Documento a exportar.
     * @param context Contexto para acceder al ContentResolver.
     */
    fun descargarADescargas(doc: DocumentoFirmado, context: Context) {

        viewModelScope.launch {
            try {
                val sourcePath = doc.rutaFirmado ?: doc.rutaOriginal
                val sourceFile = File(sourcePath)
                if (!sourceFile.exists()) {
                    _uiState.update { it.copy(mensaje = "No se encontró el archivo") }
                    return@launch
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ — usar MediaStore
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, doc.nombre)
                        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    uri?.let { destUri ->
                        resolver.openOutputStream(destUri)?.use { output ->
                            sourceFile.inputStream().use { input ->
                                input.copyTo(output)
                            }
                        }
                        _uiState.update { it.copy(mensaje = "Documento guardado en Descargas") }
                    } ?: run {
                        _uiState.update { it.copy(mensaje = "Error al guardar el archivo") }
                    }
                } else {
                    // Android 9 y menor — copiar directo
                    @Suppress("DEPRECATION")
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destFile = File(downloadsDir, doc.nombre)
                    sourceFile.copyTo(destFile, overwrite = true)
                    _uiState.update { it.copy(mensaje = "Documento guardado en Descargas") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = "Error al guardar: ${e.localizedMessage}") }
            }
        }
    }

    // ── Ver detalles ─────────────────────────────────

    /**
     * Prepara la visualización de detalles de un documento.
     */
    fun onVerDetalles(doc: DocumentoFirmado) {

        _uiState.update {
            it.copy(documentoSeleccionado = doc, showDetallesSheet = true)
        }
    }

    /**
     * Cierra el panel de detalles.
     */
    fun onDismissDetalles() {

        _uiState.update {
            it.copy(documentoSeleccionado = null, showDetallesSheet = false)
        }
    }

    // ── Eliminar ─────────────────────────────────────

    /**
     * Inicia el flujo de confirmación para eliminar un documento.
     */
    fun onSolicitarEliminar(doc: DocumentoFirmado) {

        _uiState.update {
            it.copy(documentoAEliminar = doc, showEliminarDialog = true)
        }
    }

    /**
     * Ejecuta la eliminación definitiva del documento en base de datos y archivos.
     */
    fun onConfirmarEliminar() {

        val doc = _uiState.value.documentoAEliminar ?: return
        viewModelScope.launch {
            documentoRepository.eliminar(doc.id)
            _uiState.update {
                it.copy(
                    documentoAEliminar = null,
                    showEliminarDialog = false,
                    mensaje = "Documento eliminado correctamente",
                )
            }
        }
    }

    fun onCancelarEliminar() {
        _uiState.update {
            it.copy(documentoAEliminar = null, showEliminarDialog = false)
        }
    }

    // ── Mensajes ─────────────────────────────────────

    /**
     * Limpia las notificaciones de UI.
     */
    fun clearMensaje() {

        _uiState.update { it.copy(mensaje = null) }
    }
}

/**
 * Estado interno para el manejo de estados de la UI que no persisten en base de datos.
 *
 * @property documentoSeleccionado Referencia para el BottomSheet de detalles.
 * @property showDetallesSheet Visibilidad del panel de detalles.
 * @property showEliminarDialog Visibilidad del diálogo de confirmación.
 * @property documentoAEliminar Referencia para el proceso de borrado.
 * @property mensaje Texto para mostrar en Snackbar.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
private data class ValidacionUiState(

    val documentoSeleccionado: DocumentoFirmado? = null,
    val showDetallesSheet: Boolean = false,
    val showEliminarDialog: Boolean = false,
    val documentoAEliminar: DocumentoFirmado? = null,
    val mensaje: String? = null,
)