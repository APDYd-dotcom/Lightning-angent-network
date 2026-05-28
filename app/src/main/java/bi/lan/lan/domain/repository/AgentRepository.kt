package bi.lan.lan.domain.repository

import bi.lan.lan.data.model.Agent
import bi.lan.lan.data.remote.NetworkResult

interface AgentRepository {
    suspend fun getNearbyAgents(): NetworkResult<List<Agent>>
    suspend fun getAgentDashboard(): NetworkResult<Agent>
}
