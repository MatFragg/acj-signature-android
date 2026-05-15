package acj.soluciones.acjsignature.presentation.verifyotp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.usecase.ResendOtpUseCase
import acj.soluciones.acjsignature.domain.usecase.VerifyOtpUseCase
import acj.soluciones.acjsignature.shared.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resendOtpUseCase: ResendOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyOtpUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val email = savedStateHandle.get<String>("email") ?: ""
        _uiState.update { it.copy(email = email) }
    }

    fun onOtpChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(6)
        _uiState.update { 
            it.copy(
                otp = filtered,
                otpError = if (filtered.length == 6) null else it.otpError,
                error = null,
                resendSuccessMessage = null
            ) 
        }
    }

    fun verifyOtp() {
        val otp = _uiState.value.otp
        val email = _uiState.value.email

        if (otp.length != 6) {
            _uiState.update { it.copy(otpError = "El código OTP debe tener 6 dígitos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, resendSuccessMessage = null) }
            
            when (val result = verifyOtpUseCase(email, otp)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun resendOtp() {
        val email = _uiState.value.email
        
        viewModelScope.launch {
            _uiState.update { it.copy(isResendLoading = true, error = null, resendSuccessMessage = null) }
            
            when (val result = resendOtpUseCase(email)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isResendLoading = false, 
                            resendSuccessMessage = result.data.message ?: "Código OTP reenviado exitosamente."
                        ) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isResendLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, resendSuccessMessage = null) }
    }
}
