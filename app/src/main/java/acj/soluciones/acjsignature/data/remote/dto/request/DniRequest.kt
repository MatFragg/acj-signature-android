package acj.soluciones.acjsignature.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de consulta de DNI.
 * Contiene el número de DNI a consultar en la API RENIEC.
 */
data class DniRequest(
    @SerializedName("numero")
    val numero: String
)
