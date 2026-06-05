package bi.lan.lan.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class InvoiceCreated(val invoice: String) : PaymentState()
    data class PaymentSuccess(val paymentHash: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel(
    private val createInvoiceUseCase: CreateInvoiceUseCase,
    private val payInvoiceUseCase: PayInvoiceUseCase,
    private val getBalanceUseCase: GetBalanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> = _balance.asStateFlow()

    init {
        fetchBalance()
    }

    fun fetchBalance() {
        viewModelScope.launch {
            when (val result = getBalanceUseCase()) {
                is NetworkResult.Success -> _balance.value = result.data
                is NetworkResult.Error -> {
                    // Fallback for demo
                    _balance.value = 0
                }
                else -> {}
            }
        }
    }

    fun createInvoice(amount: Long, memo: String) {
        if (amount <= 0) {
            _state.value = PaymentState.Error("Amount must be greater than 0")
            return
        }
        viewModelScope.launch {
            _state.value = PaymentState.Loading
            try {
                when (val result = createInvoiceUseCase(amount, memo)) {
                    is NetworkResult.Success -> _state.value = PaymentState.InvoiceCreated(result.data)
                    is NetworkResult.Error -> _state.value = PaymentState.Error(result.message)
                    else -> {}
                }
            } catch (e: Exception) {
                _state.value = PaymentState.Error(e.message ?: "Unknown error creating invoice")
            }
        }
    }

    fun payInvoice(paymentRequest: String) {
        if (paymentRequest.isBlank()) {
            _state.value = PaymentState.Error("Invoice cannot be empty")
            return
        }
        viewModelScope.launch {
            _state.value = PaymentState.Loading
            try {
                when (val result = payInvoiceUseCase(paymentRequest)) {
                    is NetworkResult.Success -> {
                        _state.value = PaymentState.PaymentSuccess(result.data)
                        fetchBalance()
                    }
                    is NetworkResult.Error -> _state.value = PaymentState.Error(result.message)
                    else -> {}
                }
            } catch (e: Exception) {
                _state.value = PaymentState.Error(e.message ?: "Unknown error paying invoice")
            }
        }
    }

    fun resetState() {
        _state.value = PaymentState.Idle
    }
}
