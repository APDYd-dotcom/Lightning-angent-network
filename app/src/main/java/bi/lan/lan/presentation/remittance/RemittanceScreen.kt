package bi.lan.lan.presentation.remittance

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.core.utils.ReceiptGenerator
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.presentation.components.*
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemittanceScreen(
    onBack: () -> Unit,
    viewModel: RemittanceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Remittance", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundDark)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is RemittanceUiState.Idle, is RemittanceUiState.Loading -> {
                    Text(
                        text = "Enter the amount you wish to request from abroad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        LANTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = "Amount (sats)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        LANTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Description (Optional)",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    GradientButton(
                        text = "Generate Remittance Request",
                        isLoading = state is RemittanceUiState.Loading,
                        onClick = {
                            val amt = amount.toLongOrNull() ?: 0
                            if (amt > 0) {
                                viewModel.createRemittanceRequest(amt, description)
                            }
                        }
                    )
                }

                is RemittanceUiState.Created -> {
                    RemittanceCreatedContent(
                        remittance = state.remittance,
                        onShare = { shareRemittance(context, state.remittance) }
                    )
                }

                is RemittanceUiState.Paid -> {
                    RemittancePaidContent(
                        remittance = state.remittance,
                        onShareReceipt = { shareReceipt(context, state.remittance) },
                        onDone = {
                            viewModel.reset()
                            onBack()
                        }
                    )
                }

                is RemittanceUiState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.reset() }) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

@Composable
fun RemittanceCreatedContent(
    remittance: RemittanceEntity,
    onShare: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Request Created Successfully!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Reference: ${remittance.reference}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(Modifier.height(24.dp))

        QRCard(invoice = remittance.invoice)

        Spacer(Modifier.height(24.dp))

        CopyableTextCard(label = "Lightning Invoice", value = remittance.invoice)

        Spacer(Modifier.height(32.dp))

        GradientButton(
            text = "Share via WhatsApp",
            onClick = onShare
        )

        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = PrimaryGreen)
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Waiting for payment...",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark
            )
        }
    }
}

@Composable
fun RemittancePaidContent(
    remittance: RemittanceEntity,
    onShareReceipt: () -> Unit,
    onDone: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(StatusSuccess.copy(alpha = 0.15f), RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusSuccess,
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            text = "Payment Received!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )

        Text(
            text = "${remittance.amount} sats",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryGreen
        )

        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Reference", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                Text(remittance.reference, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Status", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                StatusChip(status = remittance.status)
            }
        }

        Spacer(Modifier.height(32.dp))

        GradientButton(
            text = "Share Receipt via WhatsApp",
            onClick = onShareReceipt
        )

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onDone) {
            Text("Back to Home", fontWeight = FontWeight.Bold, color = PrimaryGreen)
        }
    }
}

private fun shareRemittance(context: android.content.Context, remittance: RemittanceEntity) {
    val qrUri = bi.lan.lan.core.utils.QrGenerator.generateQrUri(context, remittance.invoice, remittance.reference)
    
    val message = """
        ⚡ LAN - Lightning Agent Network
        
        Please send me ${remittance.amount} sats.
        
        Reference: ${remittance.reference}
        
        Lightning Invoice:
        ${remittance.invoice}
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        if (qrUri != null) {
            putExtra(Intent.EXTRA_STREAM, qrUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            type = "text/plain"
        }
        `setPackage`("com.whatsapp")
    }

    try {
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        val chooser = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            if (qrUri != null) {
                putExtra(Intent.EXTRA_STREAM, qrUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }, "Share Remittance Request")
        context.startActivity(chooser)
    }
}

private fun shareReceipt(context: android.content.Context, remittance: RemittanceEntity) {
    val uri = ReceiptGenerator.generateReceipt(
        context,
        remittance.amount,
        remittance.reference,
        remittance.transactionId ?: "N/A",
        remittance.paidAt ?: System.currentTimeMillis(),
        remittance.status
    )

    val message = """
        ✅ Payment received successfully.
        
        Amount:
        ${remittance.amount} sats
        
        Reference:
        ${remittance.reference}
        
        Transaction ID:
        ${remittance.transactionId}
        
        Status:
        SUCCESS
        
        Powered by LAN ⚡
    """.trimIndent()

    if (uri != null) {
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
        } catch (e: Exception) {
            val chooser = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Share Receipt")
            context.startActivity(chooser)
        }
    }
}
