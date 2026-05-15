package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.data.remote.dto.response.OtpResponse
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import javax.inject.Inject

/**
 * Caso de uso para solicitar un nuevo código OTP.
 */
class ResendOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<OtpResponse> {
        if (email.isBlank()) {
            return Result.Error("El correo electrónico es requerido.")
        }
        return repository.resendOtp(email)
    }
}
