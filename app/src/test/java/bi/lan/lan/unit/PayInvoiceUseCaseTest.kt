package bi.lan.lan.unit

import bi.lan.lan.data.model.PaymentResponse
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.domain.usecase.PayInvoiceUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PayInvoiceUseCaseTest {

    private val repository = mockk<LightningRepository>()
    private val useCase = PayInvoiceUseCase(repository)

    @Test
    fun `invoke should return payment hash on success`() = runBlocking {
        // Given
        val paymentRequest = "lnbc1000..."
        val expectedPaymentHash = "hash123"
        coEvery { repository.payInvoice(paymentRequest, null) } returns NetworkResult.Success(
            PaymentResponse(paymentHash = expectedPaymentHash)
        )

        // When
        val result = useCase(paymentRequest)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedPaymentHash, (result as NetworkResult.Success).data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runBlocking {
        // Given
        val paymentRequest = "lnbc1000..."
        val errorMessage = "Payment Failed"
        coEvery { repository.payInvoice(paymentRequest, null) } returns NetworkResult.Error(message = errorMessage)

        // When
        val result = useCase(paymentRequest)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).message)
    }
}
