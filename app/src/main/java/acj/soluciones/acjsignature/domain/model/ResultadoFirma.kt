package acj.soluciones.acjsignature.domain.model

import java.io.File

data class ResultadoFirma(
    val archivoFirmado: File,
    val nombreArchivo: String,
    val aliasCertificado: String,
    val timestampFirma: Long = System.currentTimeMillis(),
)