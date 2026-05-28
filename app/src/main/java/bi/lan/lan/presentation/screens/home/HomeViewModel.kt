package bi.lan.lan.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.Wallet
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WalletRepository) : ViewModel() {
    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadWallet() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = repository.getWallet()) {
                is NetworkResult.Success -> {
                    _wallet.value = res.data
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
