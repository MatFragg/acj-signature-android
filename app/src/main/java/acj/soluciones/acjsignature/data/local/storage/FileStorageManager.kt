package acj.soluciones.acjsignature.data.local.storage

import android.content.Context
import android.net.Uri
import acj.soluciones.acjsignature.shared.util.Constants
import acj.soluciones.acjsignature.shared.util.copyToFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

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
     * Copies a PDF from a content URI into the "originales" directory.
     * Returns the destination File.
     */
    fun saveOriginalPdf(uri: Uri, fileName: String): File {
        val destFile = File(originalesDir, fileName)
        // If same name exists, add timestamp
        val finalDest = if (destFile.exists()) {
            val nameNoExt = fileName.substringBeforeLast(".")
            val ext = fileName.substringAfterLast(".", "pdf")
            File(originalesDir, "${nameNoExt}_${System.currentTimeMillis()}.$ext")
        } else destFile
        return uri.copyToFile(context, finalDest)
    }

    /**
     * Returns the output directory for signed documents.
     */
    fun getSignedOutputDir(): File = firmadosDir

    /**
     * Returns the full path for a signed version of a file.
     */
    fun getSignedFilePath(originalName: String): String {
        val nameNoExt = originalName.substringBeforeLast(".")
        val ext = originalName.substringAfterLast(".", "pdf")
        return File(firmadosDir, "${nameNoExt}${Constants.SUFIJO_FIRMADO}.$ext").absolutePath
    }

    /**
     * Deletes a file from storage.
     */
    fun deleteFile(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.delete()
    }

    /**
     * Checks if a file exists.
     */
    fun fileExists(path: String): Boolean = File(path).exists()
}
