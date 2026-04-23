package acj.soluciones.acjsignature.presentation.configuracion

data class ConfiguracionState(
    val logoUri: String? = null,
    val incluirEmpresa: Boolean = true,
    val incluirCargo: Boolean = true,
    val isLoading: Boolean = false,
    val guardado: Boolean = false,
)
