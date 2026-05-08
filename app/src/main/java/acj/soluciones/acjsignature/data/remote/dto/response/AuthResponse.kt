package acj.soluciones.acjsignature.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO para respuesta de autenticación (login y registro).
 * Contiene el JWT token, información del usuario autenticado y sus roles.
 */
data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("tokenType")
    val tokenType: String,
    @SerializedName("expiresIn")
    val expiresIn: Long,
    @SerializedName("user")
    val user: UserInfo
) {
    data class UserInfo(
        @SerializedName("id")
        val id: Long,

        @SerializedName("email")
        val email: String,
        @SerializedName("dni")
        val dni: String?,
        @SerializedName("firstName")
        val firstName: String?,
        @SerializedName("lastName")
        val lastName: String?,
        @SerializedName("fullName")
        val fullName: String?,
        @SerializedName("active")
        val active: Boolean?,
        @SerializedName("roles")
        val roles: Set<String>?
    )
}
