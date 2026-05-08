package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.data.remote.dto.response.DniResponse
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import javax.inject.Inject

/**
 * Caso de uso para realizar la consulta de DNI (RENIEC).
 */
class ConsultarDniUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(dni: String): Result<DniResponse> {
        if (dni.isBlank()) {
            return Result.Error("El número de DNI es requerido.")
        }
        if (!dni.matches("^\\d{8}$".toRegex())) {
            return Result.Error("El DNI debe contener exactamente 8 dígitos.")
        }
        return repository.consultarDni(dni)
    }
}
