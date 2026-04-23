package acj.soluciones.acjsignature.presentation.validacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import acj.soluciones.acjsignature.domain.repository.DocumentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ValidacionViewModel @Inject constructor(
    private val documentoRepository: DocumentoRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val documentos = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) documentoRepository.getDocumentos()
        else documentoRepository.buscar(query)
    }

    val state = combine(
        documentos,
        documentoRepository.getEstadisticas(),
        _searchQuery,
    ) { docs, stats, query ->
        ValidacionState(
            documentos = docs,
            estadisticas = stats,
            searchQuery = query,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ValidacionState(isLoading = true),
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}