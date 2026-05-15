package acj.soluciones.acjsignature.data.remote.dto.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
