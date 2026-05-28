package bi.lan.lan.presentation.screens.withdraw

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bi.lan.lan.presentation.components.LoadingButton
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(
    viewModel: WithdrawViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val amount by viewModel.amount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val success by viewModel.success.collectAsState()

    LaunchedEffect(success) {
        if (success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Withdraw") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text("Amount (Sats)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            LoadingButton("Request Withdrawal", isLoading) {
                viewModel.requestWithdrawal("agent_1")
            }
        }
    }
}
