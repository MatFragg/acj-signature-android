package acj.soluciones.acjsignature.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO para respuesta genérica de la API.
 * Envuelve todas las respuestas exitosas en un formato consistente.
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: T?,
    @SerializedName("timestamp")
    val timestamp: String?
)
