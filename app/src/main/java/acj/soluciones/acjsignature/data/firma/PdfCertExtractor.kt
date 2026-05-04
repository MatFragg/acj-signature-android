package acj.soluciones.acjsignature.data.firma

import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.PDSignature
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cms.CMSSignedData
import java.io.File
import java.security.cert.X509Certificate

/**
 * Utilidad para la extracción de certificados digitales embebidos en archivos PDF.
 * Utiliza Apache PDFBox y BouncyCastle para analizar los diccionarios de firma del documento.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
object PdfCertExtractor {
    init {
        try {
            java.security.Security.removeProvider("BC")
        } catch (e: Exception) {
            // Ignorar
        }
        java.security.Security.insertProviderAt(org.bouncycastle.jce.provider.BouncyCastleProvider(), 1)
    }

    /**
     * Analiza un archivo PDF y extrae los certificados X.509 asociados a cada firma encontrada.
     *
     * @param file El archivo PDF físico del cual extraer los certificados.
     * @return Un mapa donde la clave es el nombre de la firma y el valor es el certificado X509.
     */
    fun extractCertificates(file: File): Map<String, X509Certificate> {
        val certsMap = mutableMapOf<String, X509Certificate>()
        try {
            PDDocument.load(file).use { document ->
                val signatures = document.signatureDictionaries
                for (signature in signatures) {
                    try {
                        val contents = signature.contents
                        if (contents != null) {
                            val signedData = CMSSignedData(contents)
                            val certStore = signedData.certificates
                            val signers = signedData.signerInfos.signers

                            for (signer in signers) {
                                val certCollection = certStore.getMatches(null)
                                val certHolder = certCollection.firstOrNull() as? X509CertificateHolder
                                if (certHolder != null) {
                                    val cert = JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder)
                                    certsMap[signature.name ?: "Unknown"] = cert
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("PdfCertExtractor", "Error extracting from signature", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PdfCertExtractor", "Error loading PDF", e)
        }
        return certsMap
    }
}
