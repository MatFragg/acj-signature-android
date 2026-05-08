package acj.soluciones.acjsignature.presentation.register

/**
 * Representa el estado de la interfaz de usuario para la pantalla de registro.
 */
data class RegisterUiState(
    val email: String = "",
    val emailError: String? = null,
    
    val password: String = "",
    val passwordError: String? = null,
    
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    
    val dni: String = "",
    val dniError: String? = null,
    
    val firstName: String = "",
    val firstNameError: String? = null,
    
    val lastName: String = "",
    val lastNameError: String? = null,
    
    val acceptTerms: Boolean = false,
    
    // Estados asíncronos para consulta de DNI
    val isDniLoading: Boolean = false,
    val isDniSuccess: Boolean = false,
    val dniQueryError: String? = null,
    
    // Estados asíncronos para registro
    val isRegisterLoading: Boolean = false,
    val registerError: String? = null,
    val isSuccess: Boolean = false
)
