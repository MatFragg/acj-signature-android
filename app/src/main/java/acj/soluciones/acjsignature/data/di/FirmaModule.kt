package acj.soluciones.acjsignature.data.di

import acj.soluciones.acjsignature.data.firma.DocumentoRepositoryImpl
import acj.soluciones.acjsignature.data.firma.FirmaRepositoryImpl
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirmaModule {

    @Binds
    @Singleton
    abstract fun bindFirmaRepository(impl: FirmaRepositoryImpl): FirmaRepository

    @Binds
    @Singleton
    abstract fun bindDocumentoRepository(impl: DocumentoRepositoryImpl): DocumentoRepository
}