package acj.soluciones.acjsignature.presentation.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.data.local.datastore.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    /**
     * Nombre completo del usuario actualmente logueado.
     */
    val userFullName: StateFlow<String> = sessionManager.userFullName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Usuario"
        )

    /**
     * Cierra la sesión activa borrando el DataStore y notificando éxito.
     */
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            onSuccess()
        }
    }
}
