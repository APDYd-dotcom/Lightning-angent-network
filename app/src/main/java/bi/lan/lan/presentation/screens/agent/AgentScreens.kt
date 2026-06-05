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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
    onReceiveDeposit: () -> Unit,
    onSendWithdrawal: () -> Unit,
    onTransactions: () -> Unit,
    onNodeInfo: () -> Unit,
    onBack: () -> Unit
) {
    val health by vm.health.collectAsState()
    val balance by vm.balance.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
        PullToRefreshBox(
            isRefreshing = isLoading && balance != null,
            onRefresh = { vm.load() },
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Header
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                            .background(Brush.verticalGradient(listOf(PrimaryGreen, PrimaryGreenDark)))
                            .padding(horizontal = 20.dp).padding(top = 48.dp, bottom = 28.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                                Spacer(Modifier.width(4.dp))
                                Text("Agent Space", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(20.dp))
                            if (isLoading && balance == null) {
                                CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }

                // Balance
                item {
                    Column(Modifier.padding(horizontal = 20.dp).padding(top = 20.dp)) {
                        BalanceCard(balance, PrimaryGreen)
                    }
                }

                // Health
                item {
                    Column(Modifier.padding(horizontal = 20.dp).padding(top = 16.dp)) {
                        HealthStatusCard(health)
                    }
                }

                // Quick Actions
                item {
                    Column(Modifier.padding(horizontal = 20.dp).padding(top = 24.dp)) {
                        SectionHeader("Agent Operations")
                        Spacer(Modifier.height(12.dp))
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                            Column(Modifier.padding(8.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    QuickActionButton(Icons.Filled.CallReceived, "Receive\nDeposit", PrimaryGreen, onReceiveDeposit)
                                    QuickActionButton(Icons.Filled.CallMade, "Send\nWithdrawal", PrimaryGreen, onSendWithdrawal)
                                }
                            }
                        }
                    }
                }
            }
        }

        LANBottomAppBar(
            selectedIndex = 0,
            onItemSelected = { index ->
                when(index) {
                    1 -> onTransactions()
                    2 -> onNodeInfo()
                }
            }
        )
    }
}

// ─── Agent Receive Deposit ───────────────────────────────────────────────────

@Composable
fun AgentReceiveDepositScreen(
    vm: AgentDepositViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val amount by vm.amount.collectAsState()
    val memo by vm.memo.collectAsState()
    val invoice by vm.invoice.collectAsState()
    val status by vm.invoiceStatus.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    Column(Modifier.fillMaxSize().background(BackgroundLight).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).background(PrimaryGreen).padding(horizontal = 20.dp).statusBarsPadding().padding(top = 20.dp, bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                Text("Receive Cash Deposit", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
            }
        }

        Column(Modifier.padding(20.dp)) {
            if (invoice == null) {
                Text(
                    "Set the amount for the cash deposit you are receiving from the customer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(24.dp))

                LANTextField(
                    value = amount,
                    onValueChange = { vm.updateAmount(it) },
                    label = "Amount (sats)",
                    placeholder = "e.g. 5000",
                    leadingIcon = Icons.Default.Payments,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(12.dp))
                LANTextField(
                    value = memo,
                    onValueChange = { vm.updateMemo(it) },
                    label = "Memo",
                    placeholder = "Deposit reference",
                    leadingIcon = Icons.AutoMirrored.Filled.Notes
                )

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = StatusError)
                }

                Spacer(Modifier.height(32.dp))
                LoadingButton("Generate Deposit Invoice", isLoading, PrimaryGreen) { vm.createInvoice() }
            } else {
                // Success State - Hide keys as requested
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(StatusSuccess.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(48.dp), tint = StatusSuccess)
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Deposit Invoice Ready!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Ask the customer to scan this QR code to complete the cash deposit.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    QrCodeImage(content = invoice!!.paymentRequest)

                    Spacer(Modifier.height(24.dp))

                    CopyableTextCard("Lightning Invoice", invoice!!.paymentRequest, PrimaryGreen)

                    Spacer(Modifier.height(32.dp))
                    
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Back to Home", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── Agent Process Withdrawal ───────────────────────────────────────────────

@Composable
fun AgentProcessWithdrawalScreen(
    vm: AgentWithdrawalViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val payReq by vm.payReq.collectAsState()
    val decoded by vm.decoded.collectAsState()
    val result by vm.payResult.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val customerInvoices by vm.customerInvoices.collectAsState()

    Column(Modifier.fillMaxSize().background(BackgroundLight).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).background(PrimaryGreen).padding(horizontal = 20.dp).statusBarsPadding().padding(top = 20.dp, bottom = 24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                    Text("Confirm Withdrawal", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { vm.loadCustomerInvoices() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = SurfaceWhite)
                }
            }
        }

        Column(Modifier.padding(20.dp)) {
            SectionHeader("Pending Customer Withdrawals")
            Text(
                "Select a withdrawal request created by a customer to pay them their cash.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(16.dp))

            if (customerInvoices.isEmpty() && !isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⏳", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No pending customer requests", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Invoices created by customers for cash-out will appear here.", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    customerInvoices.forEach { invoice ->
                        val isSelected = payReq == invoice.paymentRequest
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { vm.selectInvoice(invoice) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.05f) else SurfaceWhite),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryGreen) else null,
                            elevation = CardDefaults.cardElevation(if (isSelected) 2.dp else 1.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                LightningLogo(size = 40, backgroundColor = PrimaryGreen.copy(alpha = 0.1f), logoColor = PrimaryGreen)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${invoice.amount} sats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(invoice.memo.ifEmpty { "Customer Withdrawal" }, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                                }
                                if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = PrimaryGreen)
                            }
                        }
                    }
                }
            }

            if (decoded != null) {
                Spacer(Modifier.height(24.dp))
                SectionHeader("Process Payment")
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRow("Amount to Pay", "${decoded!!.numSatoshis} sats")
                        InfoRow("Description", decoded!!.description.ifEmpty { "Withdrawal" })
                        InfoRow("Destination", decoded!!.destination.take(12) + "...")
                    }
                }

                Spacer(Modifier.height(24.dp))
                LoadingButton("Confirm & Pay Withdrawal", isLoading, PrimaryGreen) { vm.confirmPayment() }
            }

            if (result != null) {
                Spacer(Modifier.height(24.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = StatusSuccess.copy(alpha = 0.1f))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = StatusSuccess)
                        Spacer(Modifier.width(12.dp))
                        Text("Payment Successful!", color = StatusSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(error!!, color = StatusError)
            }

            // Manual fallback
            var showManual by remember { mutableStateOf(false) }
            Spacer(Modifier.height(32.dp))
            TextButton(onClick = { showManual = !showManual }, modifier = Modifier.fillMaxWidth()) {
                Text(if (showManual) "Hide Manual Input" else "Or enter invoice manually", color = TextSecondary)
            }

            if (showManual) {
                LANTextField(
                    value = payReq,
                    onValueChange = { vm.updatePayReq(it) },
                    label = "Paste Customer Invoice (lnbc...)",
                    placeholder = "lnbc...",
                    leadingIcon = Icons.Default.QrCode,
                    singleLine = false,
                    minLines = 3
                )
                Spacer(Modifier.height(12.dp))
                LoadingButton("Decode Manual Invoice", isLoading, PrimaryGreen) { vm.decode() }
            }
        }
    }
}

// ─── Agent Transactions ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentTransactionsScreen(
    vm: AgentTransactionsViewModel = koinViewModel(),
    onHome: () -> Unit,
    onNodeInfo: () -> Unit,
    onBack: () -> Unit
) {
    val invoices by vm.invoices.collectAsState()
    val payments by vm.payments.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val actionLoading by vm.actionLoading.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var selectedInvoice by remember { mutableStateOf<InvoiceResponse?>(null) }
    
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
        Column(Modifier.weight(1f)) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).background(PrimaryGreen).padding(horizontal = 20.dp).padding(top = 48.dp, bottom = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                    Text("Agent Transactions", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }

            TabRow(selectedTabIndex = tab, containerColor = SurfaceWhite, contentColor = PrimaryGreen, indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[tab]), color = PrimaryGreen)
            }) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, selectedContentColor = PrimaryGreen, unselectedContentColor = TextSecondary) { Text("Invoices (Received)", Modifier.padding(12.dp)) }
                Tab(selected = tab == 1, onClick = { tab = 1 }, selectedContentColor = PrimaryGreen, unselectedContentColor = TextSecondary) { Text("Payments (Sent)", Modifier.padding(12.dp)) }
            }

            PullToRefreshBox(
                isRefreshing = isLoading && (invoices.isNotEmpty() || payments.isNotEmpty()),
                onRefresh = { vm.load() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading && invoices.isEmpty() && payments.isEmpty()) {
                    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(8) { InvoiceCardSkeleton() }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                        if (tab == 0) {
                            items(invoices) { invoice ->
                                InvoiceCard(invoice) {
                                    if (!invoice.settled) {
                                        selectedInvoice = invoice
                                    }
                                }
                            }
                            if (invoices.isEmpty() && !isLoading) { item { Text("No invoices received", color = TextSecondary) } }
                        } else {
                            items(payments) { PaymentCard(it) }
                            if (payments.isEmpty() && !isLoading) { item { Text("No payments sent", color = TextSecondary) } }
                        }
                    }
                }
            }
        }

        LANBottomAppBar(
            selectedIndex = 1,
            onItemSelected = { index ->
                when(index) {
                    0 -> onHome()
                    2 -> onNodeInfo()
                }
            }
        )
    }

    selectedInvoice?.let { invoice ->
        PendingInvoiceBottomSheet(
            invoice = invoice,
            isLoading = actionLoading,
            onConfirm = {
                vm.checkInvoiceStatus(invoice.rHash) { isPaid ->
                    if (isPaid) {
                        selectedInvoice = null
                    }
                }
            },
            onDismiss = { selectedInvoice = null }
        )
    }
}

// ─── Agent Node Info ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentNodeInfoScreen(
    vm: NodeInfoViewModel = koinViewModel(named("agent")),
    onHome: () -> Unit,
    onTransactions: () -> Unit,
    onBack: () -> Unit
) {
    val info by vm.info.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
        Column(Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(PrimaryGreen, PrimaryGreenDark)))
                    .padding(horizontal = 20.dp)
                    .statusBarsPadding()
                    .padding(top = 20.dp, bottom = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                        Text("Agent Profile", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Profile Header
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = SurfaceWhite.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(48.dp), tint = SurfaceWhite)
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Text(
                        text = info?.alias ?: "Loading...",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SurfaceWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Authorized Lightning Agent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurfaceWhite.copy(alpha = 0.8f)
                    )
                }
            }
            
            PullToRefreshBox(
                isRefreshing = isLoading && info != null,
                onRefresh = { vm.load() },
                modifier = Modifier.fillMaxSize()
            ) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
                    if (isLoading && info == null) {
                        NodeInfoSkeleton()
                    } else {
                        info?.let { n ->
                            SectionHeader("Agent Node Details")
                            Spacer(Modifier.height(12.dp))
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                                Column(Modifier.padding(16.dp)) {
                                    InfoRow("Alias", n.alias)
                                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = DividerColor.copy(alpha = 0.5f))
                                    InfoRow("Active Channels", n.numActiveChannels.toString())
                                    InfoRow("Peers", n.numPeers.toString())
                                    InfoRow("Block Height", n.blockHeight.toString())
                                    InfoRow("Synced (Chain)", if (n.syncedToChain) "✅" else "❌")
                                    InfoRow("Synced (Graph)", if (n.syncedToGraph) "✅" else "❌")
                                    InfoRow("Version", n.version)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            SectionHeader("Identity")
                            Spacer(Modifier.height(12.dp))
                            CopyableTextCard("Identity Pubkey", n.identityPubkey, PrimaryGreen)
                        }
                    }
                }
            }
        }

        LANBottomAppBar(
            selectedIndex = 2,
            onItemSelected = { index ->
                when(index) {
                    0 -> onHome()
                    1 -> onTransactions()
                }
            }
        )
    }
}
