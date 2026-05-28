package bi.lan.lan.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bi.lan.lan.presentation.components.AgentCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyAgentsScreen(
    viewModel: NearbyAgentsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onAgentSelected: (String) -> Unit
) {
    val agents by viewModel.agents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAgents()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nearby Agents") }) }
    ) { padding ->
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                items(agents) { agent ->
                    AgentCard(agent = agent, onClick = { onAgentSelected(agent.id) })
                }
            }
        }
    }
}
