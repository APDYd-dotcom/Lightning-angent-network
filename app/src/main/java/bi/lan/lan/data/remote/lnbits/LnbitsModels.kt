package bi.lan.lan.data.remote.lnbits

import kotlinx.serialization.Serializable

@Serializable
data class LnbitsWalletResponse(
    val id: String = "",
    val name: String = "",
    val balance: Long = 0
)

@Serializable
data class LnbitsInvoiceRequest(
    val out: Boolean = false,
    val amount: Long,
    val memo: String = "",
    val unit: String = "sat"
)

@Serializable
data class LnbitsInvoiceResponse(
    val payment_hash: String = "",
    val payment_request: String = "",
    val checking_id: String = ""
)

@Serializable
data class LnbitsPaymentRequest(
    val out: Boolean = true,
    val bolt11: String
)

@Serializable
data class LnbitsPaymentResponse(
    val payment_hash: String = ""
)

@Serializable
data class LnbitsTransaction(
    val payment_hash: String,
    val amount: Long,
    val fee: Long,
    val memo: String? = "",
    val time: Long,
    val pending: Boolean
)
