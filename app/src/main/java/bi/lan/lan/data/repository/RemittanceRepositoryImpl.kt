package bi.lan.lan.data.repository

import bi.lan.lan.data.local.RemittanceDao
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.remote.blink.BlinkApiService
import bi.lan.lan.domain.repository.RemittanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

class RemittanceRepositoryImpl(
    private val api: BlinkApiService,
    private val dao: RemittanceDao
) : RemittanceRepository {

    private var cachedWalletId: String? = null

    private suspend fun getWalletId(): String {
        cachedWalletId?.let { return it }
        val balance = api.getBalance()
        val wallet = balance.data?.me?.defaultAccount?.wallets?.find { it.walletCurrency == "BTC" }
        val id = wallet?.id ?: throw Exception("BTC wallet not found")
        cachedWalletId = id
        return id
    }

    override fun getRemittances(): Flow<List<RemittanceEntity>> = dao.getAllRemittances()

    override suspend fun getRemittanceByReference(reference: String): RemittanceEntity? = 
        dao.getRemittanceByReference(reference)

    override suspend fun createRemittanceRequest(amount: Long, description: String): NetworkResult<RemittanceEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val walletId = getWalletId()
                val reference = generateReference()
                val blinkRes = api.createInvoice(amount, "Ref: $reference | $description", walletId)
                val invoice = blinkRes.data?.lnInvoiceCreate?.invoice ?: throw Exception("Failed to create Blink invoice")
                
                val entity = RemittanceEntity(
                    reference = reference,
                    amount = amount,
                    description = description,
                    invoice = invoice.paymentRequest,
                    transactionId = invoice.paymentHash,
                    walletId = walletId,
                    createdAt = System.currentTimeMillis(),
                    status = "PENDING",
                    type = "INBOUND"
                )
                dao.insertRemittance(entity)
                NetworkResult.Success(entity)
            } catch (e: Exception) {
                NetworkResult.Error(message = e.message ?: "Unknown error creating remittance")
            }
        }
    }

    override suspend fun trackOutboundPayment(
        paymentRequest: String,
        amount: Long,
        description: String,
        transactionId: String
    ): RemittanceEntity {
        return withContext(Dispatchers.IO) {
            val reference = generateReference()
            val entity = RemittanceEntity(
                reference = reference,
                amount = amount,
                description = description,
                invoice = paymentRequest,
                transactionId = transactionId,
                createdAt = System.currentTimeMillis(),
                status = "PAID",
                paidAt = System.currentTimeMillis(),
                type = "OUTBOUND"
            )
            dao.insertRemittance(entity)
            entity
        }
    }

    override suspend fun getAccountInfo(): NetworkResult<bi.lan.lan.data.remote.blink.BlinkMe> {
        return withContext(Dispatchers.IO) {
            try {
                val res = api.getAccountDetails()
                val me = res.data?.me ?: throw Exception("Failed to get account details")
                NetworkResult.Success(me)
            } catch (e: Exception) {
                NetworkResult.Error(message = e.message ?: "Unknown error")
            }
        }
    }

    override suspend fun checkRemittanceStatus(reference: String): NetworkResult<RemittanceEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val remittance = dao.getRemittanceByReference(reference) ?: throw Exception("Remittance not found")
                if (remittance.status == "PAID") return@withContext NetworkResult.Success(remittance)

                // First check by status query (efficient)
                val statusRes = api.checkInvoiceStatus(remittance.invoice)
                val blinkStatus = statusRes.data?.lnInvoicePaymentStatus?.status

                if (blinkStatus == "PAID") {
                    // Fetch full details to get transactionId and timestamp
                    val transactions = api.getTransactions(100)
                    val tx = transactions.data?.me?.defaultAccount?.transactions?.edges?.find {
                        it.node.memo?.contains(reference) == true
                    }
                    
                    val updatedRemittance = remittance.copy(
                        status = "PAID",
                        paidAt = (tx?.node?.createdAt ?: (System.currentTimeMillis() / 1000)) * 1000,
                        transactionId = tx?.node?.id ?: remittance.transactionId
                    )
                    dao.updateRemittance(updatedRemittance)
                    NetworkResult.Success(updatedRemittance)
                } else if (blinkStatus == "EXPIRED") {
                    val updated = remittance.copy(status = "EXPIRED")
                    dao.updateRemittance(updated)
                    NetworkResult.Success(updated)
                } else {
                    NetworkResult.Success(remittance)
                }
            } catch (e: Exception) {
                NetworkResult.Error(message = e.message ?: "Error checking status")
            }
        }
    }

    override fun searchRemittances(query: String): Flow<List<RemittanceEntity>> = dao.searchRemittances(query)

    private fun generateReference(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val random = (100000..999999).random()
        return "LAN-$year-$random"
    }
}
