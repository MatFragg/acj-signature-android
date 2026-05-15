package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import javax.inject.Inject

/**
 * Caso de uso para verificar el código OTP de un usuario y autenticarlo.
 */
class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String): Result<AuthResponse> {
        if (email.isBlank() || otp.isBlank()) {
            return Result.Error("El correo y el código OTP son requeridos.")
        }
        if (otp.length != 6 || !otp.all { it.isDigit() }) {
            return Result.Error("El código OTP debe tener 6 dígitos.")
        }
        return repository.verifyOtp(email, otp)
    }
}
