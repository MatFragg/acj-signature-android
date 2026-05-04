package acj.soluciones.acjsignature.presentation.validarpdf

import android.net.Uri
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion

/**
 * Estado que representa el proceso de validación externa de un documento firmado.
 *
 * @property fileName Nombre del archivo seleccionado para validar.
 * @property fileSize Tamaño del archivo en bytes.
 * @property fileUri URI del documento a validar.
 * @property isValidating Indica si el proceso criptográfico de validación está en ejecución.
 * @property resultado Detalles del análisis de integridad y firmas del documento.
 * @property error Mensaje descriptivo en caso de fallos técnicos durante la validación.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ValidarPdfState(
    val fileName: String? = null,
    val fileSize: Long = 0L,
    val fileUri: Uri? = null,
    val isValidating: Boolean = false,
    val resultado: ResultadoValidacion? = null,
    val error: String? = null,
)

