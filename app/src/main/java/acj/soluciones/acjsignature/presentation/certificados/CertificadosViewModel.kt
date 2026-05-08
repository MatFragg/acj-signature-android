package acj.soluciones.acjsignature.presentation.certificados

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.repository.FirmaRepository
import acj.soluciones.acjsignature.shared.domain.Result
import acj.soluciones.acjsignature.shared.util.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel que gestiona la lógica de administración de certificados digitales (.p12).
 * Permite listar identidades disponibles, importar nuevos archivos PKCS#12 con contraseña
 * y eliminar certificados existentes.
 *
 * @property firmaRepository Repositorio para la gestión de llaves y certificados.
 * @property logger Logger para registrar auditoría del proceso.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@HiltViewModel
class CertificadosViewModel @Inject constructor(
    private val firmaRepository: FirmaRepository,
    private val logger: AppLogger
) : ViewModel() {


    private val _state = MutableStateFlow(CertificadosState())
    val state = _state.asStateFlow()

    // Selected P12 file URI for import
    private var pendingP12Uri: Uri? = null
    private var pendingP12Bytes: ByteArray? = null

    init {
        logger.info("Cargando lista de certificados al inicializar.")
        cargarCertificados()
    }

    /**
     * Recupera la lista actualizada de certificados desde el repositorio.
     */
    fun cargarCertificados() {

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = firmaRepository.listarCertificados()) {
                is Result.Success -> {
                    logger.info("Se encontraron ${result.data.size} certificados.")
                    _state.update {
                        it.copy(certificados = result.data, isLoading = false)
                    }
                }
                is Result.Error -> {
                    logger.error("Error cargando certificados", result.cause)
                    _state.update {
                        it.copy(error = result.message, isLoading = false)
                    }
                }

                else -> {
                    _state.update {
                        it.copy(error = "Error desconocido", isLoading = false)
                    }
                }
            }
        }
    }

    /**
     * Maneja la selección de un archivo .p12 desde el selector de archivos.
     * @param uri URI del archivo seleccionado.
     * @param bytes Contenido binario del archivo.
     */
    fun onP12Selected(uri: Uri, bytes: ByteArray) {
        logger.info("Archivo .p12 seleccionado para importar: ${uri.path}")
        pendingP12Uri = uri
        pendingP12Bytes = bytes
        _state.update { it.copy(showPasswordDialog = true) }
    }

    /**
     * Procesa la contraseña ingresada para validar e importar el certificado .p12.
     * @param password Contraseña para abrir el archivo PKCS#12.
     */
    fun onPasswordConfirmed(password: String) {

        val bytes = pendingP12Bytes ?: return

        viewModelScope.launch {
            _state.update { it.copy(showPasswordDialog = false, isLoading = true) }
            logger.info("Validando contraseña del certificado...")

            // Verificar la contraseña y extraer el CN antes de pedir el PIN/importar
            val cn = withContext(Dispatchers.IO) {
                runCatching {
                    val ks = java.security.KeyStore.getInstance("PKCS12")
                    ks.load(java.io.ByteArrayInputStream(bytes), password.toCharArray())
                    
                    var foundCn: String? = null
                    val aliases = ks.aliases()
                    while (aliases.hasMoreElements()) {
                        val a = aliases.nextElement()
                        if (ks.isKeyEntry(a)) {
                            val cert = ks.getCertificate(a) as? java.security.cert.X509Certificate
                            if (cert != null) {
                                foundCn = com.acj.firma.lib.LibUtilitario.extractField(cert, "2.5.4.3")
                                break
                            }
                        }
                    }
                    foundCn ?: "Desconocido"
                }.getOrNull()
            }

            if (cn != null) {
                logger.info("Contraseña correcta. Iniciando importación.")
                val alias = "cert_${System.currentTimeMillis()}"
                logger.info("importando certificado $cn como $alias")
                when (val result = firmaRepository.importarCertificado(bytes, password, alias)) {
                    is Result.Success -> {
                        logger.info("Importación exitosa de $alias")
                        clearPendingState()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                importSuccess = "Certificado importado exitosamente",
                            )
                        }
                        cargarCertificados()
                    }
                    is Result.Error -> {
                        logger.error("Error en repositorio durante importación", result.cause)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                            )
                        }
                    }
                    else -> {
                        _state.update { it.copy(isLoading = false, error = "Error desconocido al importar") }
                    }
                }
            } else {
                logger.warning("Intento de importación fallido: Contraseña incorrecta.")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Contraseña incorrecta o archivo dañado.",
                    )
                }
            }
        }
    }

    /**
     * Cancela el proceso de importación actual y limpia los datos temporales.
     */
    fun onCancelImport() {
        logger.info("Importación cancelada por el usuario.")
        clearPendingState()
        _state.update { it.copy(showPasswordDialog = false) }
    }

    /**
     * Limpia los mensajes de error o éxito del estado.
     */
    fun clearMessages() {

        _state.update { it.copy(error = null, importSuccess = null) }
    }

    /**
     * Elimina permanentemente un certificado del almacenamiento seguro.
     * @param alias Identificador único del certificado a eliminar.
     */
    fun eliminarCertificado(alias: String) {
        logger.warning("Solicitud de eliminación para certificado: $alias")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = firmaRepository.eliminarCertificado(alias)) {
                is Result.Success -> {
                    logger.info("Certificado $alias eliminado correctamente.")
                    cargarCertificados()
                }
                is Result.Error -> {
                    logger.error("Error eliminando certificado $alias", result.cause)
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {
                    _state.update { it.copy(isLoading = false, error = "Error desconocido al eliminar") }
                }
            }
        }
    }

    private fun clearPendingState() {
        pendingP12Bytes = null
        pendingP12Uri = null
    }
}