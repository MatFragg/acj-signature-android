package acj.soluciones.acjsignature.presentation.certificados

import acj.soluciones.acjsignature.domain.model.Certificado

data class CertificadosState(
    val certificados: List<Certificado> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showPasswordDialog: Boolean = false,
    val importSuccess: String? = null,
)
