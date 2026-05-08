package acj.soluciones.acjsignature.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO para respuesta de error estandarizada.
 */
data class ErrorResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("timestamp")
    val timestamp: String?,
    @SerializedName("path")
    val path: String?,
    @SerializedName("validationErrors")
    val validationErrors: Map<String, String>?
)
