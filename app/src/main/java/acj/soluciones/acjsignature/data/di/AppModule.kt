package acj.soluciones.acjsignature.data.di

import android.content.Context
import androidx.room.Room
import acj.soluciones.acjsignature.data.local.db.ACJDatabase
import acj.soluciones.acjsignature.data.local.db.DocumentoDao
import acj.soluciones.acjsignature.data.local.datastore.ConfigDataStore
import acj.soluciones.acjsignature.data.local.storage.FileStorageManager
import acj.soluciones.acjsignature.shared.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ACJDatabase =
        Room.databaseBuilder(
            context,
            ACJDatabase::class.java,
            Constants.DB_NAME,
        ).build()

    @Provides
    fun provideDocumentoDao(database: ACJDatabase): DocumentoDao =
        database.documentoDao()

    @Provides
    @Singleton
    fun provideConfigDataStore(@ApplicationContext context: Context): ConfigDataStore =
        ConfigDataStore(context)

    @Provides
    @Singleton
    fun provideFileStorageManager(@ApplicationContext context: Context): FileStorageManager =
        FileStorageManager(context)
}