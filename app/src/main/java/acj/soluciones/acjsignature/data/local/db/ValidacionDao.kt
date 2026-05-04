package acj.soluciones.acjsignature.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión del historial de validaciones.
 * Permite registrar y consultar los resultados de las inspecciones técnicas de firmas en PDFs.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Dao
interface ValidacionDao {

    /**
     * Recupera todos los registros de validación ordenados por fecha descendente.
     */
    @Query("SELECT * FROM validaciones ORDER BY fechaValidacion DESC")
    fun getAll(): Flow<List<ValidacionEntity>>

    /**
     * Registra un nuevo resultado de validación.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(validacion: ValidacionEntity): Long

    /**
     * Elimina un registro de validación específico.
     */
    @Delete
    suspend fun delete(validacion: ValidacionEntity)

    /**
     * Limpia por completo el historial de validaciones.
     */
    @Query("DELETE FROM validaciones")
    suspend fun deleteAll()
}
