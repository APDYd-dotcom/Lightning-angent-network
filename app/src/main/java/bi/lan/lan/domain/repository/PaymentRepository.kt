package bi.lan.lan.domain.repository

import bi.lan.lan.data.remote.NetworkResult

interface PaymentRepository {
    suspend fun getBalance(): NetworkResult<Long>
    suspend fun createInvoice(amount: Long, memo: String): NetworkResult<String>
    suspend fun payInvoice(paymentRequest: String): NetworkResult<String>
}
