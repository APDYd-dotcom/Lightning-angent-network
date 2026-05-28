package bi.lan.lan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import bi.lan.lan.presentation.screens.agent.AgentDashboardScreen
import bi.lan.lan.presentation.screens.auth.LoginScreen
import bi.lan.lan.presentation.screens.auth.OtpScreen
import bi.lan.lan.presentation.screens.auth.SplashScreen
import bi.lan.lan.presentation.screens.deposit.DepositScreen
import bi.lan.lan.presentation.screens.deposit.PaymentSuccessScreen
import bi.lan.lan.presentation.screens.home.HomeScreen
import bi.lan.lan.presentation.screens.home.NearbyAgentsScreen
import bi.lan.lan.presentation.screens.transactions.TransactionHistoryScreen
import bi.lan.lan.presentation.screens.withdraw.WithdrawScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNavigateToLogin = {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(onNavigateToOtp = { phone ->
                navController.navigate("otp/$phone")
            })
        }
        composable(
            "otp/{phone}",
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            OtpScreen(phone = phone, onNavigateToHome = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(
                onNavigateToDeposit = { navController.navigate("deposit") },
                onNavigateToWithdraw = { navController.navigate("withdraw") },
                onNavigateToTransactions = { navController.navigate("history") },
                onNavigateToAgents = { navController.navigate("agents") }
            )
        }
        composable("deposit") {
            DepositScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.navigate("success") { popUpTo("home") } }
            )
        }
        composable("withdraw") {
            WithdrawScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.navigate("success") { popUpTo("home") } }
            )
        }
        composable("success") {
            PaymentSuccessScreen(onNavigateHome = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            })
        }
        composable("history") {
            TransactionHistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("agents") {
            NearbyAgentsScreen(
                onNavigateBack = { navController.popBackStack() },
                onAgentSelected = { agentId ->
                    // For MVP, we just navigate to dashboard when an agent is selected to simulate agent login/dashboard
                    navController.navigate("agent_dashboard")
                }
            )
        }
        composable("agent_dashboard") {
            AgentDashboardScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
