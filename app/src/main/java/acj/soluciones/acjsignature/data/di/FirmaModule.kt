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

/**
 * Módulo de Hilt encargado de vincular las interfaces de los repositorios con sus implementaciones concretas.
 * Se instala en el SingletonComponent para asegurar que los repositorios tengan el mismo ciclo de vida que la aplicación.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FirmaModule {

    /**
     * Vincula la interfaz FirmaRepository con su implementación FirmaRepositoryImpl.
     *
     * @param impl Implementación concreta de la lógica de firma.
     * @return Instancia vinculada de FirmaRepository.
     */
    @Binds
    @Singleton
    abstract fun bindFirmaRepository(impl: FirmaRepositoryImpl): FirmaRepository

    /**
     * Vincula la interfaz DocumentoRepository con su implementación DocumentoRepositoryImpl.
     *
     * @param impl Implementación concreta de la gestión de documentos.
     * @return Instancia vinculada de DocumentoRepository.
     */
    @Binds
    @Singleton
    abstract fun bindDocumentoRepository(impl: DocumentoRepositoryImpl): DocumentoRepository
}