package acj.soluciones.acjsignature.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de registro de usuario.
 */
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("dni")
    val dni: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String
)
