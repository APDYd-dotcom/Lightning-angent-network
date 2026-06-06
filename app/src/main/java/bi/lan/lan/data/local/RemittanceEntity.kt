package bi.lan.lan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remittances")
data class RemittanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reference: String,
    val amount: Long,
    val description: String,
    val invoice: String, // BOLT11
    val transactionId: String? = null,
    val walletId: String? = null,
    val createdAt: Long,
    val paidAt: Long? = null,
    val status: String // PENDING, PAID, EXPIRED, FAILED
)
