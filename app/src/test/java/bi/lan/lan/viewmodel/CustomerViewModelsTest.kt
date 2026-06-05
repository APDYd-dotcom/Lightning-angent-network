package bi.lan.lan.viewmodel

import app.cash.turbine.test
import bi.lan.lan.MainDispatcherExtension
import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
import bi.lan.lan.presentation.screens.customer.CustomerHomeViewModel
import bi.lan.lan.presentation.screens.customer.CustomerInvoiceViewModel
import bi.lan.lan.presentation.screens.customer.CustomerPaymentViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerViewModelsTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val repository = mockk<LightningRepository>(relaxed = true)

    @Test
    fun `CustomerHomeViewModel load should update balance and health`() = runTest {
        val health = HealthResponse(status = "OK")
        val balance = BalanceResponse(walletBalance = WalletBalance(totalBalance = 1000L))
        coEvery { repository.getHealth() } returns NetworkResult.Success(health)
        coEvery { repository.getBalance() } returns NetworkResult.Success(balance)

        val viewModel = CustomerHomeViewModel(repository)
        viewModel.load()

        assertEquals(health, viewModel.health.value)
        assertEquals(balance, viewModel.balance.value)
    }

    @Test
    fun `CustomerInvoiceViewModel createInvoice should update result on success`() = runTest {
        val invoice = CreateInvoiceResponse(paymentRequest = "lnbc100")
        coEvery { repository.createInvoice(any(), any(), any()) } returns NetworkResult.Success(invoice)

        val viewModel = CustomerInvoiceViewModel(repository)
        viewModel.updateAmount("100")
        viewModel.createInvoice()

        assertEquals(invoice, viewModel.result.value)
    }

    @Test
    fun `CustomerPaymentViewModel payInvoice should update result and refresh list`() = runTest {
        val customerRepo = mockk<LightningRepository>(relaxed = true)
        val agentRepo = mockk<LightningRepository>(relaxed = true)
        val paymentResponse = PaymentResponse(paymentHash = "hash")
        
        coEvery { agentRepo.getInvoices() } returns NetworkResult.Success(emptyList())
        coEvery { customerRepo.payInvoice(any(), any()) } returns NetworkResult.Success(paymentResponse)

        val viewModel = CustomerPaymentViewModel(customerRepo, agentRepo)
        viewModel.updatePayReq("lnbc100")
        
        viewModel.result.test {
            assertTrue(awaitItem() == null)
            viewModel.payInvoice()
            assertEquals(paymentResponse, awaitItem())
        }
    }
}
