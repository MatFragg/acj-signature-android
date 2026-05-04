package acj.soluciones.acjsignature.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla "documentos" en la base de datos local.
 * Almacena los metadatos y el estado de los archivos procesados por la aplicación.
 *
 * @property id Clave primaria autogenerada.
 * @property nombre Nombre descriptivo del archivo.
 * @property rutaOriginal Ubicación del archivo original en el almacenamiento.
 * @property rutaFirmado Ubicación del archivo resultante tras la firma.
 * @property tamano Tamaño del archivo en bytes.
 * @property tipoDocumento Formato del documento (por defecto PDF).
 * @property estado Situación actual del documento (PENDIENTE, FIRMADO, etc.).
 * @property aliasCertificado Referencia al certificado del KeyStore utilizado.
 * @property fechaCreacion Fecha de registro inicial.
 * @property fechaFirma Fecha en que se completó la firma.
 * @property posicionFirmaX Coordenada horizontal del sello visual.
 * @property posicionFirmaY Coordenada vertical del sello visual.
 * @property paginaFirma Página del PDF donde se estampó la firma.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Entity(tableName = "documentos")
data class DocumentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val rutaOriginal: String,
    val rutaFirmado: String? = null,
    val tamano: Long,
    val tipoDocumento: String = "PDF",
    val estado: String = EstadoDocumento.PENDIENTE,
    val aliasCertificado: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaFirma: Long? = null,
    val posicionFirmaX: Int? = null,
    val posicionFirmaY: Int? = null,
    val paginaFirma: Int? = null,
)

/**
 * Constantes que definen los posibles estados de un documento en el flujo de trabajo.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
object EstadoDocumento {
    const val PENDIENTE = "PENDIENTE"
    const val FIRMADO = "FIRMADO"
    const val VALIDADO = "VALIDADO"
    const val ERROR = "ERROR"
}
