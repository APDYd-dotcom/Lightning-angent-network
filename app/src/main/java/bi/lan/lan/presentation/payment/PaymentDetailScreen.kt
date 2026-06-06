package bi.lan.lan.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.lan.lan.data.model.PaymentHistoryItem
import bi.lan.lan.presentation.components.*
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailScreen(
    paymentHash: String,
    onBack: () -> Unit,
    vm: PaymentDetailViewModel = koinViewModel()
) {
    val payment by vm.payment.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val decoded by vm.decoded.collectAsState()

    LaunchedEffect(paymentHash) {
        vm.loadPayment(paymentHash)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Payment Details", fontWeight = FontWeight.Black) },
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
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center), color = PrimaryGreen)
            } else if (payment != null) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(SecondaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bolt, null, modifier = Modifier.size(48.dp), tint = SecondaryBlue)
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    Text("Sent Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimaryDark)
                    Text(
                        text = "${payment!!.value} sats",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = PrimaryGreen
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        DetailRow("Status", payment!!.status, isDark = true)
                        DetailRow("Date", formatDate(payment!!.creationDate), isDark = true)
                        DetailRow("Fee", "${payment!!.fee} sats", isDark = true)
                        HorizontalDivider(Modifier.padding(vertical = 16.dp), color = DarkOutline.copy(alpha = 0.5f))
                        DetailRow("Payment Hash", payment!!.paymentHash.take(16) + "...", isDark = true)
                        DetailRow("Description", decoded?.description ?: "No description", isDark = true)
                    }

                    Spacer(Modifier.height(24.dp))
                    SectionHeader("Receiver Information", modifier = Modifier.align(Alignment.Start), color = TextPrimaryDark)
                    Spacer(Modifier.height(12.dp))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = PrimaryGreen)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Blink Wallet User", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                Text("Lightning Address / Invoice", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                    
                    GradientButton(
                        text = "Share Receipt",
                        icon = Icons.Default.Share,
                        onClick = { /* Share functionality */ }
                    )
                }
            } else {
                Text("Payment not found", Modifier.align(Alignment.Center), color = TextPrimaryDark)
            }
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

private fun formatDate(timestamp: Long): String {
    val date = if (timestamp < 1000000000000L) timestamp * 1000 else timestamp
    return java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(date))
}
