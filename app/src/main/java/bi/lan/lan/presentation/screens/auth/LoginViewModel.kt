package bi.lan.lan.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun updatePhoneNumber(number: String) {
        _phoneNumber.value = number
    }

    fun login() {
        if (_phoneNumber.value.isBlank()) {
            _error.value = "Phone number cannot be empty"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val res = repository.login(_phoneNumber.value)) {
                is NetworkResult.Success -> {
                    _success.value = true
                }
                is NetworkResult.Error -> {
                    _error.value = res.message
                }
                is NetworkResult.Exception -> {
                    _error.value = res.e.message ?: "An error occurred"
                }
                is NetworkResult.Loading -> {}
            }
            _isLoading.value = false
        }
    }
}
