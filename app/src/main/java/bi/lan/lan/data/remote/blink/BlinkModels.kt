package bi.lan.lan.data.remote.blink

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: JsonObject? = null
)

@Serializable
data class BlinkBalanceResponse(
    val data: BlinkBalanceData? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkBalanceData(
    val me: BlinkMe? = null
)

@Serializable
data class BlinkMe(
    val id: String? = null,
    val username: String? = null,
    val createdAt: Long? = null,
    val defaultAccount: BlinkAccount? = null
)

@Serializable
data class BlinkAccount(
    val wallets: List<BlinkWallet> = emptyList(),
    val transactions: BlinkTransactions? = null
)

@Serializable
data class BlinkWallet(
    val id: String,
    val balance: Long,
    val walletCurrency: String
)

@Serializable
data class BlinkTransactions(
    val edges: List<BlinkTransactionEdge> = emptyList()
)

@Serializable
data class BlinkTransactionEdge(
    val node: BlinkTransactionNode
)

@Serializable
data class BlinkTransactionNode(
    val id: String,
    val initiationVia: BlinkInitiationVia,
    val settlementVia: BlinkSettlementVia,
    val settlementAmount: Long,
    val settlementCurrency: String,
    val settlementDisplayAmount: String,
    val settlementDisplayCurrency: String,
    val settlementDisplayFee: String,
    val status: String,
    val memo: String? = null,
    val createdAt: Long
)

@Serializable
data class BlinkInitiationVia(
    val type: String,
    val paymentRequest: String? = null
)

@Serializable
data class BlinkSettlementVia(
    val type: String,
    val counterpartyWalletId: String? = null
)

@Serializable
data class BlinkError(
    val message: String
)

@Serializable
data class BlinkInvoiceResponse(
    val data: BlinkInvoiceData? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkInvoiceData(
    val lnInvoiceCreate: BlinkLnInvoiceCreate? = null
)

@Serializable
data class BlinkLnInvoiceCreate(
    val invoice: BlinkInvoice? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkInvoice(
    val paymentRequest: String,
    val paymentHash: String
)

@Serializable
data class BlinkPaymentResponse(
    val data: BlinkPaymentData? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkPaymentData(
    val lnInvoicePaymentSend: BlinkLnInvoicePaymentSend? = null
)

@Serializable
data class BlinkLnInvoicePaymentSend(
    val status: String? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkTransactionsResponse(
    val data: BlinkBalanceData? = null,
    val errors: List<BlinkError>? = null
)
