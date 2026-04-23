package acj.soluciones.acjsignature.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

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

object EstadoDocumento {
    const val PENDIENTE = "PENDIENTE"
    const val FIRMADO = "FIRMADO"
    const val VALIDADO = "VALIDADO"
    const val ERROR = "ERROR"
}
