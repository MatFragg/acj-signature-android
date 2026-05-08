package acj.soluciones.acjsignature.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.data.remote.dto.request.RegisterRequest
import acj.soluciones.acjsignature.domain.usecase.ConsultarDniUseCase
import acj.soluciones.acjsignature.domain.usecase.RegisterUseCase
import acj.soluciones.acjsignature.shared.domain.Result
import android.util.Patterns
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestionar el flujo de registro, incluyendo la consulta automática de DNI.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val consultarDniUseCase: ConsultarDniUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()


    fun onEmailChanged(value: String) {
        _uiState.update { 
            it.copy(
                email = value,
                emailError = if (Patterns.EMAIL_ADDRESS.matcher(value).matches()) null else it.emailError
            ) 
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { 
            it.copy(
                password = value,
                passwordError = if (value.length >= 6) null else it.passwordError
            ) 
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { 
            it.copy(
                confirmPassword = value,
                confirmPasswordError = if (value == it.password) null else it.confirmPasswordError
            ) 
        }
    }

    fun onDniChanged(value: String) {
        // Permitir solo números y máximo 8 dígitos
        val filtered = value.filter { it.isDigit() }.take(8)
        _uiState.update { 
            it.copy(
                dni = filtered,
                dniError = if (filtered.length == 8) null else it.dniError
            ) 
        }
        
        // Auto-consulta si llega a los 8 dígitos
        if (filtered.length == 8) {
            consultarDni(filtered)
        }
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { 
            it.copy(
                firstName = value,
                firstNameError = if (value.isNotBlank()) null else it.firstNameError
            ) 
        }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { 
            it.copy(
                lastName = value,
                lastNameError = if (value.isNotBlank()) null else it.lastNameError
            ) 
        }
    }

    fun onAcceptTermsChanged(value: Boolean) {
        _uiState.update { it.copy(acceptTerms = value) }
    }

    private fun consultarDni(dni: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isDniLoading = true, 
                    dniQueryError = null,
                    isDniSuccess = false
                ) 
            }
            
            when (val result = consultarDniUseCase(dni)) {
                is Result.Success -> {
                    val response = result.data
                    val nombres = response.firstName ?: ""
                    val apellidos = listOfNotNull(response.firstLastName, response.secondLastName)
                        .joinToString(" ")
                        .trim()
                    
                    _uiState.update { 
                        it.copy(
                            isDniLoading = false,
                            isDniSuccess = true,
                            firstName = nombres,
                            lastName = apellidos,
                            firstNameError = null,
                            lastNameError = null
                        ) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isDniLoading = false,
                            dniQueryError = result.message
                        ) 
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun register() {
        // Validaciones locales rápidas de UI
        val state = _uiState.value
        var hasError = false
        
        var emailErr: String? = null
        var passwordErr: String? = null
        var confirmPasswordErr: String? = null
        var dniErr: String? = null
        var firstNameErr: String? = null
        var lastNameErr: String? = null

        if (state.email.isBlank()) {
            emailErr = "El correo es requerido."
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailErr = "Correo electrónico inválido."
            hasError = true
        }

        if (state.password.isBlank()) {
            passwordErr = "La contraseña es requerida."
            hasError = true
        } else if (state.password.length < 6) {
            passwordErr = "Mínimo 6 caracteres."
            hasError = true
        } else {
            val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$"
            if (!state.password.matches(passwordPattern.toRegex())) {
                passwordErr = "La contraseña debe tener al menos una letra en mayuscula, una en minuscula y un digito."
                hasError = true
            }
        }

        if (state.confirmPassword != state.password) {
            confirmPasswordErr = "Las contraseñas no coinciden."
            hasError = true
        }

        if (state.dni.length != 8) {
            dniErr = "El DNI debe tener 8 dígitos."
            hasError = true
        }

        if (state.firstName.isBlank()) {
            firstNameErr = "El nombre es requerido."
            hasError = true
        }

        if (state.lastName.isBlank()) {
            lastNameErr = "El apellido es requerido."
            hasError = true
        }

        if (!state.acceptTerms) {
            _uiState.update { it.copy(registerError = "Debe aceptar los Términos y Condiciones.") }
            return
        }

        if (hasError) {
            _uiState.update { 
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr,
                    dniError = dniErr,
                    firstNameError = firstNameErr,
                    lastNameError = lastNameErr
                ) 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRegisterLoading = true, registerError = null) }
            
            val request = RegisterRequest(
                email = state.email,
                password = state.password,
                dni = state.dni,
                firstName = state.firstName,
                lastName = state.lastName
            )
            
            when (val result = registerUseCase(request)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isRegisterLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isRegisterLoading = false, registerError = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearErrors() {
        _uiState.update { 
            it.copy(
                registerError = null,
                dniQueryError = null
            ) 
        }
    }
}
