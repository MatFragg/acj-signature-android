package acj.soluciones.acjsignature.data.remote.api

import acj.soluciones.acjsignature.data.remote.dto.request.DniRequest
import acj.soluciones.acjsignature.data.remote.dto.request.LoginRequest
import acj.soluciones.acjsignature.data.remote.dto.request.RegisterRequest
import acj.soluciones.acjsignature.data.remote.dto.response.ApiResponse
import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.data.remote.dto.response.DniResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz de Retrofit que define las operaciones remotas de autenticación y consulta pública.
 */
interface AuthApiService {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<AuthResponse>

    @POST("api/v1/public/dni/consultar")
    suspend fun consultarDni(
        @Body request: DniRequest
    ): ApiResponse<DniResponse>
}
