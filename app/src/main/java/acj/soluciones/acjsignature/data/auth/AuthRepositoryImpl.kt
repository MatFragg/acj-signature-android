package acj.soluciones.acjsignature.data.auth

import acj.soluciones.acjsignature.data.local.datastore.SessionManager
import acj.soluciones.acjsignature.data.remote.api.AuthApiService
import acj.soluciones.acjsignature.data.remote.dto.request.DniRequest
import acj.soluciones.acjsignature.data.remote.dto.request.LoginRequest
import acj.soluciones.acjsignature.data.remote.dto.request.RegisterRequest
import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.data.remote.dto.response.DniResponse
import acj.soluciones.acjsignature.data.remote.dto.response.ErrorResponse
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del repositorio de autenticación y consulta pública.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            if (response.success && response.data != null) {
                sessionManager.saveSession(response.data)
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "Credenciales o respuesta incorrecta del servidor.")
            }
        } catch (e: Exception) {
            Result.Error(parseError(e))
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(request)
            if (response.success && response.data != null) {
                sessionManager.saveSession(response.data)
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "Error al registrar el usuario.")
            }
        } catch (e: Exception) {
            Result.Error(parseError(e))
        }
    }

    override suspend fun consultarDni(numero: String): Result<DniResponse> {
        return try {
            val request = DniRequest(numero)
            val response = apiService.consultarDni(request)
            if (response.success && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "No se encontró información para el DNI ingresado.")
            }
        } catch (e: Exception) {
            Result.Error(parseError(e))
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return sessionManager.isLoggedIn
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    /**
     * Parsea excepciones y errores de red para extraer un mensaje de error amigable.
     */
    private fun parseError(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> "Error de conexión. Verifica tu conexión a Internet."
            is HttpException -> {
                try {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                    
                    // Priorizar el listado de validación, sino el mensaje general
                    if (!errorResponse.validationErrors.isNullOrEmpty()) {
                        errorResponse.validationErrors.values.joinToString("\n")
                    } else {
                        errorResponse.message ?: errorResponse.error ?: "Error del servidor (${throwable.code()})."
                    }
                } catch (e: Exception) {
                    "Error del servidor (${throwable.code()})."
                }
            }
            else -> throwable.localizedMessage ?: "Ocurrió un error inesperado."
        }
    }
}
