package acj.soluciones.acjsignature.domain.model

import java.io.File

/**
 * Representa la solicitud completa para el proceso de firma de un documento.
 *
 * @property archivo El archivo físico que se desea firmar.
 * @property rutaDestino Ubicación donde se guardará el archivo firmado.
 * @property aliasCertificado Alias del certificado a utilizar desde el KeyStore.
 * @property motivo Razón o motivo de la firma.
 * @property lugar Ubicación geográfica donde se realiza la firma.
 * @property sufijo Texto opcional para añadir al nombre del archivo de salida.
 * @property nivel Nivel de firma (Básica o con Sello de Tiempo).
 * @property firmaVisible Configuración de la apariencia visual de la firma en el PDF.
 * @property tsl Parámetros para la validación contra una Trusted Service List.
 * @property tsa Parámetros para la comunicación con una Authority Time Stamp.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
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

/**
 * Define el nivel de seguridad y cumplimiento de la firma digital.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
enum class NivelFirma { B, T }

/**
 * Define los parámetros de visualización del sello de firma en el documento PDF.
 *
 * @property pagina Número de página donde se colocará el sello.
 * @property x Coordenada horizontal.
 * @property y Coordenada vertical.
 * @property ancho Ancho del sello.
 * @property alto Alto del sello.
 * @property fontSize Tamaño de la fuente del texto informativo.
 * @property titulo Título opcional sobre el sello.
 * @property tipo Estilo visual (Solo texto o Texto con Logo).
 * @property rutaImagen Ruta local de la imagen para el logo si aplica.
 * @property apariencia Estilo CSS o descriptivo de la apariencia.
 * @property incluirCargo Indica si se debe mostrar el cargo del firmante.
 * @property incluirEmpresa Indica si se debe mostrar la organización del firmante.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
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

/**
 * Define las variantes de representación visual de la firma.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
enum class TipoFirmaVisible { TEXTO, LOGO_TEXTO }

/**
 * Configuración para la validación de certificados mediante TSL.
 *
 * @property verificar Indica si se debe realizar la validación por TSL.
 * @property url Dirección del servicio TSL.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ConfiguracionTsl(
    val verificar: Boolean = false,
    val url: String = "",
)

/**
 * Configuración para el uso de Sello de Tiempo (TSA).
 *
 * @property verificar Indica si se debe solicitar sello de tiempo.
 * @property url Dirección del servidor TSA.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ConfiguracionTsa(
    val verificar: Boolean = false,
    val url: String = "",
)