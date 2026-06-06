package bi.lan.lan.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.domain.repository.RemittanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: RemittanceRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("ALL") // ALL, PAID, PENDING, FAILED
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _dateFilter = MutableStateFlow("ALL") // ALL, TODAY, WEEK, MONTH
    val dateFilter: StateFlow<String> = _dateFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("DATE_DESC") // DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    val remittances: StateFlow<List<RemittanceEntity>> = combine(
        repository.getRemittances(),
        _searchQuery,
        _statusFilter,
        _dateFilter,
        _sortBy
    ) { list, query, status, dateF, sort ->
        var filtered = list

        // Search Query
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.reference.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        // Status Filter
        if (status != "ALL") {
            filtered = filtered.filter {
                if (status == "FAILED") {
                    it.status.uppercase() != "PAID" && it.status.uppercase() != "PENDING"
                } else {
                    it.status.uppercase() == status
                }
            }
        }

        // Date Filter
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        when (dateF) {
            "TODAY" -> {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                val startOfToday = calendar.timeInMillis
                filtered = filtered.filter { it.createdAt >= startOfToday }
            }
            "WEEK" -> {
                val oneWeekAgo = now - (7L * 24L * 60L * 60L * 1000L)
                filtered = filtered.filter { it.createdAt >= oneWeekAgo }
            }
            "MONTH" -> {
                val oneMonthAgo = now - (30L * 24L * 60L * 60L * 1000L)
                filtered = filtered.filter { it.createdAt >= oneMonthAgo }
            }
        }

        // Sorting
        filtered = when (sort) {
            "DATE_DESC" -> filtered.sortedByDescending { it.createdAt }
            "DATE_ASC" -> filtered.sortedBy { it.createdAt }
            "AMOUNT_DESC" -> filtered.sortedByDescending { it.amount }
            "AMOUNT_ASC" -> filtered.sortedBy { it.amount }
            else -> filtered.sortedByDescending { it.createdAt }
        }

        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: String) {
        _statusFilter.value = status
    }

    fun onDateFilterChange(filter: String) {
        _dateFilter.value = filter
    }

    fun onSortChange(sort: String) {
        _sortBy.value = sort
    }

    fun checkStatus(reference: String) {
        viewModelScope.launch {
            repository.checkRemittanceStatus(reference)
        }
    }
}
