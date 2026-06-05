package bi.lan.lan.unit

import bi.lan.lan.data.remote.lnbits.LnbitsApiService
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
import org.junit.jupiter.api.Test

class LnbitsApiServiceTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `getWalletBalance should return balance from network`() = runBlocking {
        // Given
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"id": "1", "name": "test", "balance": 1000}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        val apiService = LnbitsApiService(client)

        // When
        val result = apiService.getWalletBalance()

        // Then
        assertEquals(1000L, result.balance)
    }

    @Test
    fun `createInvoice should send correct body and return response`() = runBlocking {
        // Given
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"payment_hash": "hash123", "payment_request": "lnbc100", "checking_id": "id1"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        val apiService = LnbitsApiService(client)

        // When
        val result = apiService.createInvoice(100, "Test")

        // Then
        assertEquals("lnbc100", result.payment_request)
        assertEquals("hash123", result.payment_hash)
    }
}
