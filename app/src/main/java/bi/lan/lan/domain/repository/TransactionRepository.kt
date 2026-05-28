package bi.lan.lan.domain.repository

import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.remote.NetworkResult

interface TransactionRepository {
    suspend fun getTransactions(): NetworkResult<List<Transaction>>
}
