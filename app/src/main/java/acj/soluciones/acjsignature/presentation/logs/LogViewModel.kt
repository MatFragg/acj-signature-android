package acj.soluciones.acjsignature.presentation.logs

import androidx.lifecycle.ViewModel
import acj.soluciones.acjsignature.shared.util.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * ViewModel que expone los flujos de datos de logging para la interfaz de usuario.
 * Provee acceso reactivo al buffer en memoria y métodos para gestionar archivos físicos.
 *
 * @property appLogger Singleton de logging inyectado por Hilt.
 * @author Ethan Matias Aliaga Aguirre (y Antigravity)
 * @date 2026-05-04
 */
@HiltViewModel
class LogViewModel @Inject constructor(
    private val appLogger: AppLogger
) : ViewModel() {

    /**
     * Flujo reactivo de los logs de la sesión actual en memoria.
     */
    val logs = appLogger.logs

    /**
     * Contenido formateado del log del día actual.
     */
    fun getTodayLog() = appLogger.getTodayLogContent()

    /**
     * Lista de archivos físicos de log almacenados.
     */
    fun getLogFiles() = appLogger.listLogFiles()

    /**
     * Limpia el buffer en memoria y opcionalmente los archivos físicos.
     * @param clearFiles Si es true, borra todos los archivos .log del dispositivo.
     */
    fun clearLogs(clearFiles: Boolean = false) {
        appLogger.clear(clearFiles)
    }

    /**
     * Recarga los logs desde el archivo del día actual hacia la memoria.
     */
    fun refreshLogs() {
        appLogger.reloadFromDisk()
    }
}
