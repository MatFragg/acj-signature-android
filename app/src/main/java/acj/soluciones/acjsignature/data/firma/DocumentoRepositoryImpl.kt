package acj.soluciones.acjsignature.data.firma

import acj.soluciones.acjsignature.data.local.db.DocumentoDao
import acj.soluciones.acjsignature.data.local.db.DocumentoEntity
import acj.soluciones.acjsignature.data.local.db.EstadoDocumento
import acj.soluciones.acjsignature.data.local.storage.FileStorageManager
import acj.soluciones.acjsignature.domain.model.DocumentoFirmado
import acj.soluciones.acjsignature.domain.model.EstadisticasDocumentos
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentoRepositoryImpl @Inject constructor(
    private val dao: DocumentoDao,
    private val fileStorage: FileStorageManager,
) : DocumentoRepository {

    override fun getDocumentos(): Flow<List<DocumentoFirmado>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getRecientes(limit: Int): Flow<List<DocumentoFirmado>> =
        dao.getRecientes(limit).map { list -> list.map { it.toDomain() } }

    override fun buscar(query: String): Flow<List<DocumentoFirmado>> =
        dao.buscar(query).map { list -> list.map { it.toDomain() } }

    override fun getEstadisticas(): Flow<EstadisticasDocumentos> =
        combine(
            dao.countTotal(),
            dao.countByEstado(EstadoDocumento.ERROR),
            dao.countByEstado(EstadoDocumento.FIRMADO),
        ) { total, fallidos, firmados ->
            EstadisticasDocumentos(
                total = total,
                fallidos = fallidos,
                firmados = firmados,
            )
        }

    override suspend fun guardarDocumento(uri: Uri, fileName: String, fileSize: Long): Long =
        withContext(Dispatchers.IO) {
            val file = fileStorage.saveOriginalPdf(uri, fileName)
            val entity = DocumentoEntity(
                nombre = fileName,
                rutaOriginal = file.absolutePath,
                tamano = fileSize,
            )
            dao.insert(entity)
        }

    override suspend fun actualizarEstado(id: Long, estado: String, rutaFirmado: String?) {
        withContext(Dispatchers.IO) {
            dao.getById(id)?.let { entity ->
                dao.update(
                    entity.copy(
                        estado = estado,
                        rutaFirmado = rutaFirmado ?: entity.rutaFirmado,
                        fechaFirma = if (estado == EstadoDocumento.FIRMADO) System.currentTimeMillis() else entity.fechaFirma,
                    )
                )
            }
        }
    }

    override suspend fun actualizarPosicionFirma(id: Long, x: Int, y: Int, pagina: Int) {
        withContext(Dispatchers.IO) {
            dao.getById(id)?.let { entity ->
                dao.update(
                    entity.copy(
                        posicionFirmaX = x,
                        posicionFirmaY = y,
                        paginaFirma = pagina,
                    )
                )
            }
        }
    }

    override suspend fun getById(id: Long): DocumentoFirmado? =
        withContext(Dispatchers.IO) {
            dao.getById(id)?.toDomain()
        }

    override suspend fun eliminar(id: Long) {
        withContext(Dispatchers.IO) {
            dao.getById(id)?.let { entity ->
                fileStorage.deleteFile(entity.rutaOriginal)
                entity.rutaFirmado?.let { fileStorage.deleteFile(it) }
                dao.deleteById(id)
            }
        }
    }

    private fun DocumentoEntity.toDomain() = DocumentoFirmado(
        id = id,
        nombre = nombre,
        rutaOriginal = rutaOriginal,
        rutaFirmado = rutaFirmado,
        tamano = tamano,
        tipoDocumento = tipoDocumento,
        estado = estado,
        aliasCertificado = aliasCertificado,
        fechaCreacion = fechaCreacion,
        fechaFirma = fechaFirma,
    )
}
