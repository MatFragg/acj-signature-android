package acj.soluciones.acjsignature.domain.model

import java.util.Date

data class Certificado(
    val alias: String,
    val nombreComun: String,
    val organizacion: String,
    val emisor: String,
    val validoDesde: Date,
    val validoHasta: Date,
    val tieneNoRepudio: Boolean,
    val serial: String,
) {
    val estaVigente: Boolean
        get() {
            val ahora = Date()
            return ahora.after(validoDesde) && ahora.before(validoHasta)
        }

    val etiquetaDisplay: String
        get() = if (nombreComun.isNotBlank()) nombreComun else alias
}
