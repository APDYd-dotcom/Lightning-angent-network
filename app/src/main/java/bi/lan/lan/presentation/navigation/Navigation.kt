package bi.lan.lan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bi.lan.lan.presentation.screens.agent.*
import bi.lan.lan.presentation.screens.customer.*
import bi.lan.lan.presentation.screens.common.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        // ─── Entry Point ──────────────────────────────────────────────────────────
        composable("splash") {
            SplashScreen(onNext = {
                navController.navigate("role_selection") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("role_selection") {
            RoleSelectionScreen(
                onCustomer = { navController.navigate("customer_home") },
                onAgent = { navController.navigate("agent_home") }
            )
        }

        // ─── Customer Space ───────────────────────────────────────────────────────
        composable("customer_home") {
            CustomerHomeScreen(
                onCreateInvoice = { navController.navigate("customer_create_invoice") },
                onPayInvoice = { navController.navigate("customer_pay_invoice") },
                onDecodeInvoice = { navController.navigate("customer_decode_invoice") },
                onTransactions = { navController.navigate("customer_transactions") },
                onNodeInfo = { navController.navigate("customer_node_info") },
                onBack = { navController.navigate("role_selection") { popUpTo("role_selection") { inclusive = true } } }
            )
        }
        composable("customer_create_invoice") {
            CustomerCreateInvoiceScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("customer_pay_invoice") {
            CustomerPayInvoiceScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("customer_decode_invoice") {
            CustomerDecodeInvoiceScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("customer_transactions") {
            CustomerTransactionsScreen(
                onHome = { navController.navigate("customer_home") { popUpTo("customer_home") { inclusive = false } } },
                onNodeInfo = { navController.navigate("customer_node_info") { launchSingleTop = true } },
                onBack = { navController.popBackStack() }
            )
        }
        composable("customer_node_info") {
            CustomerNodeInfoScreen(
                onHome = { navController.navigate("customer_home") { popUpTo("customer_home") { inclusive = false } } },
                onTransactions = { navController.navigate("customer_transactions") { launchSingleTop = true } },
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Agent Space ──────────────────────────────────────────────────────────
        composable("agent_home") {
            AgentHomeScreen(
                onReceiveDeposit = { navController.navigate("agent_receive_deposit") },
                onSendWithdrawal = { navController.navigate("agent_send_withdrawal") },
                onTransactions = { navController.navigate("agent_transactions") },
                onNodeInfo = { navController.navigate("agent_node_info") },
                onBack = { navController.navigate("role_selection") { popUpTo("role_selection") { inclusive = true } } }
            )
        }
        composable("agent_receive_deposit") {
            AgentReceiveDepositScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("agent_send_withdrawal") {
            AgentProcessWithdrawalScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("agent_transactions") {
            AgentTransactionsScreen(
                onHome = { navController.navigate("agent_home") { popUpTo("agent_home") { inclusive = false } } },
                onNodeInfo = { navController.navigate("agent_node_info") { launchSingleTop = true } },
                onBack = { navController.popBackStack() }
            )
        }
        composable("agent_node_info") {
            // Reuses Customer's NodeInfoViewModel but gets agent NodeInfoViewModel qualifier from Koin (handled inside AgentNodeInfoScreen)
            AgentNodeInfoScreen(
                onHome = { navController.navigate("agent_home") { popUpTo("agent_home") { inclusive = false } } },
                onTransactions = { navController.navigate("agent_transactions") { launchSingleTop = true } },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
