package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import javax.inject.Inject

/**
 * Caso de Uso destinado a recuperar la colección de certificados aptos para firma digital.
 *
 * @property repository Repositorio de firma digital que provee acceso al KeyStore.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
class ListarCertificadosUseCase @Inject constructor(
    private val repository: FirmaRepository,
) {
    /**
     * Recupera todos los certificados de firma disponibles.
     *
     * @return Result con la lista de certificados encontrados.
     */
    suspend operator fun invoke(): Result<List<Certificado>> =
        repository.listarCertificados()
}