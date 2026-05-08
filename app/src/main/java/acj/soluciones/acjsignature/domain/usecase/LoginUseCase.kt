package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import javax.inject.Inject

/**
 * Caso de uso para gestionar el inicio de sesión.
 * Realiza validaciones básicas locales en las credenciales antes de llamar al repositorio.
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        if (email.isBlank()) {
            return Result.Error("El correo electrónico es requerido.")
        }
        if (email.length < 3 || email.length > 100) {
            return Result.Error("El correo electrónico debe tener entre 3 y 100 caracteres.")
        }
        if (password.isBlank()) {
            return Result.Error("La contraseña es requerida.")
        }
        if (password.length < 6) {
            return Result.Error("La contraseña debe tener al menos 6 caracteres.")
        }
        
        return repository.login(email, password)
    }
}
