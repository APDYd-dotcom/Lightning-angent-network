package bi.lan.lan.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentDetailViewModel(private val repo: LightningRepository) : ViewModel() {
    private val _payment = MutableStateFlow<PaymentHistoryItem?>(null)
    val payment: StateFlow<PaymentHistoryItem?> = _payment

    private val _decoded = MutableStateFlow<DecodeInvoiceResponse?>(null)
    val decoded: StateFlow<DecodeInvoiceResponse?> = _decoded

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPayment(hash: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val payments = repo.getPayments()
            if (payments is NetworkResult.Success) {
                val item = payments.data.find { it.paymentHash == hash }
                _payment.value = item
                
                item?.paymentRequest?.let { req ->
                    if (req.isNotBlank()) {
                        val d = repo.decodeInvoice(req)
                        if (d is NetworkResult.Success) {
                            _decoded.value = d.data
                        }
                    }
                }
            }
            _isLoading.value = false
        }
    }
}
