package bi.lan.lan.presentation.screens.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.domain.repository.RemittanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgentHomeViewModel(private val repo: LightningRepository) : ViewModel() {
    private val _health = MutableStateFlow<HealthResponse?>(null)
    val health: StateFlow<HealthResponse?> = _health
    
    private val _balance = MutableStateFlow<BalanceResponse?>(null)
    val balance: StateFlow<BalanceResponse?> = _balance
    
    private val _info = MutableStateFlow<NodeInfoResponse?>(null)
    val info: StateFlow<NodeInfoResponse?> = _info
    
    private val _recentInvoices = MutableStateFlow<List<InvoiceResponse>>(emptyList())
    val recentInvoices: StateFlow<List<InvoiceResponse>> = _recentInvoices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val r = repo.getHealth()) { is NetworkResult.Success -> _health.value = r.data; else -> {} }
            when (val r = repo.getBalance()) { is NetworkResult.Success -> _balance.value = r.data; else -> {} }
            when (val r = repo.getInfo()) { is NetworkResult.Success -> _info.value = r.data; else -> {} }
            when (val r = repo.getInvoices()) { 
                is NetworkResult.Success -> _recentInvoices.value = r.data.sortedByDescending { it.creationDate }.take(5)
                else -> {} 
            }
            _isLoading.value = false
        }
    }
}

class AgentDepositViewModel(private val repo: LightningRepository) : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount
    private val _memo = MutableStateFlow("Cash deposit via agent")
    val memo: StateFlow<String> = _memo
    private val _invoice = MutableStateFlow<CreateInvoiceResponse?>(null)
    val invoice: StateFlow<CreateInvoiceResponse?> = _invoice
    private val _invoiceStatus = MutableStateFlow<InvoiceResponse?>(null)
    val invoiceStatus: StateFlow<InvoiceResponse?> = _invoiceStatus
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun updateAmount(v: String) { _amount.value = v }
    fun updateMemo(v: String) { _memo.value = v }

    fun createInvoice() {
        val amt = _amount.value.toLongOrNull() ?: return
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            when (val r = repo.createInvoice(amt, _memo.value, 3600)) {
                is NetworkResult.Success -> _invoice.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun checkStatus() {
        val rHash = _invoice.value?.rHash ?: return
        viewModelScope.launch {
            _isLoading.value = true
            when (val r = repo.getInvoice(rHash)) {
                is NetworkResult.Success -> _invoiceStatus.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }
}

class AgentWithdrawalViewModel(
    private val agentRepo: LightningRepository,
    private val customerRepo: LightningRepository
) : ViewModel() {
    private val _payReq = MutableStateFlow("")
    val payReq: StateFlow<String> = _payReq
    private val _decoded = MutableStateFlow<DecodeInvoiceResponse?>(null)
    val decoded: StateFlow<DecodeInvoiceResponse?> = _decoded
    private val _payResult = MutableStateFlow<PaymentResponse?>(null)
    val payResult: StateFlow<PaymentResponse?> = _payResult
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _customerInvoices = MutableStateFlow<List<InvoiceResponse>>(emptyList())
    val customerInvoices: StateFlow<List<InvoiceResponse>> = _customerInvoices

    init {
        loadCustomerInvoices()
    }

    fun updatePayReq(v: String) { _payReq.value = v }

    fun loadCustomerInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val r = customerRepo.getInvoices()) {
                is NetworkResult.Success -> {
                    // Filter for unsettled/pending invoices
                    _customerInvoices.value = r.data.filter { !it.settled }
                }
                is NetworkResult.Error -> {
                    _error.value = "Failed to load customer invoices: ${r.message}"
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun selectInvoice(invoice: InvoiceResponse) {
        _payReq.value = invoice.paymentRequest
        decode()
    }

    fun decode() {
        if (_payReq.value.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            when (val r = agentRepo.decodeInvoice(_payReq.value)) {
                is NetworkResult.Success -> _decoded.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun confirmPayment() {
        if (_payReq.value.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            when (val r = agentRepo.payInvoice(_payReq.value, null)) {
                is NetworkResult.Success -> {
                    _payResult.value = r.data
                    // Refresh the pending invoices list after successful payment
                    loadCustomerInvoices()
                }
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _isLoading.value = false
        }
    }
}

class AgentTransactionsViewModel(
    private val repo: LightningRepository,
    private val remittanceRepo: RemittanceRepository
) : ViewModel() {
    private val _invoices = MutableStateFlow<List<InvoiceResponse>>(emptyList())
    val invoices: StateFlow<List<InvoiceResponse>> = _invoices
    private val _payments = MutableStateFlow<List<PaymentHistoryItem>>(emptyList())
    val payments: StateFlow<List<PaymentHistoryItem>> = _payments
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val remittances = remittanceRepo.getRemittances()

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
