package bi.lan.lan.domain.repository

import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult

interface LightningRepository {
    suspend fun getHealth(): NetworkResult<HealthResponse>
    suspend fun getInfo(): NetworkResult<NodeInfoResponse>
    suspend fun getBalance(): NetworkResult<BalanceResponse>
    suspend fun createInvoice(amount: Long, memo: String, expiry: Long): NetworkResult<CreateInvoiceResponse>
    suspend fun getInvoices(): NetworkResult<List<InvoiceResponse>>
    suspend fun getInvoice(rHash: String): NetworkResult<InvoiceResponse>
    suspend fun payInvoice(paymentRequest: String, amount: Long?): NetworkResult<PaymentResponse>
    suspend fun getPayments(): NetworkResult<List<PaymentHistoryItem>>
    suspend fun decodeInvoice(paymentRequest: String): NetworkResult<DecodeInvoiceResponse>
}
