package acj.soluciones.acjsignature.presentation.firma

import acj.soluciones.acjsignature.domain.model.Certificado
import android.graphics.Bitmap
import java.io.File

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

    // PIN verification dialog state
    val showPinDialog: Boolean = false,
    val pinError: String? = null,
)

