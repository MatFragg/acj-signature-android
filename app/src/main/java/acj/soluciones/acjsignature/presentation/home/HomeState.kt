package acj.soluciones.acjsignature.presentation.home

import acj.soluciones.acjsignature.domain.model.DocumentoFirmado

data class HomeState(
    val documentosRecientes: List<DocumentoFirmado> = emptyList(),
    val isLoading: Boolean = false,
)
