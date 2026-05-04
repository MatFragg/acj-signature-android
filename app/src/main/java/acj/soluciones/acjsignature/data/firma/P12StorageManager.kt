package acj.soluciones.acjsignature.data.firma

import android.content.Context
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor especializado en el almacenamiento y recuperación de archivos de certificado PKCS12 (.p12).
 * Maneja la persistencia física de los archivos y el resguardo enmascarado de sus contraseñas.
 *
 * @property context Contexto de la aplicación para acceder al sistema de archivos interno y preferencias.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Singleton
class P12StorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val certDir = File(context.filesDir, "certificados")
    private val prefs = context.getSharedPreferences("cert_prefs", Context.MODE_PRIVATE)

    init {
        if (!certDir.exists()) {
            certDir.mkdirs()
        }
    }

    /**
     * Guarda el archivo de certificado en la memoria interna y asocia su contraseña.
     * La contraseña se almacena codificada en Base64 para evitar su exposición en texto plano.
     *
     * @param alias Nombre identificador del certificado.
     * @param p12Bytes Contenido binario del archivo.
     * @param password Contraseña de acceso.
     * @return Objeto File que apunta al certificado guardado.
     */
    fun saveCertificate(alias: String, p12Bytes: ByteArray, password: String): File {
        val certFile = File(certDir, "$alias.p12")
        certFile.writeBytes(p12Bytes)

        val encoded = Base64.encodeToString(password.toByteArray(), Base64.DEFAULT)
        prefs.edit().putString("${alias}_pwd", encoded).apply()

        return certFile
    }

    /**
     * Recupera la referencia física del archivo de certificado.
     *
     * @param alias Identificador del certificado.
     * @return El objeto File si existe, o null en caso contrario.
     */
    fun getCertificateFile(alias: String): File? {
        val file = File(certDir, "$alias.p12")
        return if (file.exists()) file else null
    }

    /**
     * Decodifica y recupera la contraseña asociada a un alias de certificado.
     *
     * @param alias Identificador del certificado.
     * @return La contraseña en texto plano o null si no se encuentra.
     */
    fun getCertificatePassword(alias: String): String? {
        val encoded = prefs.getString("${alias}_pwd", null) ?: return null
        return try {
            String(Base64.decode(encoded, Base64.DEFAULT))
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene la lista de todos los alias disponibles basados en los archivos persistidos.
     *
     * @return Lista de nombres de archivos sin extensión.
     */
    fun listAliases(): List<String> {
        return certDir.listFiles { file -> file.extension == "p12" || file.extension == "pfx" }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    /**
     * Elimina el archivo de certificado y limpia sus credenciales de las preferencias.
     *
     * @param alias Identificador del certificado a borrar.
     * @return true si el proceso de eliminación física fue exitoso.
     */
    fun deleteCertificate(alias: String): Boolean {
        val file = File(certDir, "$alias.p12")
        val deletedFile = if (file.exists()) file.delete() else true
        
        prefs.edit()
            .remove("${alias}_pwd")
            .apply()
            
        return deletedFile
    }
}
