package acj.soluciones.acjsignature.presentation.firma

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import acj.soluciones.acjsignature.shared.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirmaViewModel @Inject constructor(
    private val documentoRepository: DocumentoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FirmaState())
    val state = _state.asStateFlow()

    fun onFileSelected(uri: Uri, fileName: String, fileSize: Long) {
        if (fileSize > Constants.MAX_FILE_SIZE_BYTES) {
            _state.update {
                it.copy(error = "El archivo excede el tamaño máximo de 25 MB")
            }
            return
        }
        if (!fileName.endsWith(".pdf", ignoreCase = true)) {
            _state.update {
                it.copy(error = "Solo se permiten archivos PDF")
            }
            return
        }
        _state.update {
            it.copy(
                fileName = fileName,
                fileSize = fileSize,
                fileUri = uri.toString(),
                error = null,
            )
        }
    }

    fun guardarYContinuar(onNavigate: (Long) -> Unit) {
        val currentState = _state.value
        val uriString = currentState.fileUri ?: return
        val fileName = currentState.fileName ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val docId = documentoRepository.guardarDocumento(
                    uri = Uri.parse(uriString),
                    fileName = fileName,
                    fileSize = currentState.fileSize,
                )
                _state.update { it.copy(documentoId = docId, isLoading = false) }
                onNavigate(docId)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al guardar: ${e.message}",
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}