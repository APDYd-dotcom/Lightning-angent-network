package bi.lan.lan.viewmodel

import app.cash.turbine.test
import bi.lan.lan.MainDispatcherExtension
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.usecase.CreateInvoiceUseCase
import bi.lan.lan.domain.usecase.GetBalanceUseCase
import bi.lan.lan.domain.usecase.PayInvoiceUseCase
import bi.lan.lan.presentation.payment.PaymentState
import bi.lan.lan.presentation.payment.PaymentViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val createInvoiceUseCase = mockk<CreateInvoiceUseCase>()
    private val payInvoiceUseCase = mockk<PayInvoiceUseCase>()
    private val getBalanceUseCase = mockk<GetBalanceUseCase>()

    private lateinit var viewModel: PaymentViewModel

    @BeforeEach
    fun setUp() {
        coEvery { getBalanceUseCase() } returns NetworkResult.Success(0L)
        viewModel = PaymentViewModel(createInvoiceUseCase, payInvoiceUseCase, getBalanceUseCase)
    }

    @Test
    fun `initial state should be Idle and balance 0`() = runTest {
        viewModel.state.test {
            assertEquals(PaymentState.Idle, awaitItem())
        }
        viewModel.balance.test {
            assertEquals(0L, awaitItem())
        }
    }

    @Test
    fun `createInvoice should emit Loading then InvoiceCreated on success`() = runTest {
        val invoice = "lnbc1000..."
        coEvery { createInvoiceUseCase(1000, "Test") } coAnswers {
            kotlinx.coroutines.delay(1)
            NetworkResult.Success(invoice)
        }

        viewModel.state.test {
            assertEquals(PaymentState.Idle, awaitItem())
            viewModel.createInvoice(1000, "Test")
            assertEquals(PaymentState.Loading, awaitItem())
            assertEquals(PaymentState.InvoiceCreated(invoice), awaitItem())
        }
    }

    @Test
    fun `createInvoice should emit Loading then Error on failure`() = runTest {
        val error = "Failed"
        coEvery { createInvoiceUseCase(1000, "Test") } coAnswers {
            kotlinx.coroutines.delay(1)
            NetworkResult.Error(message = error)
        }

        viewModel.state.test {
            assertEquals(PaymentState.Idle, awaitItem())
            viewModel.createInvoice(1000, "Test")
            assertEquals(PaymentState.Loading, awaitItem())
            assertEquals(PaymentState.Error(error), awaitItem())
        }
    }

    @Test
    fun `payInvoice should emit Loading then PaymentSuccess and refresh balance`() = runTest {
        val hash = "hash123"
        val newBalance = 5000L
        coEvery { payInvoiceUseCase("lnbc...") } coAnswers {
            kotlinx.coroutines.delay(1)
            NetworkResult.Success(hash)
        }
        coEvery { getBalanceUseCase() } returns NetworkResult.Success(newBalance)

        viewModel.state.test {
            assertEquals(PaymentState.Idle, awaitItem())
            viewModel.payInvoice("lnbc...")
            assertEquals(PaymentState.Loading, awaitItem())
            assertEquals(PaymentState.PaymentSuccess(hash), awaitItem())
        }
        
        viewModel.balance.test {
            assertEquals(newBalance, awaitItem())
        }
    }
}
