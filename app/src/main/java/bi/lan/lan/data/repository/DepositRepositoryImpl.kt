package bi.lan.lan.data.repository

import bi.lan.lan.data.api.ApiClient
import bi.lan.lan.data.api.LightningApiService
import bi.lan.lan.data.model.DepositRequest
import bi.lan.lan.data.model.PaymentInvoice
import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.remote.BaseRepository
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.DepositRepository
import kotlinx.coroutines.delay

class DepositRepositoryImpl(
    private val apiService: LightningApiService
) : BaseRepository(), DepositRepository {

    override suspend fun createDeposit(request: DepositRequest): NetworkResult<PaymentInvoice> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            NetworkResult.Success(PaymentInvoice("lnbc1000000test", "testhash", request.amountSats))
        } else {
            safeApiCall {
                val res = apiService.createDeposit(request)
                res.data ?: throw Exception(res.message)
            }
        }
    }

    override suspend fun getDepositStatus(id: String): NetworkResult<Transaction> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(500)
            NetworkResult.Success(Transaction(id, "DEPOSIT", 10000, "COMPLETED", System.currentTimeMillis()))
        } else {
            safeApiCall {
                apiService.getDepositStatus(id).data ?: throw Exception("Not found")
            }
        }
    }
}
