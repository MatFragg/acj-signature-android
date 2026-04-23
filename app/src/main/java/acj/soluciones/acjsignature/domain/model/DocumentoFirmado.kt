package acj.soluciones.acjsignature.domain.model

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
