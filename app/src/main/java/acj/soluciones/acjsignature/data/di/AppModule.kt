package acj.soluciones.acjsignature.data.di

import android.content.Context
import androidx.room.Room
import acj.soluciones.acjsignature.data.local.db.ACJDatabase
import acj.soluciones.acjsignature.data.local.db.DocumentoDao
import acj.soluciones.acjsignature.data.local.db.ValidacionDao
import acj.soluciones.acjsignature.data.local.datastore.ConfigDataStore
import acj.soluciones.acjsignature.data.local.storage.FileStorageManager
import acj.soluciones.acjsignature.shared.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Inyección de Dependencias (Hilt) para la provisión de componentes globales.
 * Configura instancias únicas (Singletons) para la base de datos, DAOs, DataStore y gestores de archivos.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provee la instancia única de la base de datos Room del proyecto.
     *
     * @param context Contexto de la aplicación.
     * @return Instancia de ACJDatabase.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ACJDatabase =
        Room.databaseBuilder(
            context,
            ACJDatabase::class.java,
            Constants.DB_NAME,
        )
        .fallbackToDestructiveMigration()
        .build()

    /**
     * Provee el DAO para la gestión de documentos.
     *
     * @param database Instancia de la base de datos.
     * @return Objeto DocumentoDao.
     */
    @Provides
    fun provideDocumentoDao(database: ACJDatabase): DocumentoDao =
        database.documentoDao()

    /**
     * Provee el DAO para la gestión del historial de validaciones.
     *
     * @param database Instancia de la base de datos.
     * @return Objeto ValidacionDao.
     */
    @Provides
    fun provideValidacionDao(database: ACJDatabase): ValidacionDao =
        database.validacionDao()

    /**
     * Provee el gestor de preferencias persistentes (DataStore).
     *
     * @param context Contexto de la aplicación.
     * @return Instancia de ConfigDataStore.
     */
    @Provides
    @Singleton
    fun provideConfigDataStore(@ApplicationContext context: Context): ConfigDataStore =
        ConfigDataStore(context)

    /**
     * Provee el gestor del sistema de archivos local.
     *
     * @param context Contexto de la aplicación.
     * @return Instancia de FileStorageManager.
     */
    @Provides
    @Singleton
    fun provideFileStorageManager(@ApplicationContext context: Context): FileStorageManager =
        FileStorageManager(context)
}