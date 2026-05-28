package bi.lan.lan.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Root / Health / Info ─────────────────────────────────────────────────────

@Serializable
data class ApiInfoResponse(
    val message: String = "",
    val version: String = "",
    val description: String = "",
    val docs: String = "",
    val health: String = ""
)

@Serializable
data class HealthResponse(
    val status: String = "",
    @SerialName("node_alias") val nodeAlias: String = "",
    @SerialName("synced_to_chain") val syncedToChain: Boolean = false,
    @SerialName("synced_to_graph") val syncedToGraph: Boolean = false,
    @SerialName("block_height") val blockHeight: Long = 0
)

@Serializable
data class NodeInfoResponse(
    val alias: String = "",
    @SerialName("identity_pubkey") val identityPubkey: String = "",
    @SerialName("num_active_channels") val numActiveChannels: Int = 0,
    @SerialName("num_peers") val numPeers: Int = 0,
    @SerialName("block_height") val blockHeight: Long = 0,
    @SerialName("synced_to_chain") val syncedToChain: Boolean = false,
    @SerialName("synced_to_graph") val syncedToGraph: Boolean = false,
    val version: String = ""
)

// ─── Balance ──────────────────────────────────────────────────────────────────

@Serializable
data class BalanceResponse(
    @SerialName("wallet_balance") val walletBalance: WalletBalance = WalletBalance(),
    @SerialName("channel_balance") val channelBalance: ChannelBalance = ChannelBalance()
)

@Serializable
data class WalletBalance(
    @SerialName("total_balance") val totalBalance: Long = 0,
    @SerialName("confirmed_balance") val confirmedBalance: Long = 0,
    @SerialName("unconfirmed_balance") val unconfirmedBalance: Long = 0
)

@Serializable
data class ChannelBalance(
    val balance: Long = 0,
    @SerialName("pending_open_balance") val pendingOpenBalance: Long = 0
)

// ─── Invoices ─────────────────────────────────────────────────────────────────

@Serializable
data class CreateInvoiceRequest(
    val amount: Long,
    val memo: String = "",
    val expiry: Long = 3600
)

@Serializable
data class CreateInvoiceResponse(
    @SerialName("payment_request") val paymentRequest: String = "",
    @SerialName("r_hash") val rHash: String = "",
    @SerialName("add_index") val addIndex: Long = 0
)

@Serializable
data class InvoiceResponse(
    @SerialName("payment_request") val paymentRequest: String = "",
    @SerialName("r_hash") val rHash: String = "",
    @SerialName("add_index") val addIndex: Long = 0,
    val amount: Long = 0,
    val memo: String = "",
    val expiry: Long = 0,
    val settled: Boolean = false,
    @SerialName("creation_date") val creationDate: Long = 0,
    @SerialName("settle_date") val settleDate: Long? = null
)

@Serializable
data class InvoiceListResponse(
    val invoices: List<InvoiceResponse> = emptyList()
)

// ─── Payments ─────────────────────────────────────────────────────────────────

@Serializable
data class PaymentRequestDto(
    @SerialName("payment_request") val paymentRequest: String,
    val amount: Long? = null
)

@Serializable
data class PaymentResponse(
    @SerialName("payment_hash") val paymentHash: String = "",
    @SerialName("payment_preimage") val paymentPreimage: String = "",
    @SerialName("payment_route") val paymentRoute: PaymentRoute = PaymentRoute()
)

@Serializable
data class PaymentRoute(
    @SerialName("total_fees") val totalFees: Long = 0,
    @SerialName("total_amt") val totalAmt: Long = 0,
    @SerialName("total_time_lock") val totalTimeLock: Long = 0
)

@Serializable
data class PaymentHistoryItem(
    @SerialName("payment_hash") val paymentHash: String = "",
    @SerialName("payment_preimage") val paymentPreimage: String = "",
    val value: Long = 0,
    @SerialName("creation_date") val creationDate: Long = 0,
    val fee: Long = 0,
    @SerialName("payment_request") val paymentRequest: String = "",
    val status: String = ""
)

@Serializable
data class PaymentListResponse(
    val payments: List<PaymentHistoryItem> = emptyList()
)

// ─── Decode ───────────────────────────────────────────────────────────────────

@Serializable
data class DecodeInvoiceResponse(
    val destination: String = "",
    @SerialName("payment_hash") val paymentHash: String = "",
    @SerialName("num_satoshis") val numSatoshis: Long = 0,
    val timestamp: Long = 0,
    val expiry: Long = 0,
    val description: String = "",
    @SerialName("description_hash") val descriptionHash: String = "",
    @SerialName("fallback_addr") val fallbackAddr: String = "",
    @SerialName("cltv_expiry") val cltvExpiry: Long = 0
)

// ─── Sign / Verify ────────────────────────────────────────────────────────────

@Serializable
data class SignMessageRequest(val message: String)

@Serializable
data class SignMessageResponse(val signature: String = "")

@Serializable
data class VerifyMessageRequest(
    val message: String,
    val signature: String,
    val pubkey: String
)

@Serializable
data class VerifyMessageResponse(val valid: Boolean = false)
