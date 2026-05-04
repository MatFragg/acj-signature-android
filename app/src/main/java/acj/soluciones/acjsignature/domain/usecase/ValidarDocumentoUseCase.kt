package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.data.local.db.ValidacionDao
import acj.soluciones.acjsignature.data.local.db.ValidacionEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

/**
 * Caso de Uso responsable de la validación integral de firmas digitales en documentos PDF.
 * Además de validar criptográficamente, persiste el historial de validación en la base de datos local.
 *
 * @property repository Repositorio de firma que provee la lógica de validación técnica.
 * @property validacionDao Acceso a datos para persistir el histórico de validaciones.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
class ValidarDocumentoUseCase @Inject constructor(
    private val repository: FirmaRepository,
    private val validacionDao: ValidacionDao
) {
    /**
     * Ejecuta la validación del archivo PDF y registra el resultado.
     *
     * @param archivoPdf El archivo PDF físico que se desea inspeccionar.
     * @return Result con el informe de validación consolidado.
     */
    suspend operator fun invoke(archivoPdf: File): Result<ResultadoValidacion> {
        if (!archivoPdf.exists()) {
            return Result.Error("El archivo no existe: ${archivoPdf.path}")
        }
        if (!archivoPdf.name.endsWith(".pdf", ignoreCase = true)) {
            return Result.Error("El archivo debe ser un PDF.")
        }
        val result = repository.validarDocumento(archivoPdf)
        if (result is Result.Success) {
            val totalFirmas = result.data.firmas.size
            val firmasValidas = result.data.firmas.count { it.esValida }
            
            val jsonArray = JSONArray()
            result.data.firmas.forEach { firma ->
                val jsonObject = JSONObject().apply {
                    put("firmante", firma.firmante)
                    put("esValida", firma.esValida)
                    put("mensajeError", firma.mensajeError ?: "")
                }
                jsonArray.put(jsonObject)
            }
            
            val entity = ValidacionEntity(
                nombreDocumento = archivoPdf.name,
                esValido = result.data.esValido,
                totalFirmas = totalFirmas,
                firmasValidas = firmasValidas,
                resultadoJson = jsonArray.toString()
            )
            validacionDao.insert(entity)
        }
        return result
    }
}