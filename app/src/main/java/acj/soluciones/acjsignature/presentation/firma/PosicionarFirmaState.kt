package acj.soluciones.acjsignature.presentation.firma

import acj.soluciones.acjsignature.domain.model.Certificado
import android.graphics.Bitmap
import java.io.File

/**
 * Estado que gestiona la interactividad y previsualización del documento durante el posicionamiento de la firma.
 * Contiene información técnica del PDF, certificados disponibles y parámetros de personalización visual.
 *
 * @property documentoId Identificador del documento procesado.
 * @property archivoPdf Referencia al archivo físico en el almacenamiento interno.
 * @property totalPages Cantidad total de páginas detectadas en el documento.
 * @property currentPage Página que se está visualizando y donde se posicionará la firma (1-indexed).
 * @property currentPageBitmap Renderizado de la página actual para previsualización.
 * @property pdfPageWidth Ancho nativo de la página PDF actual.
 * @property pdfPageHeight Alto nativo de la página PDF actual.
 * @property certificadosDisponibles Lista de identidades digitales para firmar.
 * @property isLoading Indica carga inicial o cambio de página.
 * @property isSigning Indica que se está ejecutando el proceso criptográfico de firma.
 * @property error Mensaje descriptivo ante fallos técnicos o de validación.
 * @property firmaExitosa Flag que indica la finalización correcta del flujo.
 * @property logoUri URI del logo configurado para estampar en la firma.
 * @property firmaNombre Nombre completo del firmante extraído del certificado.
 * @property firmaCargo Cargo configurado para mostrar.
 * @property firmaEmpresa Razón social extraída o configurada.
 * @property includeCargo Indica si el cargo es visible en la firma.
 * @property includeEmpresa Indica si la empresa es visible en la firma.
 * @property includeTelefono Indica si se incluye contacto telefónico.
 * @property sigIdealWidth Ancho calculado para el sello de firma (en puntos PDF).
 * @property sigIdealHeight Alto calculado para el sello de firma (en puntos PDF).
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class PosicionarFirmaState(
    val documentoId: Long = 0,
    val archivoPdf: File? = null,
    val totalPages: Int = 1,
    val currentPage: Int = 1,
    val currentPageBitmap: Bitmap? = null,
    
    // Dimensiones nativas de la hoja PDF en la pagina actual
    val pdfPageWidth: Int = 0,
    val pdfPageHeight: Int = 0,

    val certificadosDisponibles: List<Certificado> = emptyList(),
    
    val isLoading: Boolean = true,
    val isSigning: Boolean = false,
    val error: String? = null,
    val firmaExitosa: Boolean = false,
    
    // Configuración visual desde DataStore
    val logoUri: String? = null,
    val firmaNombre: String = "",
    val firmaCargo: String = "",
    val firmaEmpresa: String = "",
    val includeCargo: Boolean = false,
    val includeEmpresa: Boolean = false,
    val includeTelefono: Boolean = false,
    
    // Dimensiones ideales calculadas para el sello (en unidades PDF)
    val sigIdealWidth: Int = 210,
    val sigIdealHeight: Int = 55,
)


