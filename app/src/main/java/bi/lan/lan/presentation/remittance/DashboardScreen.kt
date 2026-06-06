package bi.lan.lan.presentation.remittance

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.presentation.components.*
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onRemittance: () -> Unit,
    onHistory: () -> Unit,
    onPay: () -> Unit,
    onAnalytics: () -> Unit,
    onProfile: () -> Unit,
    onRemittanceClick: (RemittanceEntity) -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val balance by viewModel.balance.collectAsState()
    val todayReceived by viewModel.todayReceivedTotal.collectAsState(initial = 0L)
    val pendingStats by viewModel.pendingStats.collectAsState(initial = 0 to 0L)
    val successStats by viewModel.successStats.collectAsState(initial = 0 to 0L)
    val recentRemittances by viewModel.recentRemittances.collectAsState(initial = emptyList())
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        bottomBar = {
            LANBottomAppBar(selectedIndex = 0, onItemSelected = {
                when(it) {
                    1 -> onHistory()
                    2 -> onProfile()
                }
            })
        },
        containerColor = BackgroundDark
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { 
                viewModel.refreshPendingRemittances()
                viewModel.loadBalance()
            },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Welcome Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hello, Agent",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Burundi Remittance Network",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark
                            )
                        }
                        Surface(
                            modifier = Modifier.size(48.dp).clickable { onProfile() },
                            shape = CircleShape,
                            color = SurfaceDark
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = PrimaryGreen)
                            }
                        }
                    }
                }

                // Balance Card
                item {
                    FintechBalanceCard(
                        balance = balance?.walletBalance?.totalBalance ?: 0,
                        confirmedBalance = balance?.walletBalance?.confirmedBalance ?: 0
                    )
                }

                // Quick Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(Icons.Default.Add, "Request", onClick = onRemittance)
                        QuickActionButton(Icons.Default.History, "History", onClick = onHistory)
                        QuickActionButton(Icons.Default.CallMade, "Pay", onClick = { onPay() })
                        QuickActionButton(Icons.Default.BarChart, "Analytics", onClick = onAnalytics)
                    }
                }

                // Statistics
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedStatCard(
                            title = "Today",
                            value = "$todayReceived sats",
                            icon = Icons.Default.Today,
                            modifier = Modifier.weight(1f)
                        )
                        AnimatedStatCard(
                            title = "Pending",
                            value = "${pendingStats.first}",
                            icon = Icons.Default.Schedule,
                            indicatorColor = StatusWarning,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Recent Remittances
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Remittances",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            TextButton(onClick = onHistory) {
                                Text("See All", color = PrimaryGreen)
                            }
                        }
                        
                        if (recentRemittances.isEmpty() && !isRefreshing) {
                            Text(
                                "No recent remittances found.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            recentRemittances.forEach { remittance ->
                                TransactionRow(
                                    title = remittance.description.ifEmpty { "Remittance" },
                                    subtitle = "Ref: ${remittance.reference}",
                                    amount = "${remittance.amount} sats",
                                    status = remittance.status,
                                    onClick = { onRemittanceClick(remittance) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
