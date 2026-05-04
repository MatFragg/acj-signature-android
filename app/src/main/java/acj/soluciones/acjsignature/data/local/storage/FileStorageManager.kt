package acj.soluciones.acjsignature.data.local.storage

import android.content.Context
import android.net.Uri
import acj.soluciones.acjsignature.shared.util.Constants
import acj.soluciones.acjsignature.shared.util.copyToFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor responsable de la manipulación de archivos físicos en el almacenamiento del dispositivo.
 * Se encarga de organizar los directorios para archivos originales, firmados y recursos temporales.
 *
 * @property context Contexto de la aplicación inyectado por Hilt.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Singleton
class FileStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val baseDir: File
        get() = context.getExternalFilesDir(Constants.DIR_DOCUMENTOS)
            ?: context.filesDir.resolve(Constants.DIR_DOCUMENTOS)

    private val originalesDir: File
        get() = File(baseDir, Constants.DIR_ORIGINALES).also { it.mkdirs() }

    private val firmadosDir: File
        get() = File(baseDir, Constants.DIR_FIRMADOS).also { it.mkdirs() }

    /**
     * Copia un archivo PDF desde un URI de contenido al directorio interno de "originales".
     *
     * @param uri URI de origen del documento seleccionado.
     * @param fileName Nombre original del archivo.
     * @return Objeto File apuntando a la nueva ubicación local.
     */
    fun saveOriginalPdf(uri: Uri, fileName: String): File {
        val destFile = File(originalesDir, fileName)
        // Si ya existe uno con el mismo nombre, añadimos timestamp
        val finalDest = if (destFile.exists()) {
            val nameNoExt = fileName.substringBeforeLast(".")
            val ext = fileName.substringAfterLast(".", "pdf")
            File(originalesDir, "${nameNoExt}_${System.currentTimeMillis()}.$ext")
        } else destFile
        return uri.copyToFile(context, finalDest)
    }

    /**
     * Provee el directorio de salida destinado a los documentos firmados.
     *
     * @return Directorio de archivos firmados.
     */
    fun getSignedOutputDir(): File = firmadosDir

    /**
     * Genera la ruta absoluta donde se guardará la versión firmada de un documento.
     *
     * @param originalName Nombre del archivo original.
     * @return Ruta absoluta para el archivo de salida.
     */
    fun getSignedFilePath(originalName: String): String {
        val nameNoExt = originalName.substringBeforeLast(".")
        val ext = originalName.substringAfterLast(".", "pdf")
        return File(firmadosDir, "${nameNoExt}${Constants.SUFIJO_FIRMADO}.$ext").absolutePath
    }

    /**
     * Elimina un archivo físico del almacenamiento.
     *
     * @param path Ruta absoluta del archivo a borrar.
     * @return true si el archivo fue eliminado exitosamente.
     */
    fun deleteFile(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.delete()
    }

    /**
     * Verifica la existencia de un archivo en una ruta específica.
     *
     * @param path Ruta absoluta a comprobar.
     * @return true si el archivo existe físicamente.
     */
    fun fileExists(path: String): Boolean = File(path).exists()

    /**
     * Recupera la ruta del logo por defecto (Escudo del Perú).
     * Si el recurso no existe en el sistema de archivos local, realiza una copia desde recursos raw.
     *
     * @return Ruta absoluta del archivo de imagen.
     */
    fun getDefaultLogoPath(): String {
        val dir = File(context.filesDir, "assets")
        if (!dir.exists()) dir.mkdirs()
        val defaultLogoFile = File(dir, "escudo_peru.png")

        // Si no existe o está vacío, lo copiamos
        if (!defaultLogoFile.exists() || defaultLogoFile.length() == 0L) {
            try {
                context.resources.openRawResource(acj.soluciones.acjsignature.R.raw.escudo_peru).use { inputStream ->
                    java.io.FileOutputStream(defaultLogoFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }
        return defaultLogoFile.absolutePath
    }
}
