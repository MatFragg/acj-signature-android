package acj.soluciones.acjsignature.domain.model

import acj.soluciones.acjsignature.domain.model.ResultadoValidacionFirma
import java.util.Date

data class ResultadoValidacion(
    val esValido: Boolean,
    val firmas: List<ResultadoValidacionFirma>,
    val mensajeGeneral: String = "",
)

data class FirmaValidada(
    val firmante: String,
    val fechaFirma: Date?,
    val estadoCertificado: EstadoCertificado,
    val ancladaEnTsl: Boolean,
    val integridadOk: Boolean,
    val nivel: String,
    val detalle: String = "",
) {
    val esValida: Boolean
        get() = integridadOk
                && ancladaEnTsl
                && estadoCertificado != EstadoCertificado.REVOCADO
}

enum class EstadoCertificado {
    VIGENTE,
    EXPIRADO,
    REVOCADO,
    DESCONOCIDO,
}
