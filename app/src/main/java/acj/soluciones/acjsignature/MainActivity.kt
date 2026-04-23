package acj.soluciones.acjsignature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import acj.soluciones.acjsignature.presentation.navigation.AppNavGraph
import acj.soluciones.acjsignature.shared.ui.components.ACJStoragePermissionGate
import acj.soluciones.acjsignature.shared.ui.theme.ACJSignatureTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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