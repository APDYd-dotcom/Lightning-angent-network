package bi.lan.lan.viewmodel

import bi.lan.lan.MainDispatcherExtension
import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.presentation.screens.agent.AgentHomeViewModel
import bi.lan.lan.presentation.screens.agent.AgentTransactionsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class AgentHomeAndTransactionsViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val repository = mockk<LightningRepository>(relaxed = true)

    @Test
    fun `AgentHomeViewModel load should update health and balance`() = runTest {
        val health = HealthResponse(status = "OK")
        val balance = BalanceResponse(walletBalance = WalletBalance(totalBalance = 1000L))
        coEvery { repository.getHealth() } returns NetworkResult.Success(health)
        coEvery { repository.getBalance() } returns NetworkResult.Success(balance)

        val viewModel = AgentHomeViewModel(repository)
        viewModel.load()

        assertEquals(health, viewModel.health.value)
        assertEquals(balance, viewModel.balance.value)
    }

    @Test
    fun `AgentTransactionsViewModel load should update invoices and payments`() = runTest {
        val invoices = listOf(InvoiceResponse(rHash = "hash1", creationDate = 100))
        val payments = listOf(PaymentHistoryItem(paymentHash = "pay1", creationDate = 200))
        
        coEvery { repository.getInvoices() } returns NetworkResult.Success(invoices)
        coEvery { repository.getPayments() } returns NetworkResult.Success(payments)

        val viewModel = AgentTransactionsViewModel(repository)
        viewModel.load()

        assertEquals(1, viewModel.invoices.value.size)
        assertEquals("hash1", viewModel.invoices.value[0].rHash)
        assertEquals(1, viewModel.payments.value.size)
        assertEquals("pay1", viewModel.payments.value[0].paymentHash)
    }
}
