package bi.lan.lan.domain.repository

import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.data.remote.NetworkResult
import kotlinx.coroutines.flow.Flow

interface RemittanceRepository {
    fun getRemittances(): Flow<List<RemittanceEntity>>
    suspend fun getRemittanceByReference(reference: String): RemittanceEntity?
    suspend fun createRemittanceRequest(amount: Long, description: String): NetworkResult<RemittanceEntity>
    suspend fun checkRemittanceStatus(reference: String): NetworkResult<RemittanceEntity>
    fun searchRemittances(query: String): Flow<List<RemittanceEntity>>
}
