package acj.soluciones.acjsignature.domain.model

/**
 * Provee un resumen cuantitativo del estado de los documentos en el repositorio local.
 *
 * @property total Cantidad total de documentos registrados.
 * @property fallidos Cantidad de documentos cuyo proceso de firma resultó en error.
 * @property firmados Cantidad de documentos firmados exitosamente.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class EstadisticasDocumentos(
    val total: Int = 0,
    val fallidos: Int = 0,
    val firmados: Int = 0,
) {
    /**
     * Calcula el porcentaje de documentos que han sido firmados satisfactoriamente.
     *
     * @return Valor entero entre 0 y 100 que representa el índice de éxito de firma.
     */
    val porcentajeSeguridad: Int
        get() = if (total > 0) ((firmados.toDouble() / total) * 100).toInt() else 100
}
