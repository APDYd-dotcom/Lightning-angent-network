package bi.lan.lan.data.agent.api

import bi.lan.lan.data.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AgentLightningApi(private val client: HttpClient) {

    suspend fun getRoot(): ApiInfoResponse = client.get("/").body()

    suspend fun getHealth(): HealthResponse = client.get("/health").body()

    suspend fun getInfo(): NodeInfoResponse = client.get("/info").body()

    suspend fun getBalance(): BalanceResponse = client.get("/balance").body()

    suspend fun createInvoice(request: CreateInvoiceRequest): CreateInvoiceResponse =
        client.post("/invoices") { setBody(request) }.body()

    suspend fun getInvoices(limit: Int = 100): InvoiceListResponse =
        client.get("/invoices?limit=$limit").body()

    suspend fun getInvoice(rHash: String): InvoiceResponse =
        client.get("/invoices/$rHash").body()

    suspend fun payInvoice(request: PaymentRequestDto): PaymentResponse =
        client.post("/payments") { setBody(request) }.body()

    suspend fun getPayments(limit: Int = 100): PaymentListResponse =
        client.get("/payments?limit=$limit").body()

    suspend fun decodeInvoice(paymentRequest: String): DecodeInvoiceResponse =
        client.get("/decode/$paymentRequest").body()
}
