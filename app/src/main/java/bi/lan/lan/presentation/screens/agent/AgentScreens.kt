package bi.lan.lan.presentation.screens.agent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.data.model.InvoiceResponse
import bi.lan.lan.presentation.components.*
import bi.lan.lan.presentation.screens.customer.NodeInfoViewModel
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.qualifier.named

// ─── Agent Home ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentHomeScreen(
    vm: AgentHomeViewModel = koinViewModel(),
    onRemittance: () -> Unit,
    onHistory: () -> Unit,
    onAnalytics: () -> Unit,
    onProfile: () -> Unit,
    onInvoiceDetails: (InvoiceResponse) -> Unit
) {
    val balance by vm.balance.collectAsState()
    val info by vm.info.collectAsState()
    val recentInvoices by vm.recentInvoices.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    LaunchedEffect(Unit) { vm.load() }

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
            isRefreshing = isLoading,
            onRefresh = { vm.load() },
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
                                text = "Hello, ${info?.alias ?: "Agent"}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Welcome back to LAN",
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
                        QuickActionButton(Icons.Default.Add, "Remittance", onClick = onRemittance)
                        QuickActionButton(Icons.Default.History, "History", onClick = onHistory)
                        QuickActionButton(Icons.Default.QrCode, "Invoice", onClick = onRemittance)
                        QuickActionButton(Icons.Default.BarChart, "Analytics", onClick = onAnalytics)
                    }
                }

                // Statistics
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Active Channels",
                            value = info?.numActiveChannels?.toString() ?: "0",
                            icon = Icons.Default.Hub,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Peers",
                            value = info?.numPeers?.toString() ?: "0",
                            icon = Icons.Default.Group,
                            color = SecondaryBlue,
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
                        
                        if (recentInvoices.isEmpty() && !isLoading) {
                            Text(
                                "No recent remittances found.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            recentInvoices.forEach { invoice ->
                                TransactionRow(
                                    title = invoice.memo.ifEmpty { "Remittance" },
                                    subtitle = "Reference: ${invoice.rHash.take(8)}",
                                    amount = "${invoice.amount} sats",
                                    status = if (invoice.settled) "PAID" else "PENDING",
                                    onClick = { onInvoiceDetails(invoice) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Unified Remittance ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemittanceScreen(
    vm: AgentDepositViewModel = koinViewModel(),
    onBack: () -> Unit,
    onShareInvoice: (String, String) -> Unit // invoice, amount
) {
    val amount by vm.amount.collectAsState()
    val memo by vm.memo.collectAsState()
    val invoice by vm.invoice.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Remittance", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimaryDark,
                    navigationIconContentColor = TextPrimaryDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (invoice == null) {
                GlassCard {
                    Text(
                        text = "Remittance Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Enter the amount and a brief description for this remittance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    LANTextField(
                        value = amount,
                        onValueChange = { vm.updateAmount(it) },
                        label = "Amount (sats)",
                        placeholder = "0",
                        leadingIcon = Icons.Default.Payments,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    LANTextField(
                        value = memo,
                        onValueChange = { vm.updateMemo(it) },
                        label = "Description / Memo",
                        placeholder = "e.g. Cash Remittance for John",
                        leadingIcon = Icons.AutoMirrored.Filled.Notes
                    )
                    
                    if (error != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(error!!, color = StatusError, style = MaterialTheme.typography.labelSmall)
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    GradientButton(
                        text = "Generate Remittance",
                        isLoading = isLoading,
                        onClick = { vm.createInvoice() }
                    )
                }
            } else {
                // Generated State
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(64.dp), tint = PrimaryGreen)
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        text = "Remittance Ready",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        text = "Share this invoice or ask the recipient to scan the QR code.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    GlassCard {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            QrCodeImage(content = invoice!!.paymentRequest, modifier = Modifier.fillMaxSize())
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Text(
                            text = "Lightning Invoice",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = invoice!!.paymentRequest.take(20) + "..." + invoice!!.paymentRequest.takeLast(20),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryDark,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    GradientButton(
                        text = "Share via WhatsApp",
                        icon = Icons.Default.Share,
                        onClick = { onShareInvoice(invoice!!.paymentRequest, amount) }
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    TextButton(onClick = onBack) {
                        Text("Back to Dashboard", color = TextSecondaryDark)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentTransactionsScreen(
    vm: AgentTransactionsViewModel = koinViewModel(),
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onInvoiceDetail: (String) -> Unit,
    onPaymentDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val invoices by vm.invoices.collectAsState()
    val payments by vm.payments.collectAsState()
    val remittances by vm.remittances.collectAsState(initial = emptyList<RemittanceEntity>())
    val isLoading by vm.isLoading.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Activity", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimaryDark,
                    navigationIconContentColor = TextPrimaryDark
                )
            )
        },
        bottomBar = {
            LANBottomAppBar(selectedIndex = 1, onItemSelected = {
                when(it) {
                    0 -> onHome()
                    2 -> onProfile()
                }
            })
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = tab,
                containerColor = BackgroundDark,
                contentColor = PrimaryGreen,
                divider = {}
            ) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    text = { Text("Invoices") },
                    selectedContentColor = PrimaryGreen,
                    unselectedContentColor = TextSecondaryDark
                )
                Tab(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    text = { Text("Payments") },
                    selectedContentColor = PrimaryGreen,
                    unselectedContentColor = TextSecondaryDark
                )
                Tab(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    text = { Text("Remittances") },
                    selectedContentColor = PrimaryGreen,
                    unselectedContentColor = TextSecondaryDark
                )
            }

            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { vm.load() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    if (tab == 0) {
                        items(invoices) { invoice ->
                            TransactionRow(
                                title = invoice.memo.ifEmpty { "Received Payment" },
                                subtitle = "Ref: ${invoice.rHash.take(8)}",
                                amount = "${invoice.amount} sats",
                                status = if (invoice.settled) "PAID" else "PENDING",
                                onClick = { onInvoiceDetail(invoice.rHash) }
                            )
                        }
                    } else if (tab == 1) {
                        items(payments) { payment ->
                            TransactionRow(
                                title = "Sent Payment",
                                subtitle = "Ref: ${payment.paymentHash.take(8)}",
                                amount = "${payment.value} sats",
                                status = payment.status,
                                onClick = { onPaymentDetail(payment.paymentHash) }
                            )
                        }
                    } else {
                        items(remittances) { remittance ->
                            TransactionRow(
                                title = remittance.description.ifEmpty { if (remittance.type == "INBOUND") "Remittance Received" else "Remittance Sent" },
                                subtitle = "Ref: ${remittance.reference}",
                                amount = "${remittance.amount} sats",
                                status = remittance.status,
                                onClick = { /* Navigate to detail if needed */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Profile Screen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: NodeInfoViewModel = koinViewModel(named("agent")),
    onHome: () -> Unit,
    onHistory: () -> Unit,
    onBack: () -> Unit
) {
    val info by vm.info.collectAsState()
    val balance by vm.balance.collectAsState()
    val accountDetails by vm.accountDetails.collectAsState()
    val totalTransactions by vm.totalTransactions.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimaryDark,
                    navigationIconContentColor = TextPrimaryDark
                )
            )
        },
        bottomBar = {
            LANBottomAppBar(selectedIndex = 2, onItemSelected = {
                when(it) {
                    0 -> onHome()
                    1 -> onHistory()
                }
            })
        },
        containerColor = BackgroundDark
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { vm.load() },
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(64.dp), tint = PrimaryGreen)
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = accountDetails?.username ?: info?.alias ?: "Agent",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
                
                Text(
                    text = "Lightning Agent Network",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(32.dp))
                
                GlassCard {
                    ProfileInfoRow("Wallet ID", accountDetails?.id?.take(16) ?: info?.identityPubkey?.take(16) ?: "...", true)
                    HorizontalDivider(color = DarkOutline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))
                    ProfileInfoRow("Total Balance", "${balance?.walletBalance?.totalBalance ?: 0} sats")
                    ProfileInfoRow("Confirmed", "${balance?.walletBalance?.confirmedBalance ?: 0} sats")
                    HorizontalDivider(color = DarkOutline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))
                    ProfileInfoRow("Transactions", totalTransactions.toString())
                    
                    val memberSince = accountDetails?.createdAt?.let {
                        java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(it * 1000))
                    } ?: "March 2024"
                    ProfileInfoRow("Member Since", memberSince)
                }
                
                Spacer(Modifier.height(24.dp))
                
                SectionHeader("Network Status")
                Spacer(Modifier.height(12.dp))
                GlassCard {
                    StatusRow("Sync Status", if (info?.syncedToChain == true) "Synced" else "Syncing", info?.syncedToChain == true)
                    StatusRow("Node Version", info?.version ?: "Unknown", true)
                    StatusRow("Block Height", info?.blockHeight?.toString() ?: "0", true)
                }
                
                Spacer(Modifier.height(32.dp))
                
                OutlinedButton(
                    onClick = { /* Logout or Reset */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.5f))
                ) {
                    Text("Disconnect Agent Node", color = StatusError)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String, isCopyable: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            if (isCopyable) {
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp), tint = PrimaryGreen)
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String, isSuccess: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
        StatusChip(if (isSuccess) "PAID" else "PENDING")
    }
}
