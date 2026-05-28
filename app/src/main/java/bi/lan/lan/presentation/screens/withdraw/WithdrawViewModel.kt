package bi.lan.lan.presentation.screens.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.WithdrawRequest
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.WithdrawRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WithdrawViewModel(private val repository: WithdrawRepository) : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun updateAmount(amt: String) {
        _amount.value = amt
    }

    fun requestWithdrawal(agentId: String) {
        val amt = _amount.value.toLongOrNull() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            when (repository.requestWithdrawal(WithdrawRequest(agentId, amt))) {
                is NetworkResult.Success -> {
                    _success.value = true
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
