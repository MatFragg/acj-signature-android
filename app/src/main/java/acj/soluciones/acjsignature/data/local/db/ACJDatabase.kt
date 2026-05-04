package acj.soluciones.acjsignature.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos principal de la aplicación construida con Room.
 * Gestiona la persistencia de documentos y el historial de validaciones.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Database(
    entities = [DocumentoEntity::class, ValidacionEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class ACJDatabase : RoomDatabase() {
    /**
     * Provee el objeto de acceso a datos para la entidad Documento.
     */
    abstract fun documentoDao(): DocumentoDao

    /**
     * Provee el objeto de acceso a datos para la entidad Validacion.
     */
    abstract fun validacionDao(): ValidacionDao
}
