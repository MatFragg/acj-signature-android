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

/**
 * Implementación del repositorio de documentos.
 * Coordina el acceso a la base de datos local (Room) y al sistema de archivos físico.
 *
 * @property dao Acceso a datos para la persistencia en base de datos.
 * @property fileStorage Gestor de archivos para la manipulación física de documentos.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Singleton
class DocumentoRepositoryImpl @Inject constructor(
    private val dao: DocumentoDao,
    private val fileStorage: FileStorageManager,
) : DocumentoRepository {

    /**
     * Recupera todos los documentos registrados transformándolos a modelos de dominio.
     *
     * @return Flow con la lista de documentos.
     */
    override fun getDocumentos(): Flow<List<DocumentoFirmado>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    /**
     * Recupera los documentos más recientes hasta un límite especificado.
     *
     * @param limit Cantidad máxima de documentos.
     * @return Flow con los documentos recientes.
     */
    override fun getRecientes(limit: Int): Flow<List<DocumentoFirmado>> =
        dao.getRecientes(limit).map { list -> list.map { it.toDomain() } }

    /**
     * Busca documentos por nombre en la base de datos.
     *
     * @param query Texto de búsqueda.
     * @return Flow con los resultados coincidentes.
     */
    override fun buscar(query: String): Flow<List<DocumentoFirmado>> =
        dao.buscar(query).map { list -> list.map { it.toDomain() } }

    /**
     * Calcula y emite las estadísticas globales de los documentos.
     *
     * @return Flow con el resumen estadístico.
     */
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

    /**
     * Guarda físicamente un PDF y registra sus metadatos en la base de datos.
     *
     * @param uri URI de origen.
     * @param fileName Nombre del archivo.
     * @param fileSize Tamaño original.
     * @return ID del nuevo registro.
     */
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

    /**
     * Actualiza el estado y la ubicación del archivo firmado en la base de datos.
     *
     * @param id Identificador del registro.
     * @param estado Nuevo estado.
     * @param rutaFirmado Ruta local del PDF generado.
     */
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

    /**
     * Actualiza las coordenadas y página de la firma visual.
     *
     * @param id Identificador del registro.
     * @param x Eje horizontal.
     * @param y Eje vertical.
     * @param pagina Número de página.
     */
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

    /**
     * Recupera un documento por ID.
     *
     * @param id Identificador único.
     * @return Documento de dominio o null.
     */
    override suspend fun getById(id: Long): DocumentoFirmado? =
        withContext(Dispatchers.IO) {
            dao.getById(id)?.toDomain()
        }

    /**
     * Elimina el registro y los archivos físicos asociados.
     *
     * @param id Identificador del registro a borrar.
     */
    override suspend fun eliminar(id: Long) {
        withContext(Dispatchers.IO) {
            dao.getById(id)?.let { entity ->
                fileStorage.deleteFile(entity.rutaOriginal)
                entity.rutaFirmado?.let { fileStorage.deleteFile(it) }
                dao.deleteById(id)
            }
        }
    }

    /**
     * Función de extensión para mapear una entidad de base de datos a un modelo de dominio.
     *
     * @return Objeto DocumentoFirmado.
     */
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
