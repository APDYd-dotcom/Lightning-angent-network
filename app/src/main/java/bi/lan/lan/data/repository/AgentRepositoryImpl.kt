package bi.lan.lan.data.repository

import bi.lan.lan.data.api.ApiClient
import bi.lan.lan.data.api.LightningApiService
import bi.lan.lan.data.model.Agent
import bi.lan.lan.data.remote.BaseRepository
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.AgentRepository
import kotlinx.coroutines.delay

class AgentRepositoryImpl(
    private val apiService: LightningApiService
) : BaseRepository(), AgentRepository {

    override suspend fun getNearbyAgents(): NetworkResult<List<Agent>> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            NetworkResult.Success(
                listOf(
                    Agent("1", "Musa Agent", "Kigali Center", 1.2, true),
                    Agent("2", "Amina K.", "Nyamirambo", 2.5, false),
                    Agent("3", "Fast Cash LND", "Kimironko", 3.0, true)
                )
            )
        } else {
            safeApiCall {
                apiService.getNearbyAgents().data ?: emptyList()
            }
        }
    }

    override suspend fun getAgentDashboard(): NetworkResult<Agent> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            NetworkResult.Success(Agent("1", "Musa Agent", "Kigali Center", 0.0, true))
        } else {
            safeApiCall {
                val res = apiService.getAgentDashboard()
                res.data ?: throw Exception("Failed to load dashboard")
            }
        }
    }
}
