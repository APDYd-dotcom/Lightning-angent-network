package bi.lan.lan.unit

import bi.lan.lan.data.model.BalanceResponse
import bi.lan.lan.data.model.WalletBalance
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.domain.usecase.GetBalanceUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetBalanceUseCaseTest {

    private val repository = mockk<LightningRepository>()
    private val useCase = GetBalanceUseCase(repository)

    @Test
    fun `invoke should return balance on success`() = runBlocking {
        // Given
        val expectedBalance = 5000L
        coEvery { repository.getBalance() } returns NetworkResult.Success(
            BalanceResponse(walletBalance = WalletBalance(totalBalance = expectedBalance))
        )

        // When
        val result = useCase()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedBalance, (result as NetworkResult.Success).data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runBlocking {
        // Given
        val errorMessage = "Failed to fetch balance"
        coEvery { repository.getBalance() } returns NetworkResult.Error(message = errorMessage)

        // When
        val result = useCase()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).message)
    }
}
