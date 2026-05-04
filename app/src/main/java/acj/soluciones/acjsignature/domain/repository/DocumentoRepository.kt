package acj.soluciones.acjsignature.domain.repository

import acj.soluciones.acjsignature.domain.model.DocumentoFirmado
import acj.soluciones.acjsignature.domain.model.EstadisticasDocumentos
import android.net.Uri
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones de persistencia y consulta para los documentos gestionados por la aplicación.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
interface DocumentoRepository {
    /**
     * Obtiene un flujo de todos los documentos registrados.
     *
     * @return Flow con la lista completa de documentos firmados.
     */
    fun getDocumentos(): Flow<List<DocumentoFirmado>>

    /**
     * Obtiene los documentos registrados más recientemente.
     *
     * @param limit Cantidad máxima de registros a retornar.
     * @return Flow con la lista de documentos recientes.
     */
    fun getRecientes(limit: Int = 5): Flow<List<DocumentoFirmado>>

    /**
     * Realiza una búsqueda de documentos por coincidencia de nombre.
     *
     * @param query Cadena de texto a buscar.
     * @return Flow con los resultados de la búsqueda.
     */
    fun buscar(query: String): Flow<List<DocumentoFirmado>>

    /**
     * Obtiene las métricas generales de los documentos.
     *
     * @return Flow con el objeto de estadísticas.
     */
    fun getEstadisticas(): Flow<EstadisticasDocumentos>

    /**
     * Registra un nuevo documento en el repositorio.
     *
     * @param uri URI del archivo original.
     * @param fileName Nombre del archivo.
     * @param fileSize Tamaño en bytes.
     * @return ID del registro insertado.
     */
    suspend fun guardarDocumento(uri: Uri, fileName: String, fileSize: Long): Long

    /**
     * Actualiza el estado de procesamiento y la ruta del archivo firmado.
     *
     * @param id Identificador del registro.
     * @param estado Nuevo estado (FIRMADO, ERROR, etc.).
     * @param rutaFirmado Ruta local del PDF firmado.
     */
    suspend fun actualizarEstado(id: Long, estado: String, rutaFirmado: String? = null)

    /**
     * Actualiza las coordenadas visuales de la firma en el documento.
     *
     * @param id Identificador del registro.
     * @param x Coordenada horizontal.
     * @param y Coordenada vertical.
     * @param pagina Número de página.
     */
    suspend fun actualizarPosicionFirma(id: Long, x: Int, y: Int, pagina: Int)

    /**
     * Recupera un documento específico por su ID.
     *
     * @param id Identificador único.
     * @return Documento encontrado o null si no existe.
     */
    suspend fun getById(id: Long): DocumentoFirmado?

    /**
     * Elimina permanentemente un documento del repositorio local.
     *
     * @param id Identificador del registro a eliminar.
     */
    suspend fun eliminar(id: Long)
}
