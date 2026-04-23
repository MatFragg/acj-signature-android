package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import java.io.File
import javax.inject.Inject

class ValidarDocumentoUseCase @Inject constructor(
    private val repository: FirmaRepository,
) {
    suspend operator fun invoke(archivoPdf: File): Result<ResultadoValidacion> {
        if (!archivoPdf.exists()) {
            return Result.Error("El archivo no existe: ${archivoPdf.path}")
        }
        if (!archivoPdf.name.endsWith(".pdf", ignoreCase = true)) {
            return Result.Error("El archivo debe ser un PDF.")
        }
        return repository.validarDocumento(archivoPdf)
    }
}