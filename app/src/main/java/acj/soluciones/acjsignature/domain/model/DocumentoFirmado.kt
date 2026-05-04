package acj.soluciones.acjsignature.domain.model

/**
 * Representa un documento que ha pasado por el proceso de firma (exitosa o fallida) y su metadatos asociados.
 *
 * @property id Identificador único del registro en la base de datos local.
 * @property nombre Nombre del archivo.
 * @property rutaOriginal Ruta del archivo antes de ser procesado.
 * @property rutaFirmado Ruta del archivo resultante tras la firma exitosa.
 * @property tamano Tamaño del archivo en bytes.
 * @property tipoDocumento Extensión o tipo MIME del documento.
 * @property estado Estado actual del procesamiento (FIRMADO, ERROR, PENDIENTE).
 * @property aliasCertificado Referencia al certificado utilizado para la firma.
 * @property fechaCreacion Timestamp de cuando se registró el documento.
 * @property fechaFirma Timestamp de cuando se completó la operación de firma.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class DocumentoFirmado(
    val id: Long,
    val nombre: String,
    val rutaOriginal: String,
    val rutaFirmado: String?,
    val tamano: Long,
    val tipoDocumento: String,
    val estado: String,
    val aliasCertificado: String?,
    val fechaCreacion: Long,
    val fechaFirma: Long?,
)
