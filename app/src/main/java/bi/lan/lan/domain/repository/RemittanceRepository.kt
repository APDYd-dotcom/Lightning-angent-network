package bi.lan.lan.domain.repository

import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.remote.blink.BlinkMe
import kotlinx.coroutines.flow.Flow

interface RemittanceRepository {
    fun getRemittances(): Flow<List<RemittanceEntity>>
    suspend fun getRemittanceByReference(reference: String): RemittanceEntity?
    suspend fun createRemittanceRequest(amount: Long, description: String): NetworkResult<RemittanceEntity>
    suspend fun trackOutboundPayment(paymentRequest: String, amount: Long, description: String, transactionId: String): RemittanceEntity
    suspend fun checkRemittanceStatus(reference: String): NetworkResult<RemittanceEntity>
    fun searchRemittances(query: String): Flow<List<RemittanceEntity>>
    suspend fun getAccountInfo(): NetworkResult<BlinkMe>
}
