package bi.lan.lan.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.User
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OtpViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun updateOtp(code: String) {
        _otp.value = code
    }

    fun verifyOtp(phoneNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val res = repository.verifyOtp(phoneNumber, _otp.value)) {
                is NetworkResult.Success -> {
                    _user.value = res.data
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
