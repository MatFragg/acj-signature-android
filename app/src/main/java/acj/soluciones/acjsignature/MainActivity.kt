package acj.soluciones.acjsignature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import acj.soluciones.acjsignature.presentation.navigation.AppNavGraph
import acj.soluciones.acjsignature.shared.ui.components.ACJStoragePermissionGate
import acj.soluciones.acjsignature.shared.ui.theme.ACJSignatureTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal de la aplicación que sirve como punto de entrada único.
 * Configura el entorno de Compose, el tema global y la navegación principal,
 * protegida por la validación de permisos de almacenamiento.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Inicializa la actividad y establece el contenido de la interfaz de usuario.
     */
    @OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Solución temporal para el crash de IndicationNodeFactory con Material 2/3 mixtos
        androidx.compose.foundation.ComposeFoundationFlags.isNonComposedClickableEnabled = true
        enableEdgeToEdge()
        setContent {
            ACJSignatureTheme {
                ACJStoragePermissionGate {
                    AppNavGraph()
                }
            }
        }
    }
}