package acj.soluciones.acjsignature.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DocumentoEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ACJDatabase : RoomDatabase() {
    abstract fun documentoDao(): DocumentoDao
}
