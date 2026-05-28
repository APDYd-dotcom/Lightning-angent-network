package bi.lan.lan.presentation.screens.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.DepositRequest
import bi.lan.lan.data.model.PaymentInvoice
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.DepositRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DepositViewModel(private val repository: DepositRepository) : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _invoice = MutableStateFlow<PaymentInvoice?>(null)
    val invoice: StateFlow<PaymentInvoice?> = _invoice

    fun updateAmount(amt: String) {
        _amount.value = amt
    }

    fun createDeposit(agentId: String) {
        val amt = _amount.value.toLongOrNull() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = repository.createDeposit(DepositRequest(agentId, amt))) {
                is NetworkResult.Success -> {
                    _invoice.value = res.data
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
