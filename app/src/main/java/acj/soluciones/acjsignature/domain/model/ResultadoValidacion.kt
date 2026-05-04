package acj.soluciones.acjsignature.domain.model

import acj.soluciones.acjsignature.domain.model.ResultadoValidacionFirma
import java.util.Date

/**
 * Representa el informe general tras la validación de todas las firmas de un documento.
 *
 * @property esValido Indica si el documento en su conjunto se considera íntegro y válido.
 * @property firmas Lista detallada de los resultados de validación para cada firma encontrada.
 * @property mensajeGeneral Resumen textual del proceso de validación.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ResultadoValidacion(
    val esValido: Boolean,
    val firmas: List<ResultadoValidacionFirma>,
    val mensajeGeneral: String = "",
)

/**
 * Detalla el resultado de la validación técnica de una firma individual.
 *
 * @property firmante Nombre del titular que realizó la firma.
 * @property fechaFirma Fecha y hora registrada en la firma o sello de tiempo.
 * @property estadoCertificado Situación actual de validez del certificado del firmante.
 * @property ancladaEnTsl Indica si el certificado está presente en una lista de confianza (TSL).
 * @property integridadOk Indica si el documento no ha sido alterado tras la firma.
 * @property nivel Tipo de firma detectado (ej. AdES-B-B, AdES-B-T).
 * @property detalle Observaciones técnicas adicionales sobre la validación.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class FirmaValidada(
    val firmante: String,
    val fechaFirma: Date?,
    val estadoCertificado: EstadoCertificado,
    val ancladaEnTsl: Boolean,
    val integridadOk: Boolean,
    val nivel: String,
    val detalle: String = "",
) {
    /**
     * Evalúa si la firma cumple con todos los criterios mínimos de seguridad.
     *
     * @return true si la integridad es correcta, está en TSL y no está revocado.
     */
    val esValida: Boolean
        get() = integridadOk
                && ancladaEnTsl
                && estadoCertificado != EstadoCertificado.REVOCADO
}

/**
 * Posibles estados de validez de un certificado digital tras su verificación.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
enum class EstadoCertificado {
    VIGENTE,
    EXPIRADO,
    REVOCADO,
    DESCONOCIDO,
}
