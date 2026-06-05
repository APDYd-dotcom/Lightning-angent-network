package bi.lan.lan.repository

import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.data.remote.blink.*
import bi.lan.lan.data.repository.CustomerLightningRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CustomerLightningRepositoryTest {

    private val api = mockk<BlinkApiService>()
    private val repository = CustomerLightningRepository(api)

    @Test
    fun `getBalance should return success and cache walletId`() = runBlocking {
        // Given
        val walletId = "wallet-123"
        val balanceSats = 5000L
        val mockResponse = BlinkBalanceResponse(
            data = BlinkBalanceData(
                me = BlinkMe(
                    defaultAccount = BlinkAccount(
                        wallets = listOf(
                            BlinkWallet(id = walletId, walletCurrency = "BTC", balance = balanceSats)
                        )
                    )
                )
            )
        )
        coEvery { api.getBalance() } returns mockResponse

        // When
        val result = repository.getBalance()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(balanceSats, (result as NetworkResult.Success).data.walletBalance.totalBalance)
    }

    @Test
    fun `createInvoice should use cached walletId or fetch it`() = runBlocking {
        // Given
        val walletId = "wallet-123"
        val pr = "lnbc100"
        val balanceResponse = BlinkBalanceResponse(
            data = BlinkBalanceData(
                me = BlinkMe(
                    defaultAccount = BlinkAccount(
                        wallets = listOf(BlinkWallet(id = walletId, walletCurrency = "BTC", balance = 0))
                    )
                )
            )
        )
        val invoiceResponse = BlinkInvoiceResponse(
            data = BlinkInvoiceData(
                lnInvoiceCreate = BlinkLnInvoiceCreate(
                    invoice = BlinkInvoice(paymentRequest = pr, paymentHash = "hash")
                )
            )
        )
        
        coEvery { api.getBalance() } returns balanceResponse
        coEvery { api.createInvoice(100, "Test", walletId) } returns invoiceResponse

        // When
        val result = repository.createInvoice(100, "Test", 3600)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(pr, (result as NetworkResult.Success).data.paymentRequest)
    }

    @Test
    fun `payInvoice should return error on Blink failure`() = runBlocking {
        // Given
        val walletId = "wallet-123"
        val balanceResponse = BlinkBalanceResponse(
            data = BlinkBalanceData(me = BlinkMe(defaultAccount = BlinkAccount(wallets = listOf(BlinkWallet(id = walletId, walletCurrency = "BTC", balance = 0)))))
        )
        val errorResponse = BlinkPaymentResponse(
            data = BlinkPaymentData(
                lnInvoicePaymentSend = BlinkLnInvoicePaymentSend(
                    status = "FAILURE",
                    errors = listOf(BlinkError(message = "Insufficient funds"))
                )
            )
        )

        coEvery { api.getBalance() } returns balanceResponse
        coEvery { api.payInvoice(any(), walletId) } returns errorResponse

        // When
        val result = repository.payInvoice("lnbc...", null)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Insufficient funds", (result as NetworkResult.Error).message)
    }
}
