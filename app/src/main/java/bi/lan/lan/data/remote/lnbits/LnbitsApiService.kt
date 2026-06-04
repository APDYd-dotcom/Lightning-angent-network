package bi.lan.lan.data.remote.lnbits

import bi.lan.lan.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LnbitsApiService(private val client: HttpClient) {

    private val baseUrl = AppConfig.LNBITS_BASE_URL
    private val adminKey = AppConfig.LNBITS_ADMIN_KEY

    suspend fun getWalletBalance(): LnbitsWalletResponse = client.get("$baseUrl/api/v1/wallet") {
        header("X-Api-Key", adminKey)
    }.body()

    suspend fun createInvoice(amount: Long, memo: String): LnbitsInvoiceResponse = client.post("$baseUrl/api/v1/payments") {
        header("X-Api-Key", adminKey)
        contentType(ContentType.Application.Json)
        setBody(LnbitsInvoiceRequest(amount = amount, memo = memo))
    }.body()

    suspend fun payInvoice(bolt11: String): LnbitsPaymentResponse = client.post("$baseUrl/api/v1/payments") {
        header("X-Api-Key", adminKey)
        contentType(ContentType.Application.Json)
        setBody(LnbitsPaymentRequest(bolt11 = bolt11))
    }.body()

    suspend fun listTransactions(): List<LnbitsTransaction> = client.get("$baseUrl/api/v1/payments") {
        header("X-Api-Key", adminKey)
    }.body()
}
