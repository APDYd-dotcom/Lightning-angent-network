package bi.lan.lan.domain.repository

import bi.lan.lan.data.model.DepositRequest
import bi.lan.lan.data.model.PaymentInvoice
import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.remote.NetworkResult

interface DepositRepository {
    suspend fun createDeposit(request: DepositRequest): NetworkResult<PaymentInvoice>
    suspend fun getDepositStatus(id: String): NetworkResult<Transaction>
}
