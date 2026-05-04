package acj.soluciones.acjsignature.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un informe de validación en la tabla "validaciones".
 * Almacena el resultado técnico simplificado y el detalle extendido en formato JSON.
 *
 * @property id Clave primaria autogenerada.
 * @property nombreDocumento Nombre del archivo PDF inspeccionado.
 * @property fechaValidacion Timestamp de la operación de validación.
 * @property esValido Resultado global de la validación.
 * @property totalFirmas Cantidad de firmas detectadas en el documento.
 * @property firmasValidas Cantidad de firmas que pasaron todas las pruebas técnicas.
 * @property resultadoJson Detalle estructurado de cada firma en formato JSON.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Entity(tableName = "validaciones")
data class ValidacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreDocumento: String,
    val fechaValidacion: Long = System.currentTimeMillis(),
    val esValido: Boolean,
    val totalFirmas: Int,
    val firmasValidas: Int,
    val resultadoJson: String
)
