package acj.soluciones.acjsignature.presentation.verifyotp

data class VerifyOtpUiState(
    val email: String = "",
    val otp: String = "",
    val otpError: String? = null,
    val isLoading: Boolean = false,
    val isResendLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val resendSuccessMessage: String? = null
)
