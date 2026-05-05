package acj.soluciones.acjsignature.shared.util

import android.content.Context
import android.util.Log
import acj.soluciones.acjsignature.shared.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Synchronized

/**
 * Singleton de logging encargado de la gestión de trazabilidad en memoria y archivo.
 * Implementa rotación diaria de archivos y formato estructurado para auditoría.
 *
 * @property context Contexto de la aplicación para acceso al sistema de archivos.
 * @author Ethan Matias Aliaga Aguirre (y Antigravity)
 * @date 2026-05-04
 * @version 1.0
 */
@Singleton
class AppLogger @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tag = "ACJ_LOG"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    data class LogEntry(
        val timestamp: Long = System.currentTimeMillis(),
        val level: LogLevel,
        val message: String
    ) {
        fun format(): String {
            val sdf = SimpleDateFormat("HH:mm:ss yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = sdf.format(Date(timestamp))
            return "$time|${level.name}|$message"
        }
    }

    enum class LogLevel { INFO, WARNING, ERROR, DEBUG }

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    private val logsDir: File by lazy {
        File(context.filesDir, Constants.DIR_LOGS).also { it.mkdirs() }
    }

    /**
     * Registra un mensaje informativo.
     */
    fun info(message: String) = log(LogLevel.INFO, message)

    /**
     * Registra un mensaje de advertencia.
     */
    fun warning(message: String) = log(LogLevel.WARNING, message)

    /**
     * Registra un error crítico.
     */
    fun error(message: String, throwable: Throwable? = null) {
        val fullMessage = if (throwable != null) "$message: ${throwable.message}" else message
        log(LogLevel.ERROR, fullMessage)
    }

    /**
     * Registra mensajes de depuración.
     */
    fun debug(message: String) = log(LogLevel.DEBUG, message)

    private fun log(level: LogLevel, message: String) {
        val entry = LogEntry(level = level, message = message)
        
        // Android Logcat
        when (level) {
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARNING -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
            LogLevel.DEBUG -> Log.d(tag, message)
        }

        // Buffer en memoria (máx 500 para evitar consumo excesivo)
        _logs.update { current ->
            (current + entry).takeLast(500)
        }

        // Persistencia asíncrona en archivo
        scope.launch {
            writeToFile(entry)
        }
    }

    @Synchronized
    private fun writeToFile(entry: LogEntry) {
        try {
            val fileName = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) + ".log"
            val logFile = File(logsDir, fileName)
            
            FileWriter(logFile, true).use { writer ->
                writer.append(entry.format())
                writer.append("\n")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error escribiendo log a archivo: ${e.message}")
        }
    }

    /**
     * Vacia el historial en memoria y opcionalmente los archivos físicos.
     */
    fun clear(clearFiles: Boolean = false) {
        _logs.value = emptyList()
        if (clearFiles) {
            scope.launch {
                logsDir.listFiles()?.forEach { it.delete() }
            }
        }
    }

    /**
     * Recarga los logs desde el archivo del día actual hacia la memoria.
     */
    fun reloadFromDisk() {
        scope.launch {
            try {
                val fileName = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) + ".log"
                val logFile = File(logsDir, fileName)
                if (logFile.exists()) {
                    val lines = logFile.readLines()
                    val sdf = SimpleDateFormat("HH:mm:ss yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val loadedEntries = lines.mapNotNull { line ->
                        try {
                            val parts = line.split("|", limit = 3)
                            if (parts.size >= 3) {
                                val timestamp = sdf.parse(parts[0])?.time ?: System.currentTimeMillis()
                                val level = LogLevel.valueOf(parts[1])
                                val message = parts[2]
                                LogEntry(timestamp, level, message)
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                    _logs.value = loadedEntries.takeLast(500)
                } else {
                    _logs.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(tag, "Error recargando logs desde archivo: ${e.message}")
            }
        }
    }

    /**
     * Obtiene el contenido del log del día actual como texto plano.
     */
    fun getTodayLogContent(): String {
        val fileName = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) + ".log"
        val logFile = File(logsDir, fileName)
        return if (logFile.exists()) logFile.readText() else ""
    }

    /**
     * Lista todos los archivos de log disponibles.
     */
    fun listLogFiles(): List<File> {
        return logsDir.listFiles()?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
}
