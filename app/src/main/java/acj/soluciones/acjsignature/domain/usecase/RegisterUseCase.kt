package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.data.remote.dto.request.RegisterRequest
import acj.soluciones.acjsignature.data.remote.dto.response.OtpResponse
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import android.util.Patterns
import javax.inject.Inject

/**
 * Caso de uso para gestionar el registro de nuevos usuarios.
 * Realiza una validación exhaustiva de los campos del formulario.
 */
class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Result<OtpResponse> {
        if (request.email.isBlank()) {
            return Result.Error("El correo electrónico es requerido.")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            return Result.Error("El formato del correo electrónico es inválido.")
        }
        
        if (request.password.isBlank()) {
            return Result.Error("La contraseña es requerida.")
        }
        if (request.password.length < 6) {
            return Result.Error("La contraseña debe tener al menos 6 caracteres.")
        }
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$"
        if (!request.password.matches(passwordPattern.toRegex())) {
            return Result.Error("La contraseña debe contener al menos una mayúscula, una minúscula y un dígito.")
        }
        
        if (request.dni.isBlank()) {
            return Result.Error("El DNI es requerido.")
        }
        if (!request.dni.matches("^\\d{8}$".toRegex())) {
            return Result.Error("El DNI debe contener exactamente 8 dígitos.")
        }
        
        if (request.firstName.isBlank()) {
            return Result.Error("El nombre es requerido.")
        }
        if (request.firstName.length < 2 || request.firstName.length > 100) {
            return Result.Error("El nombre debe tener entre 2 y 100 caracteres.")
        }
        
        if (request.lastName.isBlank()) {
            return Result.Error("El apellido es requerido.")
        }
        if (request.lastName.length < 2 || request.lastName.length > 100) {
            return Result.Error("El apellido debe tener entre 2 y 100 caracteres.")
        }
        
        return repository.register(request)
    }
}
