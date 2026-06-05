package bi.lan.lan.unit

import bi.lan.lan.data.model.CreateInvoiceResponse
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.domain.usecase.CreateInvoiceUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CreateInvoiceUseCaseTest {

    private val repository = mockk<LightningRepository>()
    private val useCase = CreateInvoiceUseCase(repository)

    @Test
    fun `invoke should return payment request on success`() = runBlocking {
        // Given
        val amount = 1000L
        val memo = "Test Invoice"
        val expectedPaymentRequest = "lnbc1000..."
        coEvery { repository.createInvoice(amount, memo, 3600) } returns NetworkResult.Success(
            CreateInvoiceResponse(paymentRequest = expectedPaymentRequest)
        )

        // When
        val result = useCase(amount, memo)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedPaymentRequest, (result as NetworkResult.Success).data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runBlocking {
        // Given
        val amount = 1000L
        val memo = "Test Invoice"
        val errorMessage = "Network Error"
        coEvery { repository.createInvoice(amount, memo, 3600) } returns NetworkResult.Error(message = errorMessage)

        // When
        val result = useCase(amount, memo)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).message)
    }

    @Test
    fun `invoke should return loading when repository is loading`() = runBlocking {
        // Given
        val amount = 1000L
        val memo = "Test Invoice"
        coEvery { repository.createInvoice(amount, memo, 3600) } returns NetworkResult.Loading

        // When
        val result = useCase(amount, memo)

        // Then
        assertTrue(result is NetworkResult.Loading)
    }
}
