package acj.soluciones.acjsignature.shared.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Formatea un valor de tipo Long (bytes) hacia una cadena legible (B, KB, MB).
 *
 * @return Cadena formateada con la unidad de medida correspondiente.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
fun Long.toFormattedSize(): String = when {
    this < 1024 -> "$this B"
    this < 1024 * 1024 -> String.format("%.1f KB", this / 1024.0)
    else -> String.format("%.1f MB", this / (1024.0 * 1024.0))
}

/**
 * Convierte un timestamp en una cadena de tiempo relativo en español.
 *
 * @return Texto descriptivo del tiempo transcurrido (ej. "Hace 5 min").
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "Hace $days día${if (days > 1) "s" else ""}"
        hours > 0 -> "Hace $hours hora${if (hours > 1) "s" else ""}"
        minutes > 0 -> "Hace $minutes min"
        else -> "Justo ahora"
    }
}

/**
 * Copia el contenido de un URI hacia un archivo físico en el almacenamiento interno.
 *
 * @param context Contexto necesario para acceder al ContentResolver.
 * @param destFile Archivo de destino donde se escribirá el contenido.
 * @return El archivo de destino tras completar la copia.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
fun Uri.copyToFile(context: Context, destFile: File): File {
    context.contentResolver.openInputStream(this)?.use { input ->
        FileOutputStream(destFile).use { output ->
            input.copyTo(output)
        }
    } ?: throw IllegalStateException("No se pudo abrir el archivo")
    return destFile
}

/**
 * Recupera el nombre del archivo asociado a un URI de contenido.
 *
 * @param context Contexto para la consulta al ContentResolver.
 * @return Nombre del archivo con su extensión o un nombre por defecto.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
fun Uri.getFileName(context: Context): String {
    var name = "documento.pdf"
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}

/**
 * Obtiene el tamaño en bytes de un archivo identificado por un URI.
 *
 * @param context Contexto para la consulta al ContentResolver.
 * @return Tamaño del archivo en bytes o 0 si no se puede determinar.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
fun Uri.getFileSize(context: Context): Long {
    var size = 0L
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
        if (cursor.moveToFirst() && sizeIndex >= 0) {
            size = cursor.getLong(sizeIndex)
        }
    }
    return size
}