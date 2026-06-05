package bi.lan.lan.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import bi.lan.lan.data.model.CreateInvoiceResponse
import bi.lan.lan.presentation.screens.agent.AgentDepositViewModel
import bi.lan.lan.presentation.screens.agent.AgentReceiveDepositScreen
import bi.lan.lan.ui.theme.LANTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class AgentReceiveDepositScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = mockk<AgentDepositViewModel>(relaxed = true)

    @Test
    fun agentReceiveDepositScreen_InitialState_ShowsInputFields() {
        // Given
        every { viewModel.amount } returns MutableStateFlow("")
        every { viewModel.memo } returns MutableStateFlow("")
        every { viewModel.invoice } returns MutableStateFlow(null)
        every { viewModel.isLoading } returns MutableStateFlow(false)
        every { viewModel.error } returns MutableStateFlow(null)
        every { viewModel.invoiceStatus } returns MutableStateFlow(null)

        composeTestRule.setContent {
            LANTheme {
                AgentReceiveDepositScreen(vm = viewModel, onBack = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Receive Cash Deposit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amount (sats)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Memo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate Deposit Invoice").assertIsDisplayed()
    }

    @Test
    fun agentReceiveDepositScreen_AfterInvoiceGenerated_ShowsQrCode() {
        // Given
        val invoice = CreateInvoiceResponse(paymentRequest = "lnbc123456789", rHash = "hash")
        every { viewModel.amount } returns MutableStateFlow("5000")
        every { viewModel.memo } returns MutableStateFlow("Test Deposit")
        every { viewModel.invoice } returns MutableStateFlow(invoice)
        every { viewModel.isLoading } returns MutableStateFlow(false)
        every { viewModel.error } returns MutableStateFlow(null)
        every { viewModel.invoiceStatus } returns MutableStateFlow(null)

        composeTestRule.setContent {
            LANTheme {
                AgentReceiveDepositScreen(vm = viewModel, onBack = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Deposit Invoice Ready!").assertIsDisplayed()
        composeTestRule.onNodeWithText("lnbc123456789").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back to Home").assertIsDisplayed()
    }
}
