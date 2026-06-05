package bi.lan.lan.repository

import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.remote.lnbits.LnbitsApiService
import bi.lan.lan.data.remote.lnbits.LnbitsInvoiceResponse
import bi.lan.lan.data.remote.lnbits.LnbitsWalletResponse
import bi.lan.lan.data.repository.AgentLightningRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AgentLightningRepositoryTest {

    private val api = mockk<LnbitsApiService>()
    private val repository = AgentLightningRepository(api)

    @Test
    fun `getBalance should return success when api succeeds`() = runBlocking {
        // Given
        val expectedBalance = 1000L
        coEvery { api.getWalletBalance() } returns LnbitsWalletResponse(balance = expectedBalance)

        // When
        val result = repository.getBalance()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedBalance, (result as NetworkResult.Success).data.walletBalance.totalBalance)
    }

    @Test
    fun `createInvoice should return success when api succeeds`() = runBlocking {
        // Given
        val amount = 500L
        val memo = "Test"
        val expectedPR = "lnbc..."
        coEvery { api.createInvoice(amount, memo) } returns LnbitsInvoiceResponse(payment_request = expectedPR, payment_hash = "hash")

        // When
        val result = repository.createInvoice(amount, memo, 3600)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedPR, (result as NetworkResult.Success).data.paymentRequest)
    }

    @Test
    fun `getBalance should return error when api throws exception`() = runBlocking {
        // Given
        coEvery { api.getWalletBalance() } throws Exception("API Error")

        // When
        val result = repository.getBalance()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("API Error", (result as NetworkResult.Error).message)
    }
}
