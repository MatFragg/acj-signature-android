package acj.soluciones.acjsignature.presentation.tsl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.data.local.datastore.ConfigDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestionar la configuración de confianza (TSL).
 */
@HiltViewModel
class TSLViewModel @Inject constructor(
    private val configDataStore: ConfigDataStore
) : ViewModel() {

    private val _usarTslPrueba = MutableStateFlow(false)
    val usarTslPrueba = _usarTslPrueba.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado = _guardado.asStateFlow()

    init {
        viewModelScope.launch {
            configDataStore.configuracion.collect { config ->
                _usarTslPrueba.value = config.usarTslPrueba
            }
        }
    }

    fun onUsarTslPruebaChanged(value: Boolean) {
        _usarTslPrueba.value = value
    }

    fun guardar() {
        viewModelScope.launch {
            val current = configDataStore.configuracion.first()
            configDataStore.guardar(current.copy(usarTslPrueba = _usarTslPrueba.value))
            _guardado.value = true
        }
    }

    fun clearGuardado() {
        _guardado.value = false
    }
}
