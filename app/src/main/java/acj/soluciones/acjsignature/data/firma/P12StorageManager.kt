package acj.soluciones.acjsignature.data.firma

import android.content.Context
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

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
     * Guarda el archivo físicamente en la memoria interna de la app
     * y asocia la contraseña de forma enmascarada.
     */
    fun saveCertificate(alias: String, p12Bytes: ByteArray, password: String): File {
        val certFile = File(certDir, "$alias.p12")
        certFile.writeBytes(p12Bytes)

        // Enmascarar contraseña básico para no tenerla expuesta en raw string
        val encoded = Base64.encodeToString(password.toByteArray(), Base64.DEFAULT)
        prefs.edit().putString("${alias}_pwd", encoded).apply()

        return certFile
    }

    /**
     * Retorna el archivo fisico para firmar.
     */
    fun getCertificateFile(alias: String): File? {
        val file = File(certDir, "$alias.p12")
        return if (file.exists()) file else null
    }

    /**
     * Recupera la contraseña original del archivo.
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
     * Lista todos los alias físicos encontrados en la carpeta local.
     */
    fun listAliases(): List<String> {
        return certDir.listFiles { file -> file.extension == "p12" || file.extension == "pfx" }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    // ─── PIN Management ──────────────────────────────────────────────────────

    /**
     * Guarda el hash SHA-256 del PIN asociado al certificado.
     * El PIN nunca se almacena en texto plano.
     */
    fun savePinHash(alias: String, pin: String) {
        val hash = hashPin(pin)
        prefs.edit().putString("${alias}_pin_hash", hash).apply()
    }

    /**
     * Verifica que el PIN ingresado coincida con el almacenado.
     * Retorna false si no existe PIN configurado para el alias.
     */
    fun verifyPin(alias: String, pin: String): Boolean {
        val storedHash = prefs.getString("${alias}_pin_hash", null) ?: return false
        val inputHash = hashPin(pin)
        return storedHash == inputHash
    }

    /**
     * Indica si el certificado ya tiene un PIN configurado.
     * Útil para futura funcionalidad de cambio/recuperación de PIN.
     */
    fun hasPinConfigured(alias: String): Boolean {
        return prefs.getString("${alias}_pin_hash", null) != null
    }

    private fun hashPin(pin: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
