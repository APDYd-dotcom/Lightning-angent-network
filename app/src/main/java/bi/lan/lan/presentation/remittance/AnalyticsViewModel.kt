package bi.lan.lan.presentation.remittance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.domain.repository.RemittanceRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel(private val repository: RemittanceRepository) : ViewModel() {

    private val allRemittances = repository.getRemittances()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: Flow<Int> = allRemittances.map { it.size }

    val totalVolume: Flow<Long> = allRemittances.map { list ->
        list.filter { it.status.uppercase() == "PAID" }.sumOf { it.amount }
    }

    val averageAmount: Flow<Double> = allRemittances.map { list ->
        val paidList = list.filter { it.status.uppercase() == "PAID" }
        if (paidList.isEmpty()) 0.0 else paidList.map { it.amount }.average()
    }

    val largestAmount: Flow<Long> = allRemittances.map { list ->
        val paidList = list.filter { it.status.uppercase() == "PAID" }
        if (paidList.isEmpty()) 0L else paidList.maxOf { it.amount }
    }

    // Returns a list of month names and their corresponding volume (sats)
    val monthlyVolume: Flow<List<Pair<String, Long>>> = allRemittances.map { list ->
        val paidList = list.filter { it.status.uppercase() == "PAID" }
        val format = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        
        val grouped = paidList.groupBy {
            format.format(Date(it.paidAt ?: it.createdAt))
        }

        grouped.map { (month, items) ->
            month to items.sumOf { it.amount }
        }.sortedBy { (monthStr, _) ->
            try {
                format.parse(monthStr) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        }
    }

    // Returns status and count (PAID, PENDING, FAILED)
    val statusDistribution: Flow<Map<String, Int>> = allRemittances.map { list ->
        list.groupBy {
            val status = it.status.uppercase()
            if (status != "PAID" && status != "PENDING") "FAILED" else status
        }.mapValues { it.value.size }
    }
}
