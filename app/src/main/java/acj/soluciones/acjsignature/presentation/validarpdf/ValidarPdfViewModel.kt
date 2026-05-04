package acj.soluciones.acjsignature.presentation.validarpdf

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.usecase.ValidarDocumentoUseCase
import acj.soluciones.acjsignature.shared.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * ViewModel que gestiona la validación técnica de documentos PDF externos.
 * Permite seleccionar un archivo del dispositivo y analizar su integridad y las firmas
 * digitales que contiene utilizando el motor criptográfico de la aplicación.
 *
 * @property validarDocumentoUseCase Caso de uso para el análisis forense del PDF.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@HiltViewModel
class ValidarPdfViewModel @Inject constructor(
    private val validarDocumentoUseCase: ValidarDocumentoUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(ValidarPdfState())
    val state: StateFlow<ValidarPdfState> = _state.asStateFlow()

    /**
     * Registra la selección de un documento para ser validado.
     * @param uri URI del archivo PDF.
     * @param name Nombre del archivo.
     * @param size Tamaño en bytes.
     */
    fun onFileSelected(uri: Uri, name: String, size: Long) {

        _state.update {
            it.copy(
                fileUri = uri,
                fileName = name,
                fileSize = size,
                error = null,
                resultado = null
            )
        }
    }

    /**
     * Ejecuta el proceso de validación sobre el archivo seleccionado.
     * @param context Contexto para resolver URIs y acceso al sistema de archivos temporal.
     * @param onSuccess Callback invocado tras completar el análisis satisfactoriamente.
     */
    fun validarDocumento(context: Context, onSuccess: () -> Unit) {

        val currentState = _state.value
        if (currentState.fileUri == null) return

        viewModelScope.launch {
            _state.update { it.copy(isValidating = true, error = null) }

            try {
                // Copiar URI a archivo temporal
                val tempFile = File(context.cacheDir, currentState.fileName ?: "temp.pdf")
                context.contentResolver.openInputStream(currentState.fileUri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val result = validarDocumentoUseCase(tempFile)
                tempFile.delete()

                when (result) {
                    is Result.Success -> {
                        _state.update { 
                            it.copy(isValidating = false, resultado = result.data) 
                        }
                        onSuccess()
                    }
                    is Result.Error -> {
                        _state.update { 
                            it.copy(isValidating = false, error = result.message) 
                        }
                    }
                    is Result.Loading -> {}
                }

            } catch (e: Exception) {
                _state.update { 
                    it.copy(isValidating = false, error = "Error al procesar el archivo: ${e.message}") 
                }
            }
        }
    }

    /**
     * Limpia los mensajes de error del estado.
     */
    fun clearError() {

        _state.update { it.copy(error = null) }
    }

    /**
     * Reinicia el estado de validación para procesar un nuevo documento.
     */
    fun resetState() {

        _state.value = ValidarPdfState()
    }
}
