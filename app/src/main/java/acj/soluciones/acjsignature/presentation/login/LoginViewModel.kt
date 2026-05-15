package acj.soluciones.acjsignature.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.usecase.LoginUseCase
import acj.soluciones.acjsignature.shared.domain.Result
import android.util.Patterns
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gestiona la lógica de la pantalla de inicio de sesión.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { 
            it.copy(
                email = value,
                emailError = if (value.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(value).matches()) null else it.emailError
            ) 
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { 
            it.copy(
                password = value,
                passwordError = if (value.isNotBlank() && value.length >= 6) null else it.passwordError
            ) 
        }
    }

    fun onRememberMeChanged(value: Boolean) {
        _uiState.update { it.copy(rememberMe = value) }
    }

    fun login() {
        // Validaciones locales rápidas de UI para actualizar errores de input específicos
        val email = _uiState.value.email
        val password = _uiState.value.password

        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isBlank()) {
            emailErr = "El email es requerido."
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Correo electrónico inválido."
            hasError = true
        }

        if (password.isBlank()) {
            passwordErr = "La contraseña es requerida."
            hasError = true
        } else if (password.length < 6) {
            passwordErr = "Mínimo 6 caracteres."
            hasError = true
        }

        if (hasError) {
            _uiState.update { 
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr
                ) 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    if (result.message.contains("Por favor verifica tu email") || result.message.contains("verifica tu email antes de iniciar sesión")) {
                        _uiState.update { it.copy(isLoading = false, requiresVerification = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
                is Result.Loading -> {
                    // No debería ocurrir directamente desde el caso de uso
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
