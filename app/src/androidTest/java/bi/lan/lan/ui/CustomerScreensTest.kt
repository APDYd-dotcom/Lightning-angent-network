package bi.lan.lan.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import bi.lan.lan.data.model.BalanceResponse
import bi.lan.lan.data.model.CreateInvoiceResponse
import bi.lan.lan.data.model.HealthResponse
import bi.lan.lan.data.model.WalletBalance
import bi.lan.lan.presentation.screens.customer.CustomerHomeViewModel
import bi.lan.lan.presentation.screens.customer.CustomerHomeScreen
import bi.lan.lan.presentation.screens.customer.CustomerInvoiceViewModel
import bi.lan.lan.presentation.screens.customer.CustomerCreateInvoiceScreen
import bi.lan.lan.ui.theme.LANTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class CustomerScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun customerHomeScreen_ShowsBalance() {
        val viewModel = mockk<CustomerHomeViewModel>(relaxed = true)
        val balance = BalanceResponse(walletBalance = WalletBalance(totalBalance = 12345L))
        
        every { viewModel.balance } returns MutableStateFlow(balance)
        every { viewModel.health } returns MutableStateFlow(HealthResponse(status = "OK"))
        every { viewModel.isLoading } returns MutableStateFlow(false)

        composeTestRule.setContent {
            LANTheme {
                CustomerHomeScreen(
                    vm = viewModel,
                    onCreateInvoice = {},
                    onPayInvoice = {},
                    onDecodeInvoice = {},
                    onTransactions = {},
                    onNodeInfo = {},
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("12,345 sats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Customer Space").assertIsDisplayed()
    }

    @Test
    fun customerCreateInvoiceScreen_InputAndClick() {
        val viewModel = mockk<CustomerInvoiceViewModel>(relaxed = true)
        
        every { viewModel.amount } returns MutableStateFlow("1000")
        every { viewModel.memo } returns MutableStateFlow("Coffee")
        every { viewModel.result } returns MutableStateFlow(null)
        every { viewModel.isLoading } returns MutableStateFlow(false)
        every { viewModel.error } returns MutableStateFlow(null)

        composeTestRule.setContent {
            LANTheme {
                CustomerCreateInvoiceScreen(vm = viewModel, onBack = {})
            }
        }

        composeTestRule.onNodeWithText("Amount (sats)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate Withdrawal Invoice").performClick()
    }
}
