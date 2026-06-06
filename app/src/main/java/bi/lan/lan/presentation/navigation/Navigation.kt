package bi.lan.lan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import bi.lan.lan.presentation.screens.agent.*
import bi.lan.lan.presentation.screens.customer.*
import bi.lan.lan.presentation.screens.common.*
import bi.lan.lan.presentation.remittance.*
import bi.lan.lan.presentation.history.*
import bi.lan.lan.presentation.payment.PaymentDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNext = {
                navController.navigate("agent_home") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // ─── Main Unified Space ───────────────────────────────────────────────────
        composable("agent_home") {
            DashboardScreen(
                onRemittance = { navController.navigate("remittance_request") },
                onHistory = { navController.navigate("agent_transactions") },
                onPay = { navController.navigate("pay_invoice") },
                onAnalytics = { navController.navigate("analytics_screen") },
                onProfile = { navController.navigate("agent_node_info") },
                onRemittanceClick = { remittance -> 
                    navController.navigate("receipt_screen/${remittance.reference}")
                }
            )
        }
        
        composable("remittance_request") {
            RemittanceScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("pay_invoice") {
            CustomerPayInvoiceScreen(
                onRemittanceHistory = { navController.navigate("agent_transactions") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("analytics_screen") {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "receipt_screen/{reference}",
            arguments = listOf(navArgument("reference") { type = NavType.StringType })
        ) { backStackEntry ->
            val reference = backStackEntry.arguments?.getString("reference") ?: ""
            ReceiptScreen(
                reference = reference,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "payment_detail/{hash}",
            arguments = listOf(navArgument("hash") { type = NavType.StringType })
        ) { backStackEntry ->
            val hash = backStackEntry.arguments?.getString("hash") ?: ""
            PaymentDetailScreen(
                paymentHash = hash,
                onBack = { navController.popBackStack() }
            )
        }

        composable("agent_transactions") {
            AgentTransactionsScreen(
                onHome = { navController.navigate("agent_home") { popUpTo("agent_home") { inclusive = false } } },
                onProfile = { navController.navigate("agent_node_info") { launchSingleTop = true } },
                onInvoiceDetail = { rHash -> /* Details for received invoices if needed */ },
                onPaymentDetail = { hash -> navController.navigate("payment_detail/$hash") },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("agent_node_info") {
            ProfileScreen(
                onHome = { navController.navigate("agent_home") { popUpTo("agent_home") { inclusive = false } } },
                onHistory = { navController.navigate("agent_transactions") { launchSingleTop = true } },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
