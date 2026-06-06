package bi.lan.lan.presentation.remittance

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.core.utils.ReceiptGenerator
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.domain.repository.RemittanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReceiptViewModel(private val repository: RemittanceRepository) : ViewModel() {

    private val _remittance = MutableStateFlow<RemittanceEntity?>(null)
    val remittance: StateFlow<RemittanceEntity?> = _remittance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadRemittance(reference: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val entity = repository.getRemittanceByReference(reference)
            _remittance.value = entity
            _isLoading.value = false
        }
    }

    fun generateReceiptUri(context: Context, onResult: (Uri?) -> Unit) {
        val current = _remittance.value ?: return
        viewModelScope.launch {
            val uri = ReceiptGenerator.generateReceipt(
                context = context,
                amount = current.amount,
                reference = current.reference,
                txId = current.transactionId ?: "N/A",
                date = current.paidAt ?: current.createdAt,
                status = current.status
            )
            onResult(uri)
        }
    }
}
