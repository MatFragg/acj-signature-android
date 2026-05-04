package acj.soluciones.acjsignature.presentation.validacion

import acj.soluciones.acjsignature.domain.model.DocumentoFirmado
import acj.soluciones.acjsignature.domain.model.EstadisticasDocumentos

/**
 * Estado que representa la información de la pantalla de historial y validación de documentos.
 *
 * @property documentos Lista de documentos (firmados o pendientes) que cumplen con el filtro de búsqueda.
 * @property estadisticas Resumen numérico del estado del repositorio de documentos.
 * @property searchQuery Texto ingresado por el usuario para filtrar el historial.
 * @property isLoading Indica si se está consultando la base de datos o procesando un archivo.
 * @property documentoSeleccionado Documento elegido para mostrar detalles o acciones.
 * @property showDetallesSheet Controla la visibilidad del panel de detalles técnicos.
 * @property showEliminarDialog Controla la visibilidad del diálogo de confirmación de borrado.
 * @property documentoAEliminar Referencia al documento que se pretende borrar.
 * @property mensaje Notificación temporal para el usuario (éxito o error).
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ValidacionState(
    val documentos: List<DocumentoFirmado> = emptyList(),
    val estadisticas: EstadisticasDocumentos = EstadisticasDocumentos(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val documentoSeleccionado: DocumentoFirmado? = null,
    val showDetallesSheet: Boolean = false,
    val showEliminarDialog: Boolean = false,
    val documentoAEliminar: DocumentoFirmado? = null,
    val mensaje: String? = null,
)