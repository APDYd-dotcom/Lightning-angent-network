package bi.lan.lan.presentation.remittance

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.core.utils.QrGenerator
import bi.lan.lan.core.utils.ReceiptGenerator
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.presentation.components.CopyableTextCard
import bi.lan.lan.presentation.components.LANTextField
import bi.lan.lan.presentation.components.LoadingButton
import bi.lan.lan.ui.theme.PrimaryGreen
import bi.lan.lan.ui.theme.StatusSuccess
import bi.lan.lan.ui.theme.TextPrimary
import bi.lan.lan.ui.theme.TextSecondary
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
                title = { Text("Request Remittance", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is RemittanceUiState.Idle, is RemittanceUiState.Loading -> {
                    Text(
                        "Enter the amount you wish to request from abroad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

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

                    Spacer(Modifier.height(32.dp))

                    LoadingButton(
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
    val qrBitmap = remember(remittance.invoice) {
        QrGenerator.generateQrCode(remittance.invoice)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Request Created Successfully!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )
        Text(
            "Reference: ${remittance.reference}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(24.dp))

        CopyableTextCard(label = "Lightning Invoice", value = remittance.invoice)

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onShare,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Share via WhatsApp")
        }

        Spacer(Modifier.height(16.dp))

        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Waiting for payment...",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
fun RemittancePaidContent(
    remittance: RemittanceEntity,
    onShareReceipt: () -> Unit,
    onDone: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = StatusSuccess,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            "Payment Received!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            "${remittance.amount} sats",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryGreen
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Reference", style = MaterialTheme.typography.labelSmall)
                    Text(remittance.reference, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Status", style = MaterialTheme.typography.labelSmall)
                    Text("SUCCESS", color = StatusSuccess, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onShareReceipt,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Share Receipt via WhatsApp")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onDone) {
            Text("Back to Home", fontWeight = FontWeight.Bold)
        }
    }
}

private fun shareRemittance(context: android.content.Context, remittance: RemittanceEntity) {
    val message = """
        ⚡ LAN - Lightning Agent Network
        
        Please send me ${remittance.amount} sats.
        
        Reference:
        ${remittance.reference}
        
        Description:
        ${remittance.description}
        
        Lightning Invoice:
        ${remittance.invoice}
        
        Thank you.
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
        `setPackage`("com.whatsapp")
    }

    try {
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        // Fallback to chooser
        val chooser = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
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
        remittance.paidAt ?: System.currentTimeMillis()
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
