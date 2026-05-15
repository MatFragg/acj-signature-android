package acj.soluciones.acjsignature.data.remote.dto.request

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)
