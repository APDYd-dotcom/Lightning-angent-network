package bi.lan.lan.data.repository

import bi.lan.lan.data.api.ApiClient
import bi.lan.lan.data.api.LightningApiService
import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.remote.BaseRepository
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.TransactionRepository
import kotlinx.coroutines.delay

class TransactionRepositoryImpl(
    private val apiService: LightningApiService
) : BaseRepository(), TransactionRepository {

    override suspend fun getTransactions(): NetworkResult<List<Transaction>> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            NetworkResult.Success(
                listOf(
                    Transaction("1", "DEPOSIT", 50000, "COMPLETED", System.currentTimeMillis() - 86400000, "Cash in via Agent Musa"),
                    Transaction("2", "WITHDRAWAL", 10000, "COMPLETED", System.currentTimeMillis() - 172800000, "Cash out via Amina"),
                    Transaction("3", "LIGHTNING_PAYMENT", 5000, "FAILED", System.currentTimeMillis() - 259200000, "Store payment")
                )
            )
        } else {
            safeApiCall {
                apiService.getTransactions().data ?: emptyList()
            }
        }
    }
}
