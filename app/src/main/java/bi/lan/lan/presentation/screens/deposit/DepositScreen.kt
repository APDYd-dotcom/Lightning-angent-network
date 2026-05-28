package bi.lan.lan.presentation.screens.deposit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bi.lan.lan.presentation.components.LoadingButton
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    viewModel: DepositViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val amount by viewModel.amount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val invoice by viewModel.invoice.collectAsState()

    LaunchedEffect(invoice) {
        if (invoice != null) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Deposit") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text("Amount (Sats)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            LoadingButton("Create Invoice", isLoading) {
                viewModel.createDeposit("agent_1") // mock agent id
            }
        }
    }
}

@Composable
fun PaymentSuccessScreen(onNavigateHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Payment Successful!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateHome) {
            Text("Back to Home")
        }
    }
}
