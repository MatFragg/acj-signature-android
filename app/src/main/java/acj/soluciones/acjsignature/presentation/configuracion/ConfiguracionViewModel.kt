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

/**
 * ViewModel responsable de la lógica de negocio de la pantalla de configuración.
 * Gestiona la carga, edición y persistencia de las preferencias de firma del usuario.
 *
 * @property configDataStore Almacén de preferencias para la configuración de firma.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
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
                        usarTslPrueba = config.usarTslPrueba,
                        isLoading = false,
                    )
                }
            }
        }
    }

    /**
     * Actualiza el URI del logo en el estado temporal.
     * @param uri Ruta o identificador del recurso de imagen.
     */
    fun onLogoChanged(uri: String?) {

        _state.update { it.copy(logoUri = uri) }
    }

    /**
     * Actualiza la preferencia de incluir empresa en el estado temporal.
     * @param value true para incluir la razón social.
     */
    fun onIncluirEmpresaChanged(value: Boolean) {

        _state.update { it.copy(incluirEmpresa = value) }
    }

    /**
     * Actualiza la preferencia de incluir cargo en el estado temporal.
     * @param value true para incluir el cargo.
     */
    fun onIncluirCargoChanged(value: Boolean) {

        _state.update { it.copy(incluirCargo = value) }
    }

    /**
     * Cambia la preferencia de usar la TSL de prueba.
     * @param value true para activar validaciones de prueba.
     */
    fun onUsarTslPruebaChanged(value: Boolean) {

        _state.update { it.copy(usarTslPrueba = value) }
    }

    /**
     * Persiste la configuración actual en el DataStore.
     */
    fun guardar() {

        viewModelScope.launch {
            val s = _state.value
            configDataStore.guardar(
                ConfiguracionFirma(
                    logoUri = s.logoUri,
                    incluirEmpresa = s.incluirEmpresa,
                    incluirCargo = s.incluirCargo,
                    usarTslPrueba = s.usarTslPrueba,
                )
            )
            _state.update { it.copy(guardado = true) }
        }
    }

    /**
     * Revierte los cambios no guardados recargando la configuración original.
     */
    fun descartar() {

        viewModelScope.launch {
            configDataStore.configuracion.collect { config ->
                _state.update {
                    it.copy(
                        logoUri = config.logoUri,
                        incluirEmpresa = config.incluirEmpresa,
                        incluirCargo = config.incluirCargo,
                        usarTslPrueba = config.usarTslPrueba,
                    )
                }
            }
        }
    }

    /**
     * Limpia el flag de guardado exitoso.
     */
    fun clearGuardado() {

        _state.update { it.copy(guardado = false) }
    }
}
