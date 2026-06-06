package bi.lan.lan.presentation.remittance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.domain.repository.RemittanceRepository
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.model.BalanceResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val repository: RemittanceRepository,
    private val lightningRepo: LightningRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    
    private val _balance = MutableStateFlow<BalanceResponse?>(null)
    val balance = _balance.asStateFlow()

    // Retrieve all remittances to perform in-memory computations
    private val allRemittances = repository.getRemittances()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshPendingRemittances()
        loadBalance()
    }

    fun loadBalance() {
        viewModelScope.launch {
            when (val r = lightningRepo.getBalance()) {
                is NetworkResult.Success -> _balance.value = r.data
                else -> {}
            }
        }
    }

    val todayReceivedTotal: Flow<Long> = allRemittances.map { list ->
        val startOfToday = getStartOfToday()
        list.filter { it.status.uppercase() == "PAID" && it.paidAt ?: it.createdAt >= startOfToday }
            .sumOf { it.amount }
    }

    val monthReceivedTotal: Flow<Long> = allRemittances.map { list ->
        val startOfThisMonth = getStartOfThisMonth()
        list.filter { it.status.uppercase() == "PAID" && it.paidAt ?: it.createdAt >= startOfThisMonth }
            .sumOf { it.amount }
    }

    val pendingStats: Flow<Pair<Int, Long>> = allRemittances.map { list ->
        val pending = list.filter { it.status.uppercase() == "PENDING" }
        pending.size to pending.sumOf { it.amount }
    }

    val successStats: Flow<Pair<Int, Long>> = allRemittances.map { list ->
        val success = list.filter { it.status.uppercase() == "PAID" }
        success.size to success.sumOf { it.amount }
    }

    val failedStats: Flow<Pair<Int, Long>> = allRemittances.map { list ->
        val failed = list.filter { it.status.uppercase() == "FAILED" || it.status.uppercase() == "EXPIRED" }
        failed.size to failed.sumOf { it.amount }
    }

    val totalStats: Flow<Pair<Int, Long>> = allRemittances.map { list ->
        list.size to list.sumOf { it.amount }
    }

    val recentRemittances: Flow<List<RemittanceEntity>> = allRemittances.map { list ->
        list.take(5)
    }

    fun refreshPendingRemittances() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val pendingList = allRemittances.value.filter { it.status.uppercase() == "PENDING" }
            for (remittance in pendingList) {
                repository.checkRemittanceStatus(remittance.reference)
            }
            _isRefreshing.value = false
        }
    }

    private fun getStartOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfThisMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
