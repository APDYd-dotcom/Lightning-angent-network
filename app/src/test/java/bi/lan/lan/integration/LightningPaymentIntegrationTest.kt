package bi.lan.lan.integration

import app.cash.turbine.test
import bi.lan.lan.MainDispatcherExtension
import bi.lan.lan.data.model.*
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class LightningPaymentIntegrationTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val lightningRepository = mockk<LightningRepository>()
    
    // Use cases using the same mocked repository
    private val createInvoiceUseCase = CreateInvoiceUseCase(lightningRepository)
    private val payInvoiceUseCase = PayInvoiceUseCase(lightningRepository)
    private val getBalanceUseCase = GetBalanceUseCase(lightningRepository)

    @Test
    fun `full lightning payment flow integration test`() = runTest {
        // 1. Initial State
        coEvery { lightningRepository.getBalance() } returns NetworkResult.Success(
            BalanceResponse(walletBalance = WalletBalance(totalBalance = 10000L))
        )
        
        val viewModel = PaymentViewModel(createInvoiceUseCase, payInvoiceUseCase, getBalanceUseCase)
        
        viewModel.balance.test {
            assertEquals(10000L, awaitItem())
        }

        // 2. Agent creates invoice (1000 sats)
        val amount = 1000L
        val memo = "Hackathon Coffee"
        val generatedInvoice = "lnbc1000..."
        coEvery { lightningRepository.createInvoice(amount, memo, 3600) } coAnswers {
            kotlinx.coroutines.delay(1)
            NetworkResult.Success(
                CreateInvoiceResponse(paymentRequest = generatedInvoice, rHash = "hash123")
            )
        }

        viewModel.state.test {
            assertEquals(PaymentState.Idle, awaitItem())
            viewModel.createInvoice(amount, memo)
            assertEquals(PaymentState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is PaymentState.InvoiceCreated)
            assertEquals(generatedInvoice, (state as PaymentState.InvoiceCreated).invoice)
        }

        // 3. Customer pays the invoice
        val paymentHash = "payhash456"
        coEvery { lightningRepository.payInvoice(generatedInvoice, null) } coAnswers {
            kotlinx.coroutines.delay(1)
            NetworkResult.Success(
                PaymentResponse(paymentHash = paymentHash)
            )
        }
        // Balance updates after payment
        coEvery { lightningRepository.getBalance() } returns NetworkResult.Success(
            BalanceResponse(walletBalance = WalletBalance(totalBalance = 9000L))
        )

        viewModel.state.test {
            // Re-test from Idle or the current state. 
            // Note: Turbine 'test' on a StateFlow starts with the CURRENT value.
            // Since we just finished 'createInvoice', the current state is InvoiceCreated.
            val currentState = awaitItem() 
            assertTrue(currentState is PaymentState.InvoiceCreated)
            
            viewModel.payInvoice(generatedInvoice)
            
            assertEquals(PaymentState.Loading, awaitItem())
            val successState = awaitItem()
            assertTrue(successState is PaymentState.PaymentSuccess)
            assertEquals(paymentHash, (successState as PaymentState.PaymentSuccess).paymentHash)
        }

        // 4. Verify balance was updated
        viewModel.balance.test {
            assertEquals(9000L, awaitItem())
        }
    }
}
