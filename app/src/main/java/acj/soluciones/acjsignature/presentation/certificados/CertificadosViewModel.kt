package acj.soluciones.acjsignature.presentation.certificados

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.shared.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CertificadosViewModel @Inject constructor(
    private val firmaRepository: FirmaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CertificadosState())
    val state = _state.asStateFlow()

    // Selected P12 file URI for import
    private var pendingP12Uri: Uri? = null
    private var pendingP12Bytes: ByteArray? = null
    private var pendingPassword: String? = null

    init {
        cargarCertificados()
    }

    fun cargarCertificados() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = firmaRepository.listarCertificados()) {
                is Result.Success -> {
                    _state.update {
                        it.copy(certificados = result.data, isLoading = false)
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(error = result.message, isLoading = false)
                    }
                }

                else -> {
                    _state.update {
                        it.copy(error = "Error desconocido", isLoading = false)
                    }
                }
            }
        }
    }

    fun onP12Selected(uri: Uri, bytes: ByteArray) {
        pendingP12Uri = uri
        pendingP12Bytes = bytes
        _state.update { it.copy(showPasswordDialog = true) }
    }

    /**
     * Paso 1: Validar la contraseña del .p12.
     * Si es correcta, se guarda temporalmente y se abre el diálogo de PIN.
     */
    fun onPasswordConfirmed(password: String) {
        val bytes = pendingP12Bytes ?: return

        viewModelScope.launch {
            _state.update { it.copy(showPasswordDialog = false, isLoading = true) }

            // Verificar la contraseña antes de pedir el PIN
            val isValid = withContext(Dispatchers.IO) {
                runCatching {
                    val ks = java.security.KeyStore.getInstance("PKCS12")
                    ks.load(java.io.ByteArrayInputStream(bytes), password.toCharArray())
                }.isSuccess
            }

            if (isValid) {
                pendingPassword = password
                _state.update { it.copy(isLoading = false, showPinDialog = true) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Contraseña incorrecta o archivo dañado.",
                    )
                }
            }
        }
    }

    /**
     * Paso 2: El usuario creó su PIN de 6 dígitos.
     * Se procede a importar el certificado con el PIN.
     */
    fun onPinConfirmed(pin: String) {
        val bytes = pendingP12Bytes ?: return
        val password = pendingPassword ?: return

        _state.update { it.copy(showPinDialog = false, isLoading = true) }

        viewModelScope.launch {
            val alias = "cert_${System.currentTimeMillis()}"

            when (val result = firmaRepository.importarCertificado(bytes, password, alias, pin)) {
                is Result.Success -> {
                    clearPendingState()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            importSuccess = "Certificado importado exitosamente",
                        )
                    }
                    cargarCertificados()
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                        )
                    }
                }
                else -> {
                    _state.update { it.copy(isLoading = false, error = "Error desconocido al importar") }
                }
            }
        }
    }

    fun onCancelImport() {
        clearPendingState()
        _state.update { it.copy(showPasswordDialog = false) }
    }

    fun onCancelPin() {
        clearPendingState()
        _state.update { it.copy(showPinDialog = false) }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, importSuccess = null) }
    }

    private fun clearPendingState() {
        pendingP12Bytes = null
        pendingP12Uri = null
        pendingPassword = null
    }
}