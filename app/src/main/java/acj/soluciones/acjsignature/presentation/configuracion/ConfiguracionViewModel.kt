package acj.soluciones.acjsignature.presentation.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.data.local.datastore.ConfigDataStore
import acj.soluciones.acjsignature.data.local.datastore.ConfiguracionFirma
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val configDataStore: ConfigDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(ConfiguracionState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            configDataStore.configuracion.collect { config ->
                _state.update {
                    it.copy(
                        logoUri = config.logoUri,
                        incluirEmpresa = config.incluirEmpresa,
                        incluirCargo = config.incluirCargo,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onLogoChanged(uri: String?) {
        _state.update { it.copy(logoUri = uri) }
    }

    fun onIncluirEmpresaChanged(value: Boolean) {
        _state.update { it.copy(incluirEmpresa = value) }
    }

    fun onIncluirCargoChanged(value: Boolean) {
        _state.update { it.copy(incluirCargo = value) }
    }

    fun guardar() {
        viewModelScope.launch {
            val s = _state.value
            configDataStore.guardar(
                ConfiguracionFirma(
                    logoUri = s.logoUri,
                    incluirEmpresa = s.incluirEmpresa,
                    incluirCargo = s.incluirCargo,
                )
            )
            _state.update { it.copy(guardado = true) }
        }
    }

    fun descartar() {
        viewModelScope.launch {
            configDataStore.configuracion.collect { config ->
                _state.update {
                    it.copy(
                        logoUri = config.logoUri,
                        incluirEmpresa = config.incluirEmpresa,
                        incluirCargo = config.incluirCargo,
                    )
                }
            }
        }
    }

    fun clearGuardado() {
        _state.update { it.copy(guardado = false) }
    }
}
