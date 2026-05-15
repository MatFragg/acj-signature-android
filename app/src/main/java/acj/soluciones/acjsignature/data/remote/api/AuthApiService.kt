package acj.soluciones.acjsignature.data.remote.api

import acj.soluciones.acjsignature.data.remote.dto.request.DniRequest
import acj.soluciones.acjsignature.data.remote.dto.request.LoginRequest
import acj.soluciones.acjsignature.data.remote.dto.request.RegisterRequest
import acj.soluciones.acjsignature.data.remote.dto.response.ApiResponse
import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.data.remote.dto.response.DniResponse
import retrofit2.http.Body
import retrofit2.http.POST

import acj.soluciones.acjsignature.data.remote.dto.request.VerifyOtpRequest
import acj.soluciones.acjsignature.data.remote.dto.request.ResendOtpRequest
import acj.soluciones.acjsignature.data.remote.dto.response.OtpResponse

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
    ): ApiResponse<OtpResponse>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): ApiResponse<AuthResponse>

    @POST("api/v1/auth/resend-otp")
    suspend fun resendOtp(
        @Body request: ResendOtpRequest
    ): ApiResponse<OtpResponse>

    @POST("api/v1/public/dni/consultar")
    suspend fun consultarDni(
        @Body request: DniRequest
    ): ApiResponse<DniResponse>

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: acj.soluciones.acjsignature.data.remote.dto.request.ForgotPasswordRequest
    ): ApiResponse<OtpResponse>

    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(
        @Body request: acj.soluciones.acjsignature.data.remote.dto.request.ResetPasswordRequest
    ): ApiResponse<String>

    @POST("api/v1/auth/change-password")
    suspend fun changePassword(
        @Body request: acj.soluciones.acjsignature.data.remote.dto.request.ChangePasswordRequest
    ): ApiResponse<String>
}
