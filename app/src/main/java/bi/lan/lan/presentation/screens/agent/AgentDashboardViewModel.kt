package bi.lan.lan.presentation.screens.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.Agent
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.AgentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgentDashboardViewModel(private val repository: AgentRepository) : ViewModel() {
    private val _agent = MutableStateFlow<Agent?>(null)
    val agent: StateFlow<Agent?> = _agent

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = repository.getAgentDashboard()) {
                is NetworkResult.Success -> {
                    _agent.value = res.data
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
