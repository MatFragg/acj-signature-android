package acj.soluciones.acjsignature.presentation.home

import acj.soluciones.acjsignature.domain.model.DocumentoFirmado

/**
 * Estado que representa los datos necesarios para renderizar la pantalla de inicio.
 *
 * @property documentosRecientes Lista de los últimos documentos gestionados por el usuario.
 * @property isLoading Indica si se están cargando los datos iniciales.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class HomeState(
    val documentosRecientes: List<DocumentoFirmado> = emptyList(),
    val isLoading: Boolean = false,
)

