package acj.soluciones.acjsignature.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

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
        fun createRoute(docId: Long) = "posicionar_firma/$docId"
    }

    data object DocumentosFirmados : Screen(
        route = "documentos_firmados",
        title = "Documentos",
        selectedIcon = Icons.Filled.Description,
        unselectedIcon = Icons.Outlined.Description,
    )

    data object Certificados : Screen(route = "certificados", title = "Certificados")

    data object Configuracion : Screen(
        route = "configuracion",
        title = "Ajustes",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    )

    companion object {
        val bottomNavItems = listOf(Home, DocumentosFirmados, Configuracion)
    }
}