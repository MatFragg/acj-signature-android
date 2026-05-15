package acj.soluciones.acjsignature.data.remote.dto.request

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)
