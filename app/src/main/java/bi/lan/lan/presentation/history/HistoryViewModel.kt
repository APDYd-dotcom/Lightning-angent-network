package bi.lan.lan.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.domain.repository.RemittanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class HistoryViewModel(private val repository: RemittanceRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val remittances: StateFlow<List<RemittanceEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getRemittances()
            } else {
                repository.searchRemittances(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun checkStatus(reference: String) {
        viewModelScope.launch {
            repository.checkRemittanceStatus(reference)
        }
    }
}
