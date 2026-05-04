package acj.soluciones.acjsignature.domain.model

import java.util.Date

/**
 * Representa la información de un certificado digital extraído de un almacén de llaves.
 *
 * @property alias Identificador único del certificado en el KeyStore.
 * @property nombreComun Nombre del titular del certificado (CN).
 * @property organizacion Organización a la que pertenece el titular (O).
 * @property emisor Entidad que emitió el certificado.
 * @property validoDesde Fecha de inicio de validez del certificado.
 * @property validoHasta Fecha de fin de validez del certificado.
 * @property tieneNoRepudio Indica si el certificado posee el propósito de no repudio.
 * @property serial Número de serie único del certificado.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
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
    /**
     * Determina si el certificado se encuentra vigente en la fecha actual.
     *
     * @return true si la fecha actual está dentro del rango de validez.
     */
    val estaVigente: Boolean
        get() {
            val ahora = Date()
            return ahora.after(validoDesde) && ahora.before(validoHasta)
        }

    /**
     * Retorna una cadena de texto amigable para mostrar en la interfaz de usuario.
     *
     * @return el nombre común si no está vacío, de lo contrario retorna el alias.
     */
    val etiquetaDisplay: String
        get() = if (nombreComun.isNotBlank()) nombreComun else alias
}
