package acj.soluciones.acjsignature.shared.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Formats bytes into a human-readable size string.
 */
fun Long.toFormattedSize(): String = when {
    this < 1024 -> "$this B"
    this < 1024 * 1024 -> String.format("%.1f KB", this / 1024.0)
    else -> String.format("%.1f MB", this / (1024.0 * 1024.0))
}

/**
 * Converts a timestamp to a relative time string in Spanish.
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
 * Copies a content URI into a destination File.
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
 * Gets the file name from a content URI.
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
 * Gets the file size from a content URI.
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