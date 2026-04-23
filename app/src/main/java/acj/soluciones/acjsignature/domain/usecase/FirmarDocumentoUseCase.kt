package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoFirma
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import javax.inject.Inject

class FirmarDocumentoUseCase @Inject constructor(
    private val repository: FirmaRepository,
) {
    suspend operator fun invoke(documentoFirma: DocumentoFirma): Result<ResultadoFirma> {
        if (!documentoFirma.archivo.exists()) {
            return Result.Error("El archivo no existe: ${documentoFirma.archivo.path}")
        }
        if (!documentoFirma.archivo.name.endsWith(".pdf", ignoreCase = true)) {
            return Result.Error("El archivo debe ser un PDF.")
        }
        if (documentoFirma.aliasCertificado.isBlank()) {
            return Result.Error("Debe seleccionar un certificado.")
        }
        return repository.firmarDocumento(documentoFirma)
    }
}