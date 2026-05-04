package acj.soluciones.acjsignature.presentation.configuracion

/**
 * Estado que representa las preferencias de configuración del usuario para la firma digital.
 *
 * @property logoUri URI de la imagen del logo seleccionado para la firma.
 * @property incluirEmpresa Indica si se debe incluir la razón social en la representación visual de la firma.
 * @property incluirCargo Indica si se debe incluir el cargo del firmante.
 * @property usarTslPrueba Define si se deben validar certificados contra una TSL de pruebas.
 * @property isLoading Estado de carga durante la persistencia de cambios.
 * @property guardado Flag que indica si los cambios fueron guardados exitosamente.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ConfiguracionState(
    val logoUri: String? = null,
    val incluirEmpresa: Boolean = true,
    val incluirCargo: Boolean = true,
    val usarTslPrueba: Boolean = false,
    val isLoading: Boolean = false,
    val guardado: Boolean = false,
)

