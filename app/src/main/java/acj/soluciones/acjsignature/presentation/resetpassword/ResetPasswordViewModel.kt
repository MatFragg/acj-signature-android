package acj.soluciones.acjsignature.presentation.resetpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.data.remote.dto.request.ResetPasswordRequest
import acj.soluciones.acjsignature.domain.repository.AuthRepository
import acj.soluciones.acjsignature.shared.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val email: String = "",
    val otp: String = "",
    val otpError: String? = null,
    val newPassword: String = "",
    val newPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val email = savedStateHandle.get<String>("email") ?: ""
        _uiState.update { it.copy(email = email) }
    }

    fun onOtpChanged(value: String) {
        _uiState.update { 
            it.copy(
                otp = value,
                otpError = if (value.isNotBlank() && value.length >= 6) null else it.otpError
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
        val otp = _uiState.value.otp
        val newPassword = _uiState.value.newPassword
        var hasError = false
        var otpErr: String? = null
        var newPassErr: String? = null

        if (otp.isBlank() || otp.length < 6) {
            otpErr = "Ingrese un código OTP válido."
            hasError = true
        }

        if (newPassword.isBlank() || newPassword.length < 6) {
            newPassErr = "La contraseña debe tener al menos 6 caracteres."
            hasError = true
        }

        if (hasError) {
            _uiState.update { it.copy(otpError = otpErr, newPasswordError = newPassErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val request = ResetPasswordRequest(
                email = _uiState.value.email,
                otp = otp,
                newPassword = newPassword
            )
            
            when (val result = authRepository.resetPassword(request)) {
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
        _uiState.update { it.copy(error = null, otpError = null, newPasswordError = null) }
    }
}
