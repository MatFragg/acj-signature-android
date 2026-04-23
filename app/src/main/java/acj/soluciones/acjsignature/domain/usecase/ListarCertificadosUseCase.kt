package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import javax.inject.Inject

class ListarCertificadosUseCase @Inject constructor(
    private val repository: FirmaRepository,
) {
    suspend operator fun invoke(): Result<List<Certificado>> =
        repository.listarCertificados()
}