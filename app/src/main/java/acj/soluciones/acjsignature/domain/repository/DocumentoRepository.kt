package acj.soluciones.acjsignature.domain.repository

import acj.soluciones.acjsignature.domain.model.DocumentoFirmado
import acj.soluciones.acjsignature.domain.model.EstadisticasDocumentos
import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface DocumentoRepository {
    fun getDocumentos(): Flow<List<DocumentoFirmado>>
    fun getRecientes(limit: Int = 5): Flow<List<DocumentoFirmado>>
    fun buscar(query: String): Flow<List<DocumentoFirmado>>
    fun getEstadisticas(): Flow<EstadisticasDocumentos>
    suspend fun guardarDocumento(uri: Uri, fileName: String, fileSize: Long): Long
    suspend fun actualizarEstado(id: Long, estado: String, rutaFirmado: String? = null)
    suspend fun actualizarPosicionFirma(id: Long, x: Int, y: Int, pagina: Int)
    suspend fun getById(id: Long): DocumentoFirmado?
    suspend fun eliminar(id: Long)
}
