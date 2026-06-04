package bi.lan.lan.data.remote.blink

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, kotlinx.serialization.json.JsonElement> = emptyMap()
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
    val defaultAccount: BlinkAccount? = null
)

@Serializable
data class BlinkAccount(
    val wallets: List<BlinkWallet> = emptyList()
)

@Serializable
data class BlinkWallet(
    val id: String,
    val balance: Long,
    val walletCurrency: String
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
