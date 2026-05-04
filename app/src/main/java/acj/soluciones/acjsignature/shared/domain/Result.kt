package acj.soluciones.acjsignature.shared.domain

/**
 * Clase sellada que representa el resultado de una operación que puede fallar.
 * Facilita el manejo de estados de éxito, error y carga en la capa de presentación.
 *
 * @param T Tipo del dato contenido en caso de éxito.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
sealed class Result<out T> {
    /**
     * Representa un resultado exitoso que contiene un dato.
     * @property data Valor obtenido de la operación.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Representa un fallo en la operación con un mensaje y causa opcional.
     * @property message Descripción legible del error.
     * @property cause Excepción original que provocó el fallo.
     */
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()

    /**
     * Estado transicional que indica que la operación está en ejecución.
     */
    object Loading : Result<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading

    /**
     * Recupera el dato si es un éxito, de lo contrario retorna null.
     * @return El valor de tipo T o null.
     */
    fun getOrNull(): T? = if (this is Success) data else null

    /**
     * Recupera el mensaje de error si existe.
     * @return Cadena con el error o null.
     */
    fun getErrorOrNull(): String? = if (this is Error) message else null
}

/**
 * Ejecuta un bloque de código si el resultado es exitoso.
 *
 * @param T Tipo del dato original.
 * @param action Función a ejecutar con el dato de éxito.
 * @return La instancia original de Result para encadenamiento.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Ejecuta un bloque de código si el resultado es un error.
 *
 * @param T Tipo del dato original.
 * @param action Función que recibe el mensaje y la causa del error.
 * @return La instancia original de Result para encadenamiento.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
inline fun <T> Result<T>.onError(action: (String, Throwable?) -> Unit): Result<T> {
    if (this is Result.Error) action(message, cause)
    return this
}

/**
 * Transforma el dato contenido en un Result.Success manteniendo el estado de error o carga.
 *
 * @param T Tipo original.
 * @param R Tipo resultante tras la transformación.
 * @param transform Función de mapeo.
 * @return Nuevo Result con el tipo transformado.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error   -> this
    is Result.Loading -> Result.Loading
}

