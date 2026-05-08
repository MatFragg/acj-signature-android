package acj.soluciones.acjsignature.presentation.login

/**
 * Representa el estado de la interfaz de usuario para la pantalla de inicio de sesión.
 */
data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val rememberMe: Boolean = false
)
