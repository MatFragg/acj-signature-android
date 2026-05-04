package acj.soluciones.acjsignature.shared.util

/**
 * Constantes globales utilizadas en diversos módulos de la aplicación.
 * Define nombres de base de datos, directorios de almacenamiento y límites de archivos.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
object Constants {
    const val MAX_FILE_SIZE_BYTES = 25L * 1024 * 1024 // 25 MB
    const val DIR_DOCUMENTOS = "documentos"
    const val DIR_ORIGINALES = "originales"
    const val DIR_FIRMADOS = "firmados"
    const val SUFIJO_FIRMADO = "_firmado"
    const val DB_NAME = "acj_signature_db"
    const val DATASTORE_NAME = "acj_preferences"
}