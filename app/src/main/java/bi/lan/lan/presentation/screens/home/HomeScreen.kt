package bi.lan.lan.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bi.lan.lan.presentation.components.BalanceCard
import bi.lan.lan.presentation.components.QuickActionButton
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToDeposit: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAgents: () -> Unit
) {
    val wallet by viewModel.wallet.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWallet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lightning Network") },
                actions = {
                    IconButton(onClick = onNavigateToAgents) {
                        Icon(Icons.Filled.Person, contentDescription = "Agents")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            if (isLoading && wallet == null) {
                CircularProgressIndicator()
            } else {
                wallet?.let { w ->
                    BalanceCard(balance = w.balanceSats)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(icon = Icons.Filled.ArrowDownward, label = "Deposit", onClick = onNavigateToDeposit)
                        QuickActionButton(icon = Icons.Filled.ArrowUpward, label = "Withdraw", onClick = onNavigateToWithdraw)
                        QuickActionButton(icon = Icons.Filled.History, label = "History", onClick = onNavigateToTransactions)
                    }
                }
            }
        }
    }
}
