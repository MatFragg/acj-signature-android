package acj.soluciones.acjsignature.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de login.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)
