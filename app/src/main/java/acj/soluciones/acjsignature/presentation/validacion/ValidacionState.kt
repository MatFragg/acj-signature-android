package acj.soluciones.acjsignature.presentation.validacion

import acj.soluciones.acjsignature.domain.model.DocumentoFirmado
import acj.soluciones.acjsignature.domain.model.EstadisticasDocumentos

data class ValidacionState(
    val documentos: List<DocumentoFirmado> = emptyList(),
    val estadisticas: EstadisticasDocumentos = EstadisticasDocumentos(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
)