package bi.lan.lan.viewmodel

import app.cash.turbine.test
import bi.lan.lan.MainDispatcherExtension
import bi.lan.lan.data.model.CreateInvoiceResponse
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.presentation.screens.agent.AgentDepositViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class AgentDepositViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val repository = mockk<LightningRepository>()
    private lateinit var viewModel: AgentDepositViewModel

    @BeforeEach
    fun setUp() {
        viewModel = AgentDepositViewModel(repository)
    }

    @Test
    fun `initial state should be empty`() = runTest {
        assertEquals("", viewModel.amount.value)
        assertNull(viewModel.invoice.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `updateAmount should update amount flow`() = runTest {
        viewModel.updateAmount("5000")
        assertEquals("5000", viewModel.amount.value)
    }

    @Test
    fun `createInvoice should emit invoice on success`() = runTest {
        val amount = 5000L
        val invoice = CreateInvoiceResponse(paymentRequest = "lnbc...", rHash = "hash")
        coEvery { repository.createInvoice(amount, any(), any()) } returns NetworkResult.Success(invoice)

        viewModel.updateAmount(amount.toString())
        
        viewModel.invoice.test {
            assertNull(awaitItem())
            viewModel.createInvoice()
            assertEquals(invoice, awaitItem())
        }
    }

    @Test
    fun `createInvoice should emit error on failure`() = runTest {
        val amount = 5000L
        val errorMessage = "Failed to create invoice"
        coEvery { repository.createInvoice(amount, any(), any()) } returns NetworkResult.Error(message = errorMessage)

        viewModel.updateAmount(amount.toString())
        
        viewModel.error.test {
            assertNull(awaitItem())
            viewModel.createInvoice()
            assertEquals(errorMessage, awaitItem())
        }
    }
}
