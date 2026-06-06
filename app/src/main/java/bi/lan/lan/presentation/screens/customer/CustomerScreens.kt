package bi.lan.lan.presentation.screens.customer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.data.model.*
import bi.lan.lan.presentation.components.*
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.qualifier.named

// ─── Customer Home ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    vm: CustomerHomeViewModel = koinViewModel(),
    onCreateInvoice: () -> Unit,
    onPayInvoice: () -> Unit,
    onDecodeInvoice: () -> Unit,
    onTransactions: () -> Unit,
    onNodeInfo: () -> Unit,
    onRemittance: () -> Unit,
    onRemittanceHistory: () -> Unit,
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
                            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                            .background(Brush.verticalGradient(listOf(PrimaryGreen, PrimaryGreenDark)))
                            .padding(horizontal = 20.dp).padding(top = 48.dp, bottom = 32.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                                Spacer(Modifier.width(4.dp))
                                Text("Customer Space", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(20.dp))
                            if (isLoading && balance == null) {
                                CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        SectionHeader("Remittance History")
                        Spacer(Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onRemittanceHistory() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, null, tint = PrimaryGreen)
                                Spacer(Modifier.width(12.dp))
                                Text("View All Remittance Requests", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Balance
                item {
                    Column(Modifier.padding(horizontal = 20.dp).padding(top = 20.dp)) {
                        FintechBalanceCard(
                            balance = balance?.walletBalance?.totalBalance ?: 0,
                            confirmedBalance = balance?.walletBalance?.confirmedBalance ?: 0
                        )
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
                        SectionHeader("Payments")
                        Spacer(Modifier.height(12.dp))
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                QuickActionButton(Icons.Default.CallReceived, "Receive", PrimaryGreen, onCreateInvoice)
                                QuickActionButton(Icons.Default.CallMade, "Send", PrimaryGreen, onPayInvoice)
                                QuickActionButton(Icons.Default.VolunteerActivism, "Remittance", PrimaryGreen, onRemittance)
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        SectionHeader("Remittance History")
                        Spacer(Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onRemittanceHistory() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, null, tint = PrimaryGreen)
                                Spacer(Modifier.width(12.dp))
                                Text("View All Remittance Requests", fontWeight = FontWeight.Bold)
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

// ─── Create Invoice ───────────────────────────────────────────────────────────

@Composable
fun CustomerCreateInvoiceScreen(vm: CustomerInvoiceViewModel = koinViewModel(), onBack: () -> Unit) {
    val amount by vm.amount.collectAsState()
    val memo by vm.memo.collectAsState()
    val expiry by vm.expiry.collectAsState()
    val result by vm.result.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    Column(Modifier.fillMaxSize().background(BackgroundLight).verticalScroll(rememberScrollState())) {
        // Header
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).background(PrimaryGreen).padding(horizontal = 20.dp).statusBarsPadding().padding(top = 20.dp, bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                Text("Create Cash-Out Invoice", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
            }
        }

        Column(Modifier.padding(20.dp)) {
            if (result == null) {
                Text(
                    "Enter the amount you wish to withdraw in cash from an agent.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(24.dp))
                
                LANTextField(
                    value = amount,
                    onValueChange = { vm.updateAmount(it) },
                    label = "Amount (sats)",
                    placeholder = "e.g. 1000",
                    leadingIcon = Icons.Default.Payments,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(12.dp))
                LANTextField(
                    value = memo,
                    onValueChange = { vm.updateMemo(it) },
                    label = "Description (Optional)",
                    placeholder = "Withdrawal reference",
                    leadingIcon = Icons.AutoMirrored.Filled.Notes
                )

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = StatusError, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(32.dp))
                LoadingButton("Generate Withdrawal Invoice", isLoading, PrimaryGreen) { vm.createInvoice() }
            } else {
                // Success State
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
                    Text("Invoice Created Successfully!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "The agent can now see your withdrawal request and process your cash payment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    QrCodeImage(content = result!!.paymentRequest)
                    Spacer(Modifier.height(24.dp))
                    CopyableTextCard("Withdrawal Invoice", result!!.paymentRequest, PrimaryGreen)
                    
                    Spacer(Modifier.height(48.dp))
                    
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

// ─── Pay Invoice ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPayInvoiceScreen(
    vm: CustomerPaymentViewModel = koinViewModel(),
    onRemittanceHistory: () -> Unit,
    onBack: () -> Unit
) {
    val payReq by vm.payReq.collectAsState()
    val amount by vm.amount.collectAsState()
    val result by vm.result.collectAsState()
    val decoded by vm.decoded.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val agentInvoices by vm.agentInvoices.collectAsState()
    val isSuccess by vm.isSuccess.collectAsState()

    var showScanner by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showScanner = true
    }

    if (showScanner) {
        Box(Modifier.fillMaxSize()) {
            QrCodeScanner(
                onQrCodeScanned = { qr ->
                    vm.updatePayReq(qr)
                    showScanner = false
                },
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = { showScanner = false },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).statusBarsPadding()
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Box(
                modifier = Modifier.align(Alignment.Center).size(250.dp).border(2.dp, PrimaryGreen, RoundedCornerShape(16.dp))
            )
        }
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(if (isSuccess) "Payment Success" else if (decoded != null) "Review Payment" else "Send Payment", fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = { if (decoded != null && !isSuccess) vm.reset() else onBack() }) { 
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null) 
                        }
                    },
                    actions = {
                        if (decoded == null && !isSuccess) {
                            IconButton(onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }) {
                                Icon(Icons.Default.QrCodeScanner, null)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark,
                        titleContentColor = TextPrimaryDark,
                        navigationIconContentColor = TextPrimaryDark,
                        actionIconContentColor = TextPrimaryDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when {
                    isSuccess && result != null && decoded != null -> {
                        PaymentSuccessContent(
                            result = result!!,
                            decoded = decoded!!,
                            onDone = { vm.reset(); onBack() }
                        )
                    }
                    decoded != null -> {
                        PaymentPreviewContent(
                            decoded = decoded!!,
                            isLoading = isLoading,
                            error = error,
                            onConfirm = { vm.payInvoice() },
                            onCancel = { vm.reset() }
                        )
                    }
                    else -> {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
                            SectionHeader("Pending Deposit Requests", color = TextPrimaryDark)
                            Text(
                                "Select a request from an agent to fulfill your cash deposit.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryDark
                            )
                            Spacer(Modifier.height(16.dp))

                            if (agentInvoices.isEmpty() && !isLoading) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("⏳", fontSize = 32.sp)
                                        Spacer(Modifier.height(8.dp))
                                        Text("No pending requests", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                        Text("Ask an agent to create a deposit invoice.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                    }
                                }
                            } else {
                                agentInvoices.forEach { invoice ->
                                    TransactionRow(
                                        title = "${invoice.amount} sats",
                                        subtitle = invoice.memo.ifEmpty { "Deposit Request" },
                                        amount = "",
                                        status = "PENDING",
                                        onClick = { vm.selectInvoice(invoice) }
                                    )
                                }
                            }

                            Spacer(Modifier.height(32.dp))
                            SectionHeader("Manual Payment", color = TextPrimaryDark)
                            Text("Paste a Lightning invoice or payment code below.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                            Spacer(Modifier.height(12.dp))
                            
                            LANTextField(
                                value = payReq,
                                onValueChange = { vm.updatePayReq(it) },
                                label = "Lightning Invoice",
                                placeholder = "lnbc...",
                                leadingIcon = Icons.Default.Link,
                                singleLine = false,
                                minLines = 3
                            )

                            if (error != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(error!!, color = StatusError, style = MaterialTheme.typography.labelSmall)
                            }

                            Spacer(Modifier.height(24.dp))
                            GradientButton(
                                text = "Review Payment",
                                isLoading = isLoading,
                                onClick = { 
                                    if (payReq.isNotBlank()) vm.decodeInvoice()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentPreviewContent(
    decoded: DecodeInvoiceResponse,
    isLoading: Boolean,
    error: String?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape).background(SecondaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Bolt, null, modifier = Modifier.size(40.dp), tint = SecondaryBlue)
        }
        
        Spacer(Modifier.height(24.dp))
        Text("Lightning Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimaryDark)
        Spacer(Modifier.height(8.dp))
        StatusChip(status = "VALID")

        Spacer(Modifier.height(32.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Receiver Information", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AccountBalanceWallet, null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Blink Network", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Text("Internal Wallet", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                }
            }
            
            HorizontalDivider(Modifier.padding(vertical = 20.dp), color = DarkOutline.copy(alpha = 0.5f))
            
            DetailRow("Amount", "${decoded.numSatoshis} sats", isDark = true)
            DetailRow("Description", decoded.description.ifEmpty { "No description" }, isDark = true)
            DetailRow("Reference", decoded.paymentHash.take(12).uppercase(), isDark = true)
            DetailRow("Expiry", if (decoded.expiry > 0) "${decoded.expiry}s" else "Never", isDark = true)
        }

        if (error != null) {
            Spacer(Modifier.height(16.dp))
            Text(error, color = StatusError, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(32.dp))

        GradientButton(
            text = "Confirm & Pay Now",
            isLoading = isLoading,
            onClick = onConfirm
        )
        
        Spacer(Modifier.height(12.dp))
        
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel Payment", color = TextSecondaryDark, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PaymentSuccessContent(
    result: PaymentResponse,
    decoded: DecodeInvoiceResponse,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(StatusSuccess.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(64.dp), tint = StatusSuccess)
        }
        
        Spacer(Modifier.height(32.dp))
        Text("Payment Sent!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = TextPrimaryDark)
        Text("${decoded.numSatoshis} sats", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = PrimaryGreen)
        
        Spacer(Modifier.height(32.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            DetailRow("Reference", decoded.paymentHash.take(12).uppercase(), isDark = true)
            DetailRow("Transaction ID", result.paymentHash.take(16) + "...", isDark = true)
            DetailRow("Date", java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()), isDark = true)
            DetailRow("Status", "PAID", isDark = true)
        }
        
        Spacer(Modifier.height(48.dp))
        
        GradientButton(
            text = "Share Receipt",
            icon = Icons.Default.Share,
            onClick = { 
                val uri = bi.lan.lan.core.utils.ReceiptGenerator.generateReceipt(
                    context = context,
                    amount = decoded.numSatoshis,
                    reference = decoded.paymentHash.take(12).uppercase(),
                    txId = result.paymentHash,
                    date = System.currentTimeMillis(),
                    status = "PAID"
                )
                if (uri != null) {
                    val message = "✅ Payment of ${decoded.numSatoshis} sats sent successfully via LAN.\nRef: ${decoded.paymentHash.take(12).uppercase()}"
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                        putExtra(android.content.Intent.EXTRA_TEXT, message)
                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(android.content.Intent.createChooser(intent, "Share Receipt"))
                }
            }
        )
        
        Spacer(Modifier.height(16.dp))
        
        TextButton(onClick = onDone) {
            Text("Back to Dashboard", color = PrimaryGreen, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isDark: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = if (isDark) TextSecondaryDark else TextSecondary)
        if (label == "Status") {
            StatusChip(status = value)
        } else {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = if (isDark) TextPrimaryDark else TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Decode Invoice ───────────────────────────────────────────────────────────

@Composable
fun CustomerDecodeInvoiceScreen(vm: CustomerPaymentViewModel = koinViewModel(), onBack: () -> Unit) {
    val payReq by vm.payReq.collectAsState()
    val decoded by vm.decoded.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    Column(Modifier.fillMaxSize().background(BackgroundLight).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).background(PrimaryGreen).padding(horizontal = 20.dp).padding(top = 48.dp, bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SurfaceWhite) }
                Text("Decode Invoice", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.padding(20.dp)) {
            LANTextField(
                value = payReq,
                onValueChange = { vm.updatePayReq(it) },
                label = "Payment Request (lnbc...)",
                placeholder = "Invoice to decode",
                leadingIcon = Icons.Default.Search,
                singleLine = false,
                minLines = 3
            )
            if (error != null) { Spacer(Modifier.height(8.dp)); Text(error!!, color = StatusError) }
            Spacer(Modifier.height(16.dp))
            LoadingButton("Decode", isLoading, PrimaryGreen) { vm.decodeInvoice() }

            decoded?.let { d ->
                Spacer(Modifier.height(24.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRow("Amount", "${d.numSatoshis} sats")
                        InfoRow("Description", d.description.ifEmpty { "-" })
                        InfoRow("Expiry", "${d.expiry}s")
                        InfoRow("CLTV Expiry", d.cltvExpiry.toString())
                        Spacer(Modifier.height(8.dp))
                        CopyableTextCard("Destination", d.destination, PrimaryGreen)
                        Spacer(Modifier.height(8.dp))
                        CopyableTextCard("Payment Hash", d.paymentHash, PrimaryGreen)
                    }
                }
            }
        }
    }
}

// ─── Transactions ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerTransactionsScreen(
    vm: CustomerTransactionsViewModel = koinViewModel(),
    onHome: () -> Unit,
    onNodeInfo: () -> Unit,
    onPaymentDetail: (String) -> Unit,
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
                    Text("Transactions", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }

            TabRow(selectedTabIndex = tab, containerColor = SurfaceWhite, contentColor = PrimaryGreen, indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[tab]), color = PrimaryGreen)
            }) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, selectedContentColor = PrimaryGreen, unselectedContentColor = TextSecondary) { Text("Invoices", Modifier.padding(12.dp)) }
                Tab(selected = tab == 1, onClick = { tab = 1 }, selectedContentColor = PrimaryGreen, unselectedContentColor = TextSecondary) { Text("Payments", Modifier.padding(12.dp)) }
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
                            if (invoices.isEmpty() && !isLoading) { item { Text("No invoices", color = TextSecondary, modifier = Modifier.padding(20.dp)) } }
                        } else {
                            items(payments) { payment ->
                                PaymentCard(payment) { onPaymentDetail(payment.paymentHash) }
                            }
                            if (payments.isEmpty() && !isLoading) { item { Text("No payments", color = TextSecondary, modifier = Modifier.padding(20.dp)) } }
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

// ─── Node Info ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerNodeInfoScreen(
    vm: NodeInfoViewModel = koinViewModel(named("customer")),
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
                        Text("Profile", style = MaterialTheme.typography.titleLarge, color = SurfaceWhite, fontWeight = FontWeight.Bold)
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
                        text = "Lightning Node Participant",
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
                            SectionHeader("Node Information")
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
