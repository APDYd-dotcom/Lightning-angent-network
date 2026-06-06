package bi.lan.lan.presentation.screens.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CustomerHomeViewModel(private val repo: LightningRepository) : ViewModel() {
    private val _health = MutableStateFlow<HealthResponse?>(null)
    val health: StateFlow<HealthResponse?> = _health
    private val _balance = MutableStateFlow<BalanceResponse?>(null)
    val balance: StateFlow<BalanceResponse?> = _balance
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val r = repo.getHealth()) { is NetworkResult.Success -> _health.value = r.data; else -> {} }
            when (val r = repo.getBalance()) { is NetworkResult.Success -> _balance.value = r.data; else -> {} }
            _isLoading.value = false
        }
    }
}

class CustomerInvoiceViewModel(private val repo: LightningRepository) : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount
    private val _memo = MutableStateFlow("")
    val memo: StateFlow<String> = _memo
    private val _expiry = MutableStateFlow("3600")
    val expiry: StateFlow<String> = _expiry
    private val _result = MutableStateFlow<CreateInvoiceResponse?>(null)
    val result: StateFlow<CreateInvoiceResponse?> = _result
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Invoice status
    private val _rHashQuery = MutableStateFlow("")
    val rHashQuery: StateFlow<String> = _rHashQuery
    private val _invoiceStatus = MutableStateFlow<InvoiceResponse?>(null)
    val invoiceStatus: StateFlow<InvoiceResponse?> = _invoiceStatus

    fun updateAmount(v: String) { _amount.value = v }
    fun updateMemo(v: String) { _memo.value = v }
    fun updateExpiry(v: String) { _expiry.value = v }
    fun updateRHashQuery(v: String) { _rHashQuery.value = v }

    fun createInvoice() {
        val amt = _amount.value.toLongOrNull() ?: return
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            when (val r = repo.createInvoice(amt, _memo.value, _expiry.value.toLongOrNull() ?: 3600)) {
                is NetworkResult.Success -> _result.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun checkInvoiceStatus() {
        if (_rHashQuery.value.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            when (val r = repo.getInvoice(_rHashQuery.value)) {
                is NetworkResult.Success -> _invoiceStatus.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }
}

class CustomerPaymentViewModel(
    private val customerRepo: LightningRepository,
    private val agentRepo: LightningRepository,
    private val remittanceRepo: bi.lan.lan.domain.repository.RemittanceRepository
) : ViewModel() {
    private val _payReq = MutableStateFlow("")
    val payReq: StateFlow<String> = _payReq
    
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount
    
    private val _result = MutableStateFlow<PaymentResponse?>(null)
    val result: StateFlow<PaymentResponse?> = _result
    
    private val _decoded = MutableStateFlow<DecodeInvoiceResponse?>(null)
    val decoded: StateFlow<DecodeInvoiceResponse?> = _decoded
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _agentInvoices = MutableStateFlow<List<InvoiceResponse>>(emptyList())
    val agentInvoices: StateFlow<List<InvoiceResponse>> = _agentInvoices

    // New state for success screen
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    init {
        loadAgentInvoices()
    }

    fun updatePayReq(v: String) { 
        _payReq.value = v 
        if (v.startsWith("lnbc", ignoreCase = true)) {
            decodeInvoice()
        }
    }
    
    fun updateAmount(v: String) { _amount.value = v }

    fun loadAgentInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val r = agentRepo.getInvoices()) {
                is NetworkResult.Success -> {
                    _agentInvoices.value = r.data.filter { !it.settled }
                }
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun selectInvoice(invoice: InvoiceResponse) {
        _payReq.value = invoice.paymentRequest
        decodeInvoice()
    }

    fun payInvoice() {
        if (_payReq.value.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            val amt = _amount.value.toLongOrNull() ?: _decoded.value?.numSatoshis ?: 0L
            val desc = _decoded.value?.description ?: "Payment"
            when (val r = customerRepo.payInvoice(_payReq.value, if (amt > 0) amt else null)) {
                is NetworkResult.Success -> {
                    _result.value = r.data
                    _isSuccess.value = true
                    // Track in local history
                    remittanceRepo.trackOutboundPayment(
                        paymentRequest = _payReq.value,
                        amount = amt,
                        description = desc,
                        transactionId = r.data.paymentHash
                    )
                    loadAgentInvoices()
                }
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun decodeInvoice() {
        if (_payReq.value.isBlank()) {
            _error.value = "Invalid Lightning Invoice: Invoice cannot be empty"
            return
        }
        if (!_payReq.value.lowercase().startsWith("lnbc")) {
            _error.value = "Invalid Lightning Invoice: Must start with 'lnbc'"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            _decoded.value = null
            try {
                when (val r = customerRepo.decodeInvoice(_payReq.value)) {
                    is NetworkResult.Success -> _decoded.value = r.data
                    is NetworkResult.Error -> _error.value = r.message
                    else -> {}
                }
            } catch (e: Exception) {
                _error.value = "Error decoding invoice: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reset() {
        _payReq.value = ""
        _amount.value = ""
        _decoded.value = null
        _result.value = null
        _isSuccess.value = false
        _error.value = null
    }
}

class CustomerTransactionsViewModel(private val repo: LightningRepository) : ViewModel() {
    private val _invoices = MutableStateFlow<List<InvoiceResponse>>(emptyList())
    val invoices: StateFlow<List<InvoiceResponse>> = _invoices
    private val _payments = MutableStateFlow<List<PaymentHistoryItem>>(emptyList())
    val payments: StateFlow<List<PaymentHistoryItem>> = _payments
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionLoading = MutableStateFlow(false)
    val actionLoading: StateFlow<Boolean> = _actionLoading

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val r = repo.getInvoices()) { is NetworkResult.Success -> _invoices.value = r.data.sortedByDescending { it.creationDate }; else -> {} }
            when (val r = repo.getPayments()) { is NetworkResult.Success -> _payments.value = r.data.sortedByDescending { it.creationDate }; else -> {} }
            _isLoading.value = false
        }
    }

    fun checkInvoiceStatus(rHash: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _actionLoading.value = true
            when (val r = repo.getInvoice(rHash)) {
                is NetworkResult.Success -> {
                    if (r.data.settled) {
                        // Refresh list if it was settled
                        load()
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
                else -> onResult(false)
            }
            _actionLoading.value = false
        }
    }
}

class NodeInfoViewModel(
    private val repo: LightningRepository,
    private val remittanceRepo: bi.lan.lan.domain.repository.RemittanceRepository
) : ViewModel() {
    private val _info = MutableStateFlow<NodeInfoResponse?>(null)
    val info: StateFlow<NodeInfoResponse?> = _info
    
    private val _balance = MutableStateFlow<BalanceResponse?>(null)
    val balance: StateFlow<BalanceResponse?> = _balance
    
    private val _accountDetails = MutableStateFlow<bi.lan.lan.data.remote.blink.BlinkMe?>(null)
    val accountDetails: StateFlow<bi.lan.lan.data.remote.blink.BlinkMe?> = _accountDetails

    private val _totalTransactions = MutableStateFlow(0)
    val totalTransactions: StateFlow<Int> = _totalTransactions
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val r = repo.getInfo()) { is NetworkResult.Success -> _info.value = r.data; else -> {} }
            when (val r = repo.getBalance()) { is NetworkResult.Success -> _balance.value = r.data; else -> {} }
            when (val r = remittanceRepo.getAccountInfo()) { is NetworkResult.Success -> _accountDetails.value = r.data; else -> {} }
            
            // Count local transactions
            remittanceRepo.getRemittances().first().let { 
                _totalTransactions.value = it.size
            }

            _isLoading.value = false
        }
    }
}
