package acj.soluciones.acjsignature.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa los diferentes destinos de navegación dentro de la aplicación.
 * Define rutas, títulos y recursos visuales para la integración con Jetpack Navigation y UI.
 *
 * @property route Identificador único de la ruta de navegación.
 * @property title Título legible para mostrar en la UI.
 * @property selectedIcon Icono mostrado cuando el destino está seleccionado.
 * @property unselectedIcon Icono mostrado en estado inactivo.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
sealed class Screen(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
) {

    data object Home : Screen(
        route = "home",
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    data object SubirPdf : Screen(route = "subir_pdf", title = "Firmar Documento")

    data object PosicionarFirma : Screen(route = "posicionar_firma/{docId}", title = "Posicionar Firma") {
        /**
         * Genera la ruta dinámica para posicionar firma incluyendo el ID del documento.
         * @param docId Identificador único del documento en la base de datos.
         */
        fun createRoute(docId: Long) = "posicionar_firma/$docId"

    }

    data object DocumentosFirmados : Screen(
        route = "documentos_firmados",
        title = "Documentos",
        selectedIcon = Icons.Filled.Description,
        unselectedIcon = Icons.Outlined.Description,
    )

    data object Certificados : Screen(route = "certificados", title = "Certificados")

    data object Ajustes : Screen(
        route = "ajustes",
        title = "Ajustes",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    )

    data object ConfiguracionFirma : Screen(route = "configuracion_firma", title = "Configuración de Firma")

    data object TslConfig : Screen(route = "tsl_config", title = "Configuración TSL")

    data object LogsAuditoria : Screen(route = "logs_auditoria", title = "Logs de Auditoría")

    data object AcercaDe : Screen(route = "acerca_de", title = "Acerca de ACJSignature")

    data object ValidarPdf : Screen(route = "validar_pdf", title = "Validar PDF")
    
    data object ResultadoValidacion : Screen(route = "resultado_validacion", title = "Resultado Validación")
    
    data object DetalleFirmante : Screen(route = "detalle_firmante/{index}", title = "Detalle Firmante") {
        /**
         * Genera la ruta dinámica para el detalle de un firmante específico.
         * @param index Índice de la firma dentro de la lista de validación.
         */
        fun createRoute(index: Int) = "detalle_firmante/$index"

    }

    data object Login : Screen(route = "login", title = "Iniciar Sesión")
    data object Register : Screen(route = "register", title = "Crear Cuenta")
    
    data object VerifyOtp : Screen(route = "verify_otp/{email}", title = "Verificar OTP") {
        fun createRoute(email: String) = "verify_otp/$email"
    }

    data object ForgotPassword : Screen(route = "forgot_password", title = "Recuperar Contraseña")
    
    data object ResetPassword : Screen(route = "reset_password/{email}", title = "Restablecer Contraseña") {
        fun createRoute(email: String) = "reset_password/$email"
    }

    data object ChangePassword : Screen(route = "change_password", title = "Cambiar Contraseña")

    companion object {
        val bottomNavItems = listOf(Home, DocumentosFirmados, Ajustes)
    }
}