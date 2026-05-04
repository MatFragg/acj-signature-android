package acj.soluciones.acjsignature.domain.model

import java.util.Date

/**
 * Contiene el detalle exhaustivo de una firma digital validada, incluyendo metadatos del certificado y sellos de tiempo.
 *
 * @property esValida Indica si la firma técnica y criptográficamente es correcta.
 * @property firmante Nombre o razón social del titular de la firma.
 * @property motivo Razón declarada de la firma.
 * @property location Ubicación declarada en el momento de la firma.
 * @property fechaFirma Fecha en que se realizó la firma.
 * @property mensajeError Descripción del fallo en caso de que la firma no sea válida.
 * @property subjectDn Distinguished Name completo del sujeto del certificado.
 * @property dniRuc Identificador único (DNI/RUC) extraído del certificado.
 * @property email Correo electrónico asociado al certificado.
 * @property cargo Puesto u ocupación del firmante según el certificado.
 * @property empresa Organización o empresa del firmante.
 * @property unidad Área o unidad organizacional del firmante.
 * @property emisorCertificado Entidad certificadora que emitió el certificado.
 * @property formatoCertificado Estándar del certificado (ej. X.509).
 * @property serialCertificado Número de serie único del certificado.
 * @property fechaEmision Fecha en que el certificado fue emitido.
 * @property fechaExpiracion Fecha de caducidad del certificado.
 * @property selloEmitidoPor Entidad TSA que emitió el sello de tiempo.
 * @property selloMarcaDeHora Tiempo exacto certificado por la TSA.
 * @property selloValidoHasta Fecha de validez máxima del sello de tiempo.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ResultadoValidacionFirma(
    val esValida: Boolean,
    val firmante: String,
    val motivo: String?,
    val location: String?,
    val fechaFirma: Date?,
    val mensajeError: String?,
    // ── Datos extendidos del firmante ────────
    val subjectDn: String? = null,
    val dniRuc: String? = null,
    val email: String? = null,
    val cargo: String? = null,
    val empresa: String? = null,
    val unidad: String? = null,
    // ── Datos del certificado ────────────────
    val emisorCertificado: String? = null,
    val formatoCertificado: String? = null,
    val serialCertificado: String? = null,
    val fechaEmision: Date? = null,
    val fechaExpiracion: Date? = null,
    // ── Datos del sello de tiempo ────────────
    val selloEmitidoPor: String? = null,
    val selloMarcaDeHora: Date? = null,
    val selloValidoHasta: Date? = null,
)