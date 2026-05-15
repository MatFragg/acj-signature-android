package acj.soluciones.acjsignature.domain.repository

import acj.soluciones.acjsignature.data.remote.dto.request.RegisterRequest
import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.data.remote.dto.response.DniResponse
import acj.soluciones.acjsignature.shared.domain.Result
import kotlinx.coroutines.flow.Flow

import acj.soluciones.acjsignature.data.remote.dto.response.OtpResponse

/**
 * Interfaz que define las operaciones del dominio para autenticación y consulta de DNI.
 */
interface AuthRepository {

    /**
     * Inicia sesión con el nombre de usuario y contraseña especificados.
     */
    suspend fun login(email: String, password: String): Result<AuthResponse>

    /**
     * Registra un nuevo usuario en el sistema.
     */
    suspend fun register(request: RegisterRequest): Result<OtpResponse>

    /**
     * Verifica el código OTP para un correo.
     */
    suspend fun verifyOtp(email: String, otp: String): Result<AuthResponse>

    /**
     * Reenvía el código OTP a un correo.
     */
    suspend fun resendOtp(email: String): Result<OtpResponse>

    /**
     * Realiza una consulta pública de DNI en RENIEC.
     */
    suspend fun consultarDni(numero: String): Result<DniResponse>

    /**
     * Solicita la recuperación de contraseña.
     */
    suspend fun forgotPassword(email: String): Result<OtpResponse>

    /**
     * Restablece la contraseña con OTP.
     */
    suspend fun resetPassword(request: acj.soluciones.acjsignature.data.remote.dto.request.ResetPasswordRequest): Result<String>

    /**
     * Cambia la contraseña estando autenticado.
     */
    suspend fun changePassword(request: acj.soluciones.acjsignature.data.remote.dto.request.ChangePasswordRequest): Result<String>

    /**
     * Emite de forma reactiva si existe una sesión activa de usuario.
     */
    fun isLoggedIn(): Flow<Boolean>

    /**
     * Cierra la sesión del usuario actual limpiando los datos locales.
     */
    suspend fun logout()
}
