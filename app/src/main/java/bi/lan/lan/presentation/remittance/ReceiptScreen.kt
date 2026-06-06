package bi.lan.lan.presentation.remittance

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.presentation.components.*
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    reference: String,
    onBack: () -> Unit,
    viewModel: ReceiptViewModel = koinViewModel()
) {
    val remittance by viewModel.remittance.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(reference) {
        viewModel.loadRemittance(reference)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Remittance Details", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimaryDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = PrimaryGreen)
            } else {
                remittance?.let { rem ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ReceiptCard(
                            amount = rem.amount,
                            reference = rem.reference,
                            transactionId = rem.transactionId ?: "N/A",
                            date = rem.paidAt ?: rem.createdAt,
                            status = rem.status,
                            onExportPng = {
                                viewModel.generateReceiptUri(context) { uri ->
                                    if (uri != null) {
                                        shareReceiptPng(context, rem, uri)
                                    }
                                }
                            }
                        )

                        Spacer(Modifier.height(24.dp))

                        SectionHeader("Additional Details", color = TextPrimaryDark, modifier = Modifier.align(Alignment.Start))
                        Spacer(Modifier.height(12.dp))

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            DetailRow("Description", rem.description.ifEmpty { "No description" })
                            DetailRow("Wallet ID", rem.walletId ?: "Unknown")
                            DetailRow("Created", formatDate(rem.createdAt))
                            if (rem.paidAt != null) {
                                DetailRow("Paid At", formatDate(rem.paidAt))
                            }
                            DetailRow("Status", rem.status)
                        }

                        Spacer(Modifier.height(24.dp))

                        SectionHeader("Lightning Invoice", color = TextPrimaryDark, modifier = Modifier.align(Alignment.Start))
                        Spacer(Modifier.height(12.dp))
                        
                        QRCard(invoice = rem.invoice)
                        Spacer(Modifier.height(12.dp))
                        CopyableTextCard(label = "BOLT11 Invoice", value = rem.invoice)

                        Spacer(Modifier.height(32.dp))

                        GradientButton(
                            text = "Share Invoice Text",
                            icon = Icons.Default.Share,
                            onClick = { shareInvoiceText(context, rem) }
                        )
                        
                        Spacer(Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen)
                        ) {
                            Text("Back to History", color = PrimaryGreen)
                        }
                        
                        Spacer(Modifier.height(40.dp))
                    }
                } ?: run {
                    Text("Remittance not found", color = TextPrimaryDark)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}

private fun shareInvoiceText(context: Context, remittance: RemittanceEntity) {
    val message = """
        ⚡ Lightning Remittance Request
        
        Reference: ${remittance.reference}
        Amount: ${remittance.amount} sats
        Description: ${remittance.description}
        
        Invoice:
        ${remittance.invoice}
        
        Powered by LAN
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Invoice"))
}

private fun shareReceiptPng(context: Context, remittance: RemittanceEntity, uri: Uri) {
    val message = """
        ✅ Remittance Status Update
        
        Amount:
        ${remittance.amount} sats
        
        Reference:
        ${remittance.reference}
        
        Status:
        ${remittance.status.uppercase()}
        
        Transaction ID:
        ${remittance.transactionId ?: "N/A"}
        
        Powered by LAN ⚡
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        `setPackage`("com.whatsapp")
    }

    try {
        context.startActivity(sendIntent)
    } catch (_: Exception) {
        val chooser = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, "Share Remittance Receipt")
        context.startActivity(chooser)
    }
}
