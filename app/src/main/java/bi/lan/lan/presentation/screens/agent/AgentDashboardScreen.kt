package bi.lan.lan.presentation.screens.agent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bi.lan.lan.presentation.components.StatusBadge
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDashboardScreen(
    viewModel: AgentDashboardViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val agent by viewModel.agent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Agent Dashboard") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                agent?.let { a ->
                    Text("Welcome, ${a.name}", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Text("Status: ")
                        StatusBadge(text = if (a.isOnline) "Online" else "Offline", isSuccess = a.isOnline)
                    }
                    // Additional agent features can be added here
                }
            }
        }
    }
}
