package acj.soluciones.acjsignature.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión de la tabla de documentos.
 * Define las consultas SQL para insertar, actualizar, eliminar y consultar documentos.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Dao
interface DocumentoDao {

    /**
     * Recupera todos los documentos ordenados por fecha de creación descendente.
     */
    @Query("SELECT * FROM documentos ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<DocumentoEntity>>

    /**
     * Recupera un documento específico por su identificador único.
     */
    @Query("SELECT * FROM documentos WHERE id = :id")
    suspend fun getById(id: Long): DocumentoEntity?

    /**
     * Filtra los documentos por su estado actual.
     */
    @Query("SELECT * FROM documentos WHERE estado = :estado ORDER BY fechaCreacion DESC")
    fun getByEstado(estado: String): Flow<List<DocumentoEntity>>

    /**
     * Busca documentos cuyo nombre contenga la cadena especificada.
     */
    @Query(
        "SELECT * FROM documentos WHERE nombre LIKE '%' || :query || '%' ORDER BY fechaCreacion DESC"
    )
    fun buscar(query: String): Flow<List<DocumentoEntity>>

    /**
     * Cuenta la cantidad total de documentos registrados.
     */
    @Query("SELECT COUNT(*) FROM documentos")
    fun countTotal(): Flow<Int>

    /**
     * Cuenta los documentos que se encuentran en un estado específico.
     */
    @Query("SELECT COUNT(*) FROM documentos WHERE estado = :estado")
    fun countByEstado(estado: String): Flow<Int>

    /**
     * Obtiene los últimos documentos registrados según un límite.
     */
    @Query("SELECT * FROM documentos ORDER BY fechaCreacion DESC LIMIT :limit")
    fun getRecientes(limit: Int = 5): Flow<List<DocumentoEntity>>

    /**
     * Inserta un nuevo registro de documento o reemplaza uno existente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(documento: DocumentoEntity): Long

    /**
     * Actualiza la información de un documento existente.
     */
    @Update
    suspend fun update(documento: DocumentoEntity)

    /**
     * Elimina una entidad de documento de la base de datos.
     */
    @Delete
    suspend fun delete(documento: DocumentoEntity)

    /**
     * Elimina un documento identificado por su ID.
     */
    @Query("DELETE FROM documentos WHERE id = :id")
    suspend fun deleteById(id: Long)
}
