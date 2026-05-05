package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoFirma
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.shared.util.AppLogger
import javax.inject.Inject

/**
 * Caso de Uso encargado de orquestar y validar las condiciones previas para la firma de un documento.
 * Verifica la existencia del archivo, su formato y la selección de un certificado válido.
 *
 * @property repository Repositorio de firma digital que ejecutará la operación técnica.
 * @property logger Logger para registrar auditoría del proceso.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
class FirmarDocumentoUseCase @Inject constructor(
    private val repository: FirmaRepository,
    private val logger: AppLogger
) {
    /**
     * Ejecuta el proceso de validación y firma del documento.
     *
     * @param documentoFirma Objeto con la configuración y el archivo a firmar.
     * @return Result con el resultado de la firma o un mensaje de error detallado.
     */
    suspend operator fun invoke(documentoFirma: DocumentoFirma): Result<ResultadoFirma> {
        if (!documentoFirma.archivo.exists()) {
            logger.error("Error pre-firma: El archivo no existe en ${documentoFirma.archivo.path}")
            return Result.Error("El archivo no existe: ${documentoFirma.archivo.path}")
        }
        if (!documentoFirma.archivo.name.endsWith(".pdf", ignoreCase = true)) {
            logger.warning("Error pre-firma: El archivo ${documentoFirma.archivo.name} no es un PDF")
            return Result.Error("El archivo debe ser un PDF.")
        }
        if (documentoFirma.aliasCertificado.isBlank()) {
            logger.warning("Error pre-firma: No se ha seleccionado un certificado")
            return Result.Error("Debe seleccionar un certificado.")
        }
        return repository.firmarDocumento(documentoFirma)
    }
}