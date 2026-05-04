package acj.soluciones.acjsignature.domain.repository

import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion
import acj.soluciones.acjsignature.shared.domain.Result
import java.io.File

/**
 * Contrato que define las operaciones de alto nivel para la gestión de certificados y procesos de firma digital.
 * Esta interfaz actúa como un puente hacia la implementación técnica de firma en la capa de datos.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
interface FirmaRepository {

    /**
     * Lista los certificados digitales disponibles en el almacenamiento seguro del dispositivo.
     * Filtra aquellos que cumplen con el propósito de firma (no repudio).
     *
     * @return Result con la lista de objetos Certificado encontrados.
     */
    suspend fun listarCertificados(): Result<List<Certificado>>

    /**
     * Importa un certificado digital desde un archivo de bytes hacia el almacén seguro.
     *
     * @param bytes Contenido binario del archivo .p12 o .pfx.
     * @param password Contraseña de acceso al archivo de certificado.
     * @param alias Nombre único que se le asignará al certificado importado.
     * @return Result indicando el éxito o fallo de la importación.
     */
    suspend fun importarCertificado(bytes: ByteArray, password: String, alias: String): Result<Unit>

    /**
     * Ejecuta el proceso de firma digital sobre un documento PDF.
     *
     * @param documentoFirma Objeto con los parámetros y el archivo a firmar.
     * @return Result con el objeto ResultadoFirma que contiene el PDF firmado.
     */
    suspend fun firmarDocumento(documentoFirma: DocumentoFirma): Result<ResultadoFirma>

    /**
     * Realiza la validación técnica y criptográfica de las firmas digitales en un PDF.
     *
     * @param archivoPdf El archivo PDF físico que se desea validar.
     * @return Result con el informe detallado de validación.
     */
    suspend fun validarDocumento(archivoPdf: File): Result<ResultadoValidacion>

    /**
     * Elimina permanentemente un certificado del almacenamiento seguro.
     *
     * @param alias Identificador único del certificado a eliminar.
     * @return Result indicando el resultado de la eliminación.
     */
    suspend fun eliminarCertificado(alias: String): Result<Unit>
}