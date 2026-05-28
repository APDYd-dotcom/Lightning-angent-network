package bi.lan.lan.domain.repository

import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.model.WithdrawRequest
import bi.lan.lan.data.remote.NetworkResult

interface WithdrawRepository {
    suspend fun requestWithdrawal(request: WithdrawRequest): NetworkResult<Transaction>
}
