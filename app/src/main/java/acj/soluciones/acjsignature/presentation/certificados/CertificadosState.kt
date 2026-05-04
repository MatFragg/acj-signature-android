package acj.soluciones.acjsignature.presentation.certificados

import acj.soluciones.acjsignature.domain.model.Certificado

/**
 * Estado que representa la información de la gestión de certificados digitales.
 *
 * @property certificados Lista de identidades digitales disponibles en el dispositivo.
 * @property isLoading Indica si se está realizando una operación de carga o importación.
 * @property error Mensaje descriptivo en caso de fallo en la importación o lectura.
 * @property showPasswordDialog Controla la visibilidad del diálogo de ingreso de contraseña (PIN).
 * @property importSuccess Mensaje de confirmación tras una importación exitosa.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class CertificadosState(
    val certificados: List<Certificado> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showPasswordDialog: Boolean = false,
    val importSuccess: String? = null,
)


