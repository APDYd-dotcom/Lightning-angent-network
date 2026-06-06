package bi.lan.lan.data.repository

import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.remote.blink.BlinkApiService
import bi.lan.lan.domain.repository.LightningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlinkLightningRepository(
    private val api: BlinkApiService
) : LightningRepository {

    private var cachedWalletId: String? = null

    private suspend fun getWalletId(): String {
        cachedWalletId?.let { return it }
        val balance = api.getBalance()
        val wallet = balance.data?.me?.defaultAccount?.wallets?.find { 
            it.walletCurrency == "BTC" || it.currency == "BTC" 
        }
        val id = wallet?.id ?: throw Exception("BTC wallet not found")
        cachedWalletId = id
        return id
    }

    override suspend fun getHealth(): NetworkResult<HealthResponse> = NetworkResult.Success(HealthResponse(status = "OK"))

    override suspend fun getInfo(): NetworkResult<NodeInfoResponse> = NetworkResult.Success(NodeInfoResponse(alias = "Blink Node"))

    override suspend fun getBalance(): NetworkResult<BalanceResponse> = safeCall {
        val blinkBalance = api.getBalance()
        val wallet = blinkBalance.data?.me?.defaultAccount?.wallets?.find { 
            it.walletCurrency == "BTC" || it.currency == "BTC" 
        }
        cachedWalletId = wallet?.id
        val balanceSats = wallet?.balance ?: 0
        BalanceResponse(walletBalance = WalletBalance(totalBalance = balanceSats))
    }

    override suspend fun createInvoice(amount: Long, memo: String, expiry: Long): NetworkResult<CreateInvoiceResponse> =
        safeCall {
            val walletId = getWalletId()
            val res = api.createInvoice(amount, memo, walletId)
            val invoiceNode = res.data?.lnInvoiceCreate ?: res.data?.lnInvoiceCreateOnBehalfOfRecipient
            val invoice = invoiceNode?.invoice
            CreateInvoiceResponse(
                paymentRequest = invoice?.paymentRequest ?: "", 
                rHash = invoice?.paymentHash ?: ""
            )
        }

    override suspend fun getInvoices(): NetworkResult<List<InvoiceResponse>> = safeCall {
        val res = api.getTransactions(50)
        res.data?.me?.defaultAccount?.transactions?.edges?.map { edge ->
            val node = edge.node
            InvoiceResponse(
                paymentRequest = node.initiationVia?.paymentRequest ?: "",
                rHash = node.id,
                amount = node.settlementAmount,
                memo = node.memo ?: "",
                settled = node.status == "SUCCESS",
                creationDate = node.createdAt
            )
        } ?: emptyList()
    }

    override suspend fun getInvoice(rHash: String): NetworkResult<InvoiceResponse> =
        NetworkResult.Error(message = "Not implemented in Blink")

    override suspend fun payInvoice(paymentRequest: String, amount: Long?): NetworkResult<PaymentResponse> =
        safeCall {
            val walletId = getWalletId()
            val res = if (amount != null && amount > 0) {
                api.payNoAmountInvoice(paymentRequest, amount, walletId)
            } else {
                api.payInvoice(paymentRequest, walletId)
            }

            val resultNode = res.data?.lnInvoicePaymentSend ?: res.data?.lnNoAmountInvoicePaymentSend ?: res.data?.lnAddressPaymentSend
            val status = resultNode?.status
            if (status == "SUCCESS" || status == "PENDING") {
                PaymentResponse(paymentHash = "blink_payment")
            } else {
                val errorMsg = resultNode?.errors?.firstOrNull()?.message
                    ?: res.errors?.firstOrNull()?.message ?: "Payment failed"
                throw Exception(errorMsg)
            }
        }

    override suspend fun getPayments(): NetworkResult<List<PaymentHistoryItem>> = safeCall {
        val res = api.getTransactions(50)
        res.data?.me?.defaultAccount?.transactions?.edges?.map { edge ->
            val node = edge.node
            PaymentHistoryItem(
                paymentHash = node.id,
                value = node.settlementAmount,
                creationDate = node.createdAt,
                paymentRequest = node.initiationVia?.paymentRequest ?: "",
                status = node.status
            )
        } ?: emptyList()
    }

    override suspend fun decodeInvoice(paymentRequest: String): NetworkResult<DecodeInvoiceResponse> =
        safeCall {
            val res = api.decodeInvoice(paymentRequest)
            val decoded = res.data?.lnInvoiceDecode ?: res.data?.invoiceByPaymentRequest
                ?: throw Exception("Failed to decode invoice")
            
            DecodeInvoiceResponse(
                paymentHash = decoded.paymentHash ?: "",
                numSatoshis = decoded.satoshis ?: decoded.amount ?: 0,
                description = decoded.memo ?: "",
                expiry = decoded.expiry ?: 0,
                destination = "Blink Network"
            )
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
