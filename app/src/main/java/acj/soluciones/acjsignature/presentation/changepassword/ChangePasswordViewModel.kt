package acj.soluciones.acjsignature.presentation.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.data.remote.dto.request.ChangePasswordRequest
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val oldPasswordError: String? = null,
    val newPassword: String = "",
    val newPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun onOldPasswordChanged(value: String) {
        _uiState.update { 
            it.copy(
                oldPassword = value,
                oldPasswordError = if (value.isNotBlank() && value.length >= 6) null else it.oldPasswordError
            ) 
        }
    }

    fun onNewPasswordChanged(value: String) {
        _uiState.update { 
            it.copy(
                newPassword = value,
                newPasswordError = if (value.isNotBlank() && value.length >= 6) null else it.newPasswordError
            ) 
        }
    }

    fun submit() {
        val oldPassword = _uiState.value.oldPassword
        val newPassword = _uiState.value.newPassword
        var hasError = false
        var oldPassErr: String? = null
        var newPassErr: String? = null

        if (oldPassword.isBlank() || oldPassword.length < 6) {
            oldPassErr = "La contraseña debe tener al menos 6 caracteres."
            hasError = true
        }

        if (newPassword.isBlank() || newPassword.length < 6) {
            newPassErr = "La contraseña debe tener al menos 6 caracteres."
            hasError = true
        } else if (oldPassword == newPassword) {
            newPassErr = "La nueva contraseña no puede ser igual a la actual."
            hasError = true
        }

        if (hasError) {
            _uiState.update { it.copy(oldPasswordError = oldPassErr, newPasswordError = newPassErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val request = ChangePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )
            
            when (val result = authRepository.changePassword(request)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, successMessage = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null, oldPasswordError = null, newPasswordError = null) }
    }
}
