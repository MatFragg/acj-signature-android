package acj.soluciones.acjsignature.domain.repository

import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion
import acj.soluciones.acjsignature.shared.domain.Result
import java.io.File

/**
 * Contrato de acceso a operaciones de firma digital.
 * Esta interfaz NO importa nada de acjfirmalib — el AAR sólo lo
 * conoce [FirmaRepositoryImpl] en la capa data.
 */
interface FirmaRepository {

    /**
     * Lista los certificados disponibles en el AndroidKeyStore
     * que tengan el bit de no-repudio activo.
     */
    suspend fun listarCertificados(): Result<List<Certificado>>

    /**
     * Importa un certificado a la caja fuerte de la app.
     */
    suspend fun importarCertificado(bytes: ByteArray, password: String, alias: String): Result<Unit>

    /**
     * Firma el documento descrito en [documentoFirma] y devuelve
     * la referencia al archivo resultante.
     */
    suspend fun firmarDocumento(documentoFirma: DocumentoFirma): Result<ResultadoFirma>

    /**
     * Valida todas las firmas contenidas en [archivoPdf] y devuelve
     * el resultado de validación consolidado.
     */
    suspend fun validarDocumento(archivoPdf: File): Result<ResultadoValidacion>
}