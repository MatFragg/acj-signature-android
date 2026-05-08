package acj.soluciones.acjsignature.domain.repository

import acj.soluciones.acjsignature.data.remote.dto.request.RegisterRequest
import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.data.remote.dto.response.DniResponse
import acj.soluciones.acjsignature.shared.domain.Result
import kotlinx.coroutines.flow.Flow

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
    suspend fun register(request: RegisterRequest): Result<AuthResponse>

    /**
     * Realiza una consulta pública de DNI en RENIEC.
     */
    suspend fun consultarDni(numero: String): Result<DniResponse>

    /**
     * Emite de forma reactiva si existe una sesión activa de usuario.
     */
    fun isLoggedIn(): Flow<Boolean>

    /**
     * Cierra la sesión del usuario actual limpiando los datos locales.
     */
    suspend fun logout()
}
