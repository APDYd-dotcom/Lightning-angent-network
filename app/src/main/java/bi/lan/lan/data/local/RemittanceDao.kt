package bi.lan.lan.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RemittanceDao {
    @Query("SELECT * FROM remittances ORDER BY createdAt DESC")
    fun getAllRemittances(): Flow<List<RemittanceEntity>>

    @Query("SELECT * FROM remittances WHERE reference = :reference LIMIT 1")
    suspend fun getRemittanceByReference(reference: String): RemittanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemittance(remittance: RemittanceEntity)

    @Update
    suspend fun updateRemittance(remittance: RemittanceEntity)

    @Query("SELECT * FROM remittances WHERE reference LIKE '%' || :query || '%'")
    fun searchRemittances(query: String): Flow<List<RemittanceEntity>>
}
