package acj.soluciones.acjsignature.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel que gestiona la lógica de la pantalla de inicio.
 * Provee un flujo de datos reactivo con los documentos más recientes.
 *
 * @property documentoRepository Repositorio para acceder al historial de documentos.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    documentoRepository: DocumentoRepository,
) : ViewModel() {


    val state = documentoRepository.getRecientes(5)
        .map { docs -> HomeState(documentosRecientes = docs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeState(isLoading = true),
        )
}
