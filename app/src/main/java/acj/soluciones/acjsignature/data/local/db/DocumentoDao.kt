package acj.soluciones.acjsignature.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentoDao {

    @Query("SELECT * FROM documentos ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<DocumentoEntity>>

    @Query("SELECT * FROM documentos WHERE id = :id")
    suspend fun getById(id: Long): DocumentoEntity?

    @Query("SELECT * FROM documentos WHERE estado = :estado ORDER BY fechaCreacion DESC")
    fun getByEstado(estado: String): Flow<List<DocumentoEntity>>

    @Query(
        "SELECT * FROM documentos WHERE nombre LIKE '%' || :query || '%' ORDER BY fechaCreacion DESC"
    )
    fun buscar(query: String): Flow<List<DocumentoEntity>>

    @Query("SELECT COUNT(*) FROM documentos")
    fun countTotal(): Flow<Int>

    @Query("SELECT COUNT(*) FROM documentos WHERE estado = :estado")
    fun countByEstado(estado: String): Flow<Int>

    @Query("SELECT * FROM documentos ORDER BY fechaCreacion DESC LIMIT :limit")
    fun getRecientes(limit: Int = 5): Flow<List<DocumentoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(documento: DocumentoEntity): Long

    @Update
    suspend fun update(documento: DocumentoEntity)

    @Delete
    suspend fun delete(documento: DocumentoEntity)

    @Query("DELETE FROM documentos WHERE id = :id")
    suspend fun deleteById(id: Long)
}
