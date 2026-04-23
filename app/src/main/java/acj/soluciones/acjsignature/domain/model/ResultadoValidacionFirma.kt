package acj.soluciones.acjsignature.domain.model

import java.util.Date

data class ResultadoValidacionFirma(
    val esValida: Boolean,
    val firmante: String,
    val motivo: String?,
    val location: String?,
    val fechaFirma: Date?,
    val mensajeError: String?
)