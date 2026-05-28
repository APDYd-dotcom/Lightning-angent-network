package bi.lan.lan.data.repository

import bi.lan.lan.data.api.ApiClient
import bi.lan.lan.data.api.LightningApiService
import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.model.WithdrawRequest
import bi.lan.lan.data.remote.BaseRepository
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.WithdrawRepository
import kotlinx.coroutines.delay

class WithdrawRepositoryImpl(
    private val apiService: LightningApiService
) : BaseRepository(), WithdrawRepository {

    override suspend fun requestWithdrawal(request: WithdrawRequest): NetworkResult<Transaction> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            NetworkResult.Success(Transaction("tx1", "WITHDRAWAL", request.amountSats, "PENDING", System.currentTimeMillis()))
        } else {
            safeApiCall {
                val res = apiService.requestWithdrawal(request)
                res.data ?: throw Exception(res.message)
            }
        }
    }
}
