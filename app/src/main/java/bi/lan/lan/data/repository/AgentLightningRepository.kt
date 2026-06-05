package bi.lan.lan.data.repository

import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.remote.lnbits.LnbitsApiService
import bi.lan.lan.domain.repository.LightningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgentLightningRepository(
    private val api: LnbitsApiService
) : LightningRepository {

    override suspend fun getHealth(): NetworkResult<HealthResponse> = NetworkResult.Success(HealthResponse(status = "OK"))

    override suspend fun getInfo(): NetworkResult<NodeInfoResponse> = NetworkResult.Success(NodeInfoResponse(alias = "LNbits Agent"))

    override suspend fun getBalance(): NetworkResult<BalanceResponse> = safeCall {
        val lnbitsBalance = api.getWalletBalance()
        BalanceResponse(walletBalance = WalletBalance(totalBalance = lnbitsBalance.balance))
    }

    override suspend fun createInvoice(amount: Long, memo: String, expiry: Long): NetworkResult<CreateInvoiceResponse> =
        safeCall {
            val res = api.createInvoice(amount, memo)
            CreateInvoiceResponse(paymentRequest = res.payment_request, rHash = res.payment_hash)
        }

    override suspend fun getInvoices(): NetworkResult<List<InvoiceResponse>> =
        safeCall {
            api.listTransactions().filter { !it.pending && it.amount > 0 }.map {
                InvoiceResponse(paymentRequest = "", rHash = it.payment_hash, amount = it.amount, memo = it.memo, settled = true)
            }
        }

    override suspend fun getInvoice(rHash: String): NetworkResult<InvoiceResponse> =
        NetworkResult.Error(message = "Not implemented")

    override suspend fun payInvoice(paymentRequest: String, amount: Long?): NetworkResult<PaymentResponse> =
        safeCall {
            val res = api.payInvoice(paymentRequest)
            PaymentResponse(paymentHash = res.payment_hash)
        }

    override suspend fun getPayments(): NetworkResult<List<PaymentHistoryItem>> =
        safeCall {
            api.listTransactions().filter { it.amount < 0 }.map {
                PaymentHistoryItem(paymentHash = it.payment_hash, value = it.amount, status = "SUCCEEDED")
            }
        }

    override suspend fun decodeInvoice(paymentRequest: String): NetworkResult<DecodeInvoiceResponse> =
        NetworkResult.Error(message = "Not implemented")

    private suspend fun <T> safeCall(call: suspend () -> T): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(call())
            } catch (e: Exception) {
                e.printStackTrace()
                NetworkResult.Error(message = e.message ?: "Unknown error")
            }
        }
    }
}
