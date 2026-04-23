package acj.soluciones.acjsignature.domain.model

data class EstadisticasDocumentos(
    val total: Int = 0,
    val fallidos: Int = 0,
    val firmados: Int = 0,
) {
    val porcentajeSeguridad: Int
        get() = if (total > 0) ((firmados.toDouble() / total) * 100).toInt() else 100
}
