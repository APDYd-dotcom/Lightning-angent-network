package bi.lan.lan.unit

import bi.lan.lan.data.remote.blink.BlinkApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class BlinkApiServiceTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `getBalance should return wallet info`() = runBlocking {
        // Given
        val mockEngine = MockEngine { request ->
            respond(
                content = """{
                    "data": {
                        "me": {
                            "defaultAccount": {
                                "wallets": [
                                    { "id": "wallet-1", "balance": 1000, "walletCurrency": "BTC" }
                                ]
                            }
                        }
                    }
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }
        val apiService = BlinkApiService(client)

        // When
        val result = apiService.getBalance()

        // Then
        assertNotNull(result.data?.me?.defaultAccount?.wallets)
        assertEquals(1, result.data?.me?.defaultAccount?.wallets?.size)
        assertEquals(1000L, result.data?.me?.defaultAccount?.wallets?.get(0)?.balance)
    }

    @Test
    fun `createInvoice should return invoice data`() = runBlocking {
        // Given
        val mockEngine = MockEngine { request ->
            respond(
                content = """{
                    "data": {
                        "lnInvoiceCreate": {
                            "invoice": { "paymentRequest": "lnbc100", "paymentHash": "hash123" },
                            "errors": []
                        }
                    }
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }
        val apiService = BlinkApiService(client)

        // When
        val result = apiService.createInvoice(100, "Test", "wallet-1")

        // Then
        assertEquals("lnbc100", result.data?.lnInvoiceCreate?.invoice?.paymentRequest)
    }
}
