package acj.soluciones.acjsignature.data.firma

import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.data.firma.mapper.FirmaMapper.toCertificado
import acj.soluciones.acjsignature.data.firma.mapper.FirmaMapper.toParameters
import acj.soluciones.acjsignature.data.firma.mapper.FirmaMapper.toResultadoValidacion
import android.content.Context
import acj.soluciones.acjsignature.shared.domain.Result
import com.acj.firma.controller.FirmaController
import com.acj.firma.controller.ValidacionController
import com.acj.firma.util.Tsl
import com.acj.firma.util.UtilAndroid
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import org.bouncycastle.jce.provider.BouncyCastleProvider
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import java.security.Security
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.fold

/**
 * Implementación concreta de [FirmaRepository].
 * ÚNICA clase de la app que importa y usa acjfirmalib.
 */
@Singleton
class FirmaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val p12StorageManager: P12StorageManager,
) : FirmaRepository {

    init {
        // Registrar BouncyCastle
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
        // Inicializar PdfBox-Android una sola vez
        PDFBoxResourceLoader.init(context)

        // Limpiar llaves viejas corruptas (Si quedaron en AndroidKeyStore)
        try {
            val ks = java.security.KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            val aliases = ks.aliases()
            while (aliases.hasMoreElements()) {
                ks.deleteEntry(aliases.nextElement())
            }
        } catch (e: Exception) {
            // ignorar
        }
    }

    // ─── Certificados ────────────────────────────────────────────────────────
    
    override suspend fun importarCertificado(bytes: ByteArray, password: String, alias: String, pin: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Validar que la contraseña y los bytes sirvan nativamente
                val ks = java.security.KeyStore.getInstance("PKCS12")
                ks.load(java.io.ByteArrayInputStream(bytes), password.toCharArray())
                
                // Guardarlo físicamente usando nuestro Manager
                p12StorageManager.saveCertificate(alias, bytes, password)
                
                // Guardar el hash del PIN de 6 dígitos asociado al certificado
                p12StorageManager.savePinHash(alias, pin)
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error("Error al importar: Contraseña incorrecta o archivo dañado.", it) }
            )
        }

    override suspend fun listarCertificados(): Result<List<Certificado>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val aliases = p12StorageManager.listAliases()
                aliases.mapNotNull { alias ->
                    runCatching {
                        val file = p12StorageManager.getCertificateFile(alias) ?: return@mapNotNull null
                        val pwd = p12StorageManager.getCertificatePassword(alias) ?: return@mapNotNull null
                        
                        val ks = java.security.KeyStore.getInstance("PKCS12")
                        ks.load(java.io.FileInputStream(file), pwd.toCharArray())
                        
                        // Extraer el primer certificado X509 que encontramos en la llave
                        var cert: java.security.cert.X509Certificate? = null
                        val keyAliases = ks.aliases()
                        while (keyAliases.hasMoreElements()) {
                            val a = keyAliases.nextElement()
                            if (ks.isKeyEntry(a)) {
                                cert = ks.getCertificate(a) as? java.security.cert.X509Certificate
                                break
                            }
                        }
                        cert?.toCertificado(alias)
                    }.getOrNull()
                }
            }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error("Error al listar certificados: ${it.message}", it) }
            )
        }

    // ─── PIN Verification ─────────────────────────────────────────────────────

    override suspend fun verificarPinCertificado(alias: String, pin: String): Boolean =
        withContext(Dispatchers.IO) {
            p12StorageManager.verifyPin(alias, pin)
        }

    // ─── Firma ────────────────────────────────────────────────────────────────
    override suspend fun firmarDocumento(documentoFirma: DocumentoFirma): Result<ResultadoFirma> =
        withContext(Dispatchers.IO) {
            runCatching {
                val params = documentoFirma.toParameters(context)
                
                // Inyectamos la ruta física y su contraseña para evitar usar AndroidKeyStore
                val certFile = p12StorageManager.getCertificateFile(documentoFirma.aliasCertificado)
                val pwd = p12StorageManager.getCertificatePassword(documentoFirma.aliasCertificado)
                
                if (certFile != null && pwd != null) {
                    params.setRutaCertificado(certFile.absolutePath)
                    params.setPasswordCertificado(pwd)
                }

                val controller = FirmaController()
                controller.firmarDocumento(params)

                val nombreSalida = "${documentoFirma.archivo.nameWithoutExtension}" +
                        "${documentoFirma.sufijo}.pdf"
                val archivoSalida = File(documentoFirma.rutaDestino, nombreSalida)

                ResultadoFirma(
                    archivoFirmado     = archivoSalida,
                    nombreArchivo      = nombreSalida,
                    aliasCertificado   = documentoFirma.aliasCertificado,
                )
            }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error("Error al firmar: ${it.message}", it) },
            )
        }

    // ─── Validación ───────────────────────────────────────────────────────────
    override suspend fun validarDocumento(archivoPdf: File): Result<ResultadoValidacion> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Cargar TSL (usa URL configurada en common.properties del AAR)
                val tsl = Tsl(/* url = */ "", /* maxAge = */ null,
                    /* verificar = */ true, context)

                @Suppress("UNCHECKED_CAST")
                val result = ValidacionController
                    .validarDocumento(context, archivoPdf, tsl)

                result.toResultadoValidacion()
            }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error("Error al validar: ${it.message}", it) },
            )
        }
}