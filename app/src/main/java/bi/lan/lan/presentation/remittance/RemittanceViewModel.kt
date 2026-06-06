package bi.lan.lan.presentation.remittance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.RemittanceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RemittanceUiState {
    object Idle : RemittanceUiState()
    object Loading : RemittanceUiState()
    data class Created(val remittance: RemittanceEntity) : RemittanceUiState()
    data class Paid(val remittance: RemittanceEntity) : RemittanceUiState()
    data class Error(val message: String) : RemittanceUiState()
}

class RemittanceViewModel(private val repository: RemittanceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RemittanceUiState>(RemittanceUiState.Idle)
    val uiState: StateFlow<RemittanceUiState> = _uiState.asStateFlow()

    private var statusJob: Job? = null

    fun createRemittanceRequest(amount: Long, description: String) {
        viewModelScope.launch {
            _uiState.value = RemittanceUiState.Loading
            when (val result = repository.createRemittanceRequest(amount, description)) {
                is NetworkResult.Success -> {
                    _uiState.value = RemittanceUiState.Created(result.data)
                    startStatusPolling(result.data.reference)
                }
                is NetworkResult.Error -> {
                    _uiState.value = RemittanceUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    private fun startStatusPolling(reference: String) {
        statusJob?.cancel()
        statusJob = viewModelScope.launch {
            while (true) {
                delay(5000) // Poll every 5 seconds
                val result = repository.checkRemittanceStatus(reference)
                if (result is NetworkResult.Success) {
                    when (result.data.status) {
                        "PAID" -> {
                            _uiState.value = RemittanceUiState.Paid(result.data)
                            break
                        }
                        "FAILED", "EXPIRED" -> {
                            _uiState.value = RemittanceUiState.Error("Payment ${result.data.status.lowercase()}")
                            break
                        }
                    }
                }
            }
        }
    }

    fun reset() {
        statusJob?.cancel()
        _uiState.value = RemittanceUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        statusJob?.cancel()
    }
}
