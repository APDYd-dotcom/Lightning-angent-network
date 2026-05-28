package bi.lan.lan.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val phoneNumber: String,
    val name: String? = null
)

@Serializable
data class Wallet(
    val balanceSats: Long,
    val address: String? = null
)

@Serializable
data class Agent(
    val id: String,
    val name: String,
    val location: String,
    val distanceKm: Double,
    val isOnline: Boolean
)

@Serializable
data class Transaction(
    val id: String,
    val type: String, // DEPOSIT, WITHDRAWAL, LIGHTNING_PAYMENT
    val amountSats: Long,
    val status: String, // PENDING, COMPLETED, FAILED
    val timestamp: Long,
    val description: String? = null
)

@Serializable
data class DepositRequest(
    val agentId: String,
    val amountSats: Long
)

@Serializable
data class WithdrawRequest(
    val agentId: String,
    val amountSats: Long
)

@Serializable
data class PaymentInvoice(
    val pr: String, // Payment Request (invoice string)
    val paymentHash: String,
    val amountSats: Long
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)
