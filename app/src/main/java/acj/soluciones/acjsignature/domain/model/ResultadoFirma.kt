package acj.soluciones.acjsignature.domain.model

import java.io.File

/**
 * Encapsula la información resultante de una operación de firma digital exitosa.
 *
 * @property archivoFirmado Instancia del archivo físico resultante de la firma.
 * @property nombreArchivo Nombre descriptivo del archivo generado.
 * @property aliasCertificado Alias del certificado utilizado para generar la firma.
 * @property timestampFirma Momento exacto en que se completó la firma.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ResultadoFirma(
    val archivoFirmado: File,
    val nombreArchivo: String,
    val aliasCertificado: String,
    val timestampFirma: Long = System.currentTimeMillis(),
)