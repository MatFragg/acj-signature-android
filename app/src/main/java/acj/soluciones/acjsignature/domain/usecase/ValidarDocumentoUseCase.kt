package acj.soluciones.acjsignature.domain.usecase

import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.data.local.db.ValidacionDao
import acj.soluciones.acjsignature.data.local.db.ValidacionEntity
import acj.soluciones.acjsignature.shared.util.AppLogger
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
 * @property logger Logger para registrar auditoría del proceso.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
class ValidarDocumentoUseCase @Inject constructor(
    private val repository: FirmaRepository,
    private val validacionDao: ValidacionDao,
    private val logger: AppLogger
) {
    /**
     * Ejecuta la validación del archivo PDF y registra el resultado.
     *
     * @param archivoPdf El archivo PDF físico que se desea inspeccionar.
     * @return Result con el informe de validación consolidado.
     */
    suspend operator fun invoke(archivoPdf: File): Result<ResultadoValidacion> {
        if (!archivoPdf.exists()) {
            logger.error("Error validación: El archivo no existe en ${archivoPdf.path}")
            return Result.Error("El archivo no existe: ${archivoPdf.path}")
        }
        if (!archivoPdf.name.endsWith(".pdf", ignoreCase = true)) {
            logger.warning("Error validación: El archivo ${archivoPdf.name} no es un PDF")
            return Result.Error("El archivo debe ser un PDF.")
        }
        
        logger.info("Iniciando validación técnica de ${archivoPdf.name}")
        val result = repository.validarDocumento(archivoPdf)
        
        if (result is Result.Success) {
            val totalFirmas = result.data.firmas.size
            val firmasValidas = result.data.firmas.count { it.esValida }
            
            logger.info("Resultado validación ${archivoPdf.name}: $firmasValidas de $totalFirmas firmas válidas.")
            
            val jsonArray = JSONArray()
            result.data.firmas.forEach { firma ->
                val jsonObject = JSONObject().apply {
                    put("firmante", firma.firmante)
                    put("esValida", firma.esValida)
                    put("mensajeError", firma.mensajeError ?: "")
                }
                jsonArray.put(jsonObject)
                
                if (!firma.esValida) {
                    logger.warning("Firma inválida de ${firma.firmante}: ${firma.mensajeError}")
                }
            }
            
            val entity = ValidacionEntity(
                nombreDocumento = archivoPdf.name,
                esValido = result.data.esValido,
                totalFirmas = totalFirmas,
                firmasValidas = firmasValidas,
                resultadoJson = jsonArray.toString()
            )
            validacionDao.insert(entity)
            logger.debug("Historial de validación persistido para ${archivoPdf.name}")
        } else if (result is Result.Error) {
            logger.error("Fallo crítico en validación de ${archivoPdf.name}", result.cause)
        }
        
        return result
    }
}