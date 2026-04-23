package acj.soluciones.acjsignature.presentation.certificados

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.shared.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    fun onPasswordConfirmed(password: String) {
        val bytes = pendingP12Bytes ?: return
        _state.update { it.copy(showPasswordDialog = false, isLoading = true) }

        viewModelScope.launch {
            // Use the repository to import P12 into the local filesystem and bypass KeyStore
            val alias = "cert_${System.currentTimeMillis()}"
            when (val result = firmaRepository.importarCertificado(bytes, password, alias)) {
                is Result.Success -> {
                    pendingP12Bytes = null
                    pendingP12Uri = null
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
        pendingP12Bytes = null
        pendingP12Uri = null
        _state.update { it.copy(showPasswordDialog = false) }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, importSuccess = null) }
    }
}