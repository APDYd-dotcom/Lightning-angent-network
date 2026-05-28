package bi.lan.lan.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.lan.lan.data.model.Agent
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.AgentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NearbyAgentsViewModel(private val repository: AgentRepository) : ViewModel() {
    private val _agents = MutableStateFlow<List<Agent>>(emptyList())
    val agents: StateFlow<List<Agent>> = _agents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadAgents() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = repository.getNearbyAgents()) {
                is NetworkResult.Success -> {
                    _agents.value = res.data
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
