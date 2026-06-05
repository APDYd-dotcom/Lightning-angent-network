package bi.lan.lan.viewmodel

import app.cash.turbine.test
import bi.lan.lan.MainDispatcherExtension
import bi.lan.lan.data.model.InvoiceResponse
import bi.lan.lan.data.model.PaymentResponse
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.presentation.screens.agent.AgentWithdrawalViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class AgentWithdrawalViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val agentRepo = mockk<LightningRepository>()
    private val customerRepo = mockk<LightningRepository>()
    private lateinit var viewModel: AgentWithdrawalViewModel

    @BeforeEach
    fun setUp() {
        coEvery { customerRepo.getInvoices() } returns NetworkResult.Success(emptyList())
        viewModel = AgentWithdrawalViewModel(agentRepo, customerRepo)
    }

    @Test
    fun `loadCustomerInvoices should update customerInvoices list`() = runTest {
        val invoices = listOf(
            InvoiceResponse(paymentRequest = "pr1", settled = false),
            InvoiceResponse(paymentRequest = "pr2", settled = true)
        )
        coEvery { customerRepo.getInvoices() } returns NetworkResult.Success(invoices)

        viewModel.loadCustomerInvoices()

        viewModel.customerInvoices.test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals("pr1", item[0].paymentRequest)
        }
    }

    @Test
    fun `confirmPayment should update payResult on success`() = runTest {
        val pr = "lnbc100"
        val payResponse = PaymentResponse(paymentHash = "hash123")
        coEvery { agentRepo.payInvoice(pr, null) } returns NetworkResult.Success(payResponse)
        coEvery { customerRepo.getInvoices() } returns NetworkResult.Success(emptyList())

        viewModel.updatePayReq(pr)
        
        viewModel.payResult.test {
            assertTrue(awaitItem() == null)
            viewModel.confirmPayment()
            assertEquals(payResponse, awaitItem())
        }
    }
}
