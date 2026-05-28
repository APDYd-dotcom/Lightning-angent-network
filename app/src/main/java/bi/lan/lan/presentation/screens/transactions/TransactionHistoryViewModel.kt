package bi.lan.lan.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = repository.getTransactions()) {
                is NetworkResult.Success -> {
                    _transactions.value = res.data
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
