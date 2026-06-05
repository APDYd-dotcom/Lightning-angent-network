package bi.lan.lan.data.repository

import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.remote.blink.BlinkApiService
import bi.lan.lan.domain.repository.LightningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerLightningRepository(
    private val api: BlinkApiService
) : LightningRepository {

    private var cachedWalletId: String? = null

    private suspend fun getWalletId(): String {
        cachedWalletId?.let { return it }
        val balance = api.getBalance()
        val wallet = balance.data?.me?.defaultAccount?.wallets?.find { it.walletCurrency == "BTC" }
        val id = wallet?.id ?: throw Exception("BTC wallet not found")
        cachedWalletId = id
        return id
    }

    override suspend fun getHealth(): NetworkResult<HealthResponse> = NetworkResult.Success(HealthResponse(status = "OK"))

    override suspend fun getInfo(): NetworkResult<NodeInfoResponse> = NetworkResult.Success(NodeInfoResponse(alias = "Blink Customer"))

    override suspend fun getBalance(): NetworkResult<BalanceResponse> = safeCall {
        val blinkBalance = api.getBalance()
        val wallet = blinkBalance.data?.me?.defaultAccount?.wallets?.find { it.walletCurrency == "BTC" }
        cachedWalletId = wallet?.id
        val balanceSats = wallet?.balance ?: 0
        BalanceResponse(walletBalance = WalletBalance(totalBalance = balanceSats))
    }

    override suspend fun createInvoice(amount: Long, memo: String, expiry: Long): NetworkResult<CreateInvoiceResponse> =
        safeCall {
            val walletId = getWalletId()
            val res = api.createInvoice(amount, memo, walletId)
            val invoice = res.data?.lnInvoiceCreate?.invoice
            CreateInvoiceResponse(paymentRequest = invoice?.paymentRequest ?: "", rHash = invoice?.paymentHash ?: "")
        }

    override suspend fun getInvoices(): NetworkResult<List<InvoiceResponse>> =
        NetworkResult.Error(message = "Not implemented")

    override suspend fun getInvoice(rHash: String): NetworkResult<InvoiceResponse> =
        NetworkResult.Error(message = "Not implemented")

    override suspend fun payInvoice(paymentRequest: String, amount: Long?): NetworkResult<PaymentResponse> =
        safeCall {
            val walletId = getWalletId()
            val res = api.payInvoice(paymentRequest, walletId)
            val status = res.data?.lnInvoicePaymentSend?.status
            if (status == "SUCCESS" || status == "PENDING") {
                PaymentResponse(paymentHash = "blink_payment")
            } else {
                val errorMsg = res.data?.lnInvoicePaymentSend?.errors?.firstOrNull()?.message 
                    ?: res.errors?.firstOrNull()?.message ?: "Payment failed"
                throw Exception(errorMsg)
            }
        }

    override suspend fun getPayments(): NetworkResult<List<PaymentHistoryItem>> =
        NetworkResult.Error(message = "Not implemented")

    override suspend fun decodeInvoice(paymentRequest: String): NetworkResult<DecodeInvoiceResponse> =
        safeCall {
            // Mock decoding for demo mode
            if (paymentRequest.startsWith("lnbc")) {
                DecodeInvoiceResponse(
                    destination = "blink_destination_pubkey",
                    paymentHash = "mock_hash",
                    numSatoshis = 1000,
                    description = "Mock Blink Invoice",
                    expiry = 3600
                )
            } else {
                throw Exception("Invalid BOLT11 invoice")
            }
        }

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
