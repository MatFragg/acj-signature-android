package acj.soluciones.acjsignature.presentation.firma

/**
 * Estado que representa el proceso inicial de carga de un documento para firmar.
 *
 * @property fileName Nombre del archivo seleccionado.
 * @property fileSize Tamaño del archivo en bytes.
 * @property fileUri URI local del documento.
 * @property documentoId Identificador único asignado tras procesar el documento.
 * @property currentStep Paso actual del flujo de firma (1: Selección, 2: Posicionamiento).
 * @property isLoading Indica si se está realizando una operación de procesamiento.
 * @property error Mensaje descriptivo en caso de fallo durante la carga.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class FirmaState(
    val fileName: String? = null,
    val fileSize: Long = 0L,
    val fileUri: String? = null,
    val documentoId: Long? = null,
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
)