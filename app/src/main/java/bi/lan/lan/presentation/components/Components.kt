package bi.lan.lan.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.data.model.BalanceResponse
import bi.lan.lan.data.model.HealthResponse
import bi.lan.lan.data.model.InvoiceResponse
import bi.lan.lan.data.model.PaymentHistoryItem
import bi.lan.lan.ui.theme.*

// ─── Balance Card ─────────────────────────────────────────────────────────────

@Composable
fun BalanceCard(balance: BalanceResponse?, accentColor: Color = BrandGreen) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(accentColor, accentColor.copy(alpha = 0.8f))))
                .padding(24.dp)
        ) {
            Column {
                Text("Wallet Balance", style = MaterialTheme.typography.bodySmall, color = SurfaceWhite.copy(alpha = 0.75f))
                Spacer(Modifier.height(4.dp))
                Text(
                    "${balance?.walletBalance?.totalBalance ?: 0} sats",
                    style = MaterialTheme.typography.headlineLarge,
                    color = SurfaceWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = SurfaceWhite.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Channel", style = MaterialTheme.typography.labelSmall, color = SurfaceWhite.copy(alpha = 0.7f))
                        Text("${balance?.channelBalance?.balance ?: 0} sats", style = MaterialTheme.typography.titleSmall, color = SurfaceWhite, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Confirmed", style = MaterialTheme.typography.labelSmall, color = SurfaceWhite.copy(alpha = 0.7f))
                        Text("${balance?.walletBalance?.confirmedBalance ?: 0} sats", style = MaterialTheme.typography.titleSmall, color = SurfaceWhite, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ─── Health Status Card ───────────────────────────────────────────────────────

@Composable
fun HealthStatusCard(health: HealthResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val isHealthy = health?.status == "healthy"
            Box(
                modifier = Modifier.size(12.dp).clip(CircleShape)
                    .background(if (isHealthy) StatusCompleted else StatusFailed)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (isHealthy) "Node Online" else "Node Offline",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (health != null) {
                    Text("${health.nodeAlias} • Block ${health.blockHeight}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            StatusBadge(if (isHealthy) "Synced" else "Error", isHealthy)
        }
    }
}

// ─── Quick Action Button ──────────────────────────────────────────────────────

@Composable
fun QuickActionButton(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(16.dp)).clickable { onClick() }.padding(8.dp)
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

// ─── Status Badge ─────────────────────────────────────────────────────────────

@Composable
fun StatusBadge(text: String, isSuccess: Boolean) {
    val color = if (isSuccess) StatusCompleted else StatusFailed
    Box(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Loading Button ───────────────────────────────────────────────────────────

@Composable
fun LoadingButton(text: String, isLoading: Boolean, accentColor: Color = BrandGreen, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = accentColor, disabledContainerColor = accentColor.copy(alpha = 0.5f)),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        if (isLoading) CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
        else Text(text, color = SurfaceWhite, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Copyable Text Card ──────────────────────────────────────────────────────

@Composable
fun CopyableTextCard(label: String, value: String, accentColor: Color = BrandGreen) {
    val clipboard = LocalClipboardManager.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable { clipboard.setText(AnnotatedString(value)) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text("Tap to copy", style = MaterialTheme.typography.labelSmall, color = accentColor)
        }
    }
}

// ─── QR Code Placeholder ──────────────────────────────────────────────────────

@Composable
fun QRCodePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(200.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceCard),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📱", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text("QR Code", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

// ─── Invoice Card ─────────────────────────────────────────────────────────────

@Composable
fun InvoiceCard(invoice: InvoiceResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${invoice.amount} sats", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(invoice.memo.ifEmpty { "No memo" }, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            StatusBadge(if (invoice.settled) "Settled" else "Pending", invoice.settled)
        }
    }
}

// ─── Payment Card ─────────────────────────────────────────────────────────────

@Composable
fun PaymentCard(payment: PaymentHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${payment.value} sats", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Fee: ${payment.fee} sats", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            StatusBadge(payment.status, payment.status == "SUCCEEDED")
        }
    }
}

// ─── Info Row ─────────────────────────────────────────────────────────────────

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
}

// ─── Lightning Logo ───────────────────────────────────────────────────────────

@Composable
fun LightningLogo(size: Int = 56) {
    Box(
        modifier = Modifier.size(size.dp).clip(RoundedCornerShape((size * 0.25).dp)).background(BrandGreen),
        contentAlignment = Alignment.Center
    ) {
        Text("⚡", fontSize = (size * 0.45).sp)
    }
}
