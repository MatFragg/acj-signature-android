package acj.soluciones.acjsignature.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO para respuesta de consulta de DNI desde RENIEC.
 * Contiene los datos personales extraídos del DNI.
 */
data class DniResponse(
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("first_last_name")
    val firstLastName: String?,
    @SerializedName("second_last_name")
    val secondLastName: String?,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("document_number")
    val documentNumber: String?
)
