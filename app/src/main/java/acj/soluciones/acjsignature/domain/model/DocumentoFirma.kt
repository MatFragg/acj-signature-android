package acj.soluciones.acjsignature.domain.model

import java.io.File

/**
 * Representa el documento que se va a firmar.
 */
data class DocumentoFirma(
    val archivo: File,
    val rutaDestino: String,
    val aliasCertificado: String,
    val motivo: String = "",
    val lugar: String = "",
    val sufijo: String = "_firmado",
    val nivel: NivelFirma = NivelFirma.B,
    val firmaVisible: FirmaVisible? = null,
    val tsl: ConfiguracionTsl = ConfiguracionTsl(),
    val tsa: ConfiguracionTsa = ConfiguracionTsa(),
)

enum class NivelFirma { B, T }

data class FirmaVisible(
    val pagina: Int = 1,
    val x: Int = 50,
    val y: Int = 50,
    val ancho: Int = 200,
    val alto: Int = 60,
    val fontSize: Int = 8,
    val titulo: String = "",
    val tipo: TipoFirmaVisible = TipoFirmaVisible.TEXTO,
    val rutaImagen: String? = null,
    val apariencia: String = "",
    val incluirCargo: Boolean = false,
    val incluirEmpresa: Boolean = false,
)

enum class TipoFirmaVisible { TEXTO, LOGO_TEXTO }

data class ConfiguracionTsl(
    val verificar: Boolean = true,
    val url: String = "",
)

data class ConfiguracionTsa(
    val verificar: Boolean = false,
    val url: String = "",
)