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
    val me: BlinkMe? = null,
    val accountDefaultWallet: BlinkWallet? = null
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
    val balance: Long? = null,
    val walletCurrency: String? = null,
    val currency: String? = null
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
    val initiationVia: BlinkInitiationVia? = null,
    val settlementVia: BlinkSettlementVia? = null,
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
    @kotlinx.serialization.SerialName("__typename") val type: String? = null,
    val paymentRequest: String? = null
)

@Serializable
data class BlinkSettlementVia(
    @kotlinx.serialization.SerialName("__typename") val type: String? = null,
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
    val lnInvoiceCreate: BlinkLnInvoiceCreate? = null,
    val lnInvoiceCreateOnBehalfOfRecipient: BlinkLnInvoiceCreate? = null
)

@Serializable
data class BlinkLnInvoiceCreate(
    val invoice: BlinkInvoice? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkInvoice(
    val paymentRequest: String,
    val paymentHash: String? = null,
    val satoshis: Long? = null
)

@Serializable
data class BlinkPaymentResponse(
    val data: BlinkPaymentData? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkPaymentData(
    val lnInvoicePaymentSend: BlinkLnInvoicePaymentSend? = null,
    val lnNoAmountInvoicePaymentSend: BlinkLnInvoicePaymentSend? = null,
    val lnAddressPaymentSend: BlinkLnInvoicePaymentSend? = null
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

@Serializable
data class BlinkDecodeInvoiceResponse(
    val data: BlinkDecodeInvoiceData? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkDecodeInvoiceData(
    val lnInvoiceDecode: BlinkDecodedInvoice? = null,
    val invoiceByPaymentRequest: BlinkDecodedInvoice? = null
)

@Serializable
data class BlinkDecodedInvoice(
    val paymentHash: String? = null,
    val amount: Long? = null,
    val satoshis: Long? = null,
    val memo: String? = null,
    val expiry: Long? = null,
    val paymentRequest: String? = null,
    val paymentStatus: String? = null
)

@Serializable
data class BlinkAccountDetailsResponse(
    val data: BlinkAccountDetailsData? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkAccountDetailsData(
    val me: BlinkMe? = null
)

@Serializable
data class BlinkInvoiceStatusResponse(
    val data: BlinkInvoiceStatusData? = null,
    val errors: List<BlinkError>? = null
)

@Serializable
data class BlinkInvoiceStatusData(
    val lnInvoicePaymentStatus: BlinkInvoiceStatus? = null
)

@Serializable
data class BlinkInvoiceStatus(
    val status: String
)
