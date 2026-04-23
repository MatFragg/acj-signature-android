package acj.soluciones.acjsignature.presentation.firma

data class FirmaState(
    val fileName: String? = null,
    val fileSize: Long = 0L,
    val fileUri: String? = null,
    val documentoId: Long? = null,
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
)