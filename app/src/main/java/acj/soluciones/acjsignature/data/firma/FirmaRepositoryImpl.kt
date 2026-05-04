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
import acj.soluciones.acjsignature.data.local.datastore.ConfigDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.fold

/**
 * Implementación concreta del repositorio de firma digital.
 * Esta es la única clase de la aplicación que interactúa directamente con la biblioteca nativa acjfirmalib.
 *
 * @property context Contexto de la aplicación necesario para inicializar recursos de firma.
 * @property p12StorageManager Gestor encargado de la persistencia de los archivos P12.
 * @property configDataStore Almacén de configuraciones para determinar parámetros de validación (TSL).
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Singleton
class FirmaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val p12StorageManager: P12StorageManager,
    private val configDataStore: ConfigDataStore,
) : FirmaRepository {

    init {
        // Registro de proveedores de seguridad y cargadores de recursos
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
        PDFBoxResourceLoader.init(context)

        // Limpieza preventiva de llaves en AndroidKeyStore para evitar conflictos heredados
        try {
            val ks = java.security.KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            val aliases = ks.aliases()
            while (aliases.hasMoreElements()) {
                ks.deleteEntry(aliases.nextElement())
            }
        } catch (e: Exception) {
            // Se ignora silenciosamente si falla la limpieza inicial
        }
    }

    // ─── Certificados ────────────────────────────────────────────────────────
    
    /**
     * Importa un archivo de certificado digital verificando su integridad y contraseña.
     *
     * @param bytes Contenido binario del certificado (.p12 / .pfx).
     * @param password Contraseña de desbloqueo del archivo.
     * @param alias Nombre único para identificar el certificado en la app.
     * @return Result indicando el éxito o el error detallado de la operación.
     */
    override suspend fun importarCertificado(bytes: ByteArray, password: String, alias: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val ks = java.security.KeyStore.getInstance("PKCS12")
                ks.load(java.io.ByteArrayInputStream(bytes), password.toCharArray())
                
                p12StorageManager.saveCertificate(alias, bytes, password)
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error("Error al importar: Contraseña incorrecta o archivo dañado.", it) }
            )
        }

    /**
     * Enumera los certificados disponibles extrayendo sus metadatos (X.509).
     *
     * @return Result con la lista de certificados mapeados a objetos de dominio.
     */
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

    // ─── Firma ────────────────────────────────────────────────────────────────
    
    /**
     * Ejecuta la lógica de firma digital invocando al controlador nativo.
     * Inyecta dinámicamente la URL de la TSL según la configuración actual.
     *
     * @param documentoFirma Parámetros y archivo a firmar.
     * @return Result con la información del archivo resultante.
     */
    override suspend fun firmarDocumento(documentoFirma: DocumentoFirma): Result<ResultadoFirma> =
        withContext(Dispatchers.IO) {
            runCatching {
                val tslUrl = getTslUrl()
                val finalDoc = if (documentoFirma.tsl.url.isEmpty()) {
                    documentoFirma.copy(
                        tsl = documentoFirma.tsl.copy(verificar = true, url = tslUrl)
                    )
                } else {
                    documentoFirma
                }

                val params = finalDoc.toParameters(context)
                
                val certFile = p12StorageManager.getCertificateFile(finalDoc.aliasCertificado)
                val pwd = p12StorageManager.getCertificatePassword(finalDoc.aliasCertificado)
                
                if (certFile != null && pwd != null) {
                    params.setRutaCertificado(certFile.absolutePath)
                    params.setPasswordCertificado(pwd)
                }

                val controller = FirmaController()
                controller.firmarDocumento(params)

                val nombreSalida = "${finalDoc.archivo.nameWithoutExtension}" +
                        "${finalDoc.sufijo}.pdf"
                val archivoSalida = File(finalDoc.rutaDestino, nombreSalida)

                ResultadoFirma(
                    archivoFirmado     = archivoSalida,
                    nombreArchivo      = nombreSalida,
                    aliasCertificado   = finalDoc.aliasCertificado,
                )
            }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error("Error al firmar: ${it.message}", it) },
            )
        }

    // ─── Validación ───────────────────────────────────────────────────────────
    
    /**
     * Valida las firmas de un PDF comparándolas contra la Trust Service List (TSL).
     * Extrae adicionalmente los certificados embebidos para enriquecer el resultado.
     *
     * @param archivoPdf Archivo físico a validar.
     * @return Result con el informe de validación consolidado.
     */
    override suspend fun validarDocumento(archivoPdf: File): Result<ResultadoValidacion> =
        withContext(Dispatchers.IO) {
            runCatching {
                val tslUrl = getTslUrl()
                
                val tsl = Tsl(tslUrl, 24 * 60 * 60 * 1000L,
                    /* verificar = */ true, context)

                @Suppress("UNCHECKED_CAST")
                val result = ValidacionController
                    .validarDocumento(context, archivoPdf, tsl)

                val certs = acj.soluciones.acjsignature.data.firma.PdfCertExtractor.extractCertificates(archivoPdf)
                result.toResultadoValidacion(certs)
            }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error("Error al validar: ${it.message}", it) },
            )
        }

    /**
     * Obtiene la URL de la TSL (Producción o Pruebas) según la configuración persistente.
     *
     * @return URL de la lista de servicios de confianza.
     */
    private suspend fun getTslUrl(): String {
        val config = configDataStore.configuracion.first()
        return if (config.usarTslPrueba) {
            "https://nodoyuna4.github.io/pki/tsl/tsl2026.xml"
        } else {
            "https://iofe.indecopi.gob.pe/TSL/tsl-pe.xml"
        }
    }

    /**
     * Elimina el archivo de certificado y sus credenciales asociadas.
     *
     * @param alias Identificador único del certificado.
     * @return Result indicando éxito o fracaso.
     */
    override suspend fun eliminarCertificado(alias: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                p12StorageManager.deleteCertificate(alias)
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error("Error al eliminar el certificado: ${it.message}", it) }
            )
        }
}