package bi.lan.lan.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.animation.core.*
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import bi.lan.lan.data.model.BalanceResponse
import bi.lan.lan.data.model.HealthResponse
import bi.lan.lan.data.model.InvoiceResponse
import bi.lan.lan.data.model.PaymentHistoryItem
import bi.lan.lan.ui.theme.*

// ─── Balance Card ─────────────────────────────────────────────────────────────

@Composable
fun BalanceCard(balance: BalanceResponse?, accentColor: Color = PrimaryGreen) {
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
                    .background(if (isHealthy) StatusSuccess else StatusError)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (isHealthy) "Online" else "Offline",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (health != null) {
                    Text("${health.nodeAlias} • Block ${health.blockHeight}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            StatusBadge(if (isHealthy) "Synced" else "Not Synced", isHealthy)
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
    val color = if (isSuccess) StatusSuccess else StatusError
    Box(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Loading Button ───────────────────────────────────────────────────────────

@Composable
fun LoadingButton(text: String, isLoading: Boolean, accentColor: Color = PrimaryGreen, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
fun CopyableTextCard(label: String, value: String, accentColor: Color = PrimaryGreen) {
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
fun InvoiceCard(invoice: InvoiceResponse, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (invoice.settled) StatusSuccess.copy(alpha = 0.1f) else StatusWarning.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (invoice.settled) Icons.Default.CheckCircle else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (invoice.settled) StatusSuccess else StatusWarning,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${invoice.amount} sats", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(invoice.memo.ifEmpty { "No memo" }, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            StatusBadge(if (invoice.settled) "Settled" else "Pending", invoice.settled)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingInvoiceBottomSheet(
    invoice: InvoiceResponse,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceWhite,
        dragHandle = { BottomSheetDefaults.DragHandle(color = DividerColor) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(StatusWarning.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(32.dp), tint = StatusWarning)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text("Pending Invoice", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "This invoice is waiting for payment",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Spacer(Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("Amount", "${invoice.amount} sats")
                    InfoRow("Memo", invoice.memo.ifEmpty { "-" })
                    InfoRow("Created", invoice.creationDate.toString())
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            CopyableTextCard("Payment Request", invoice.paymentRequest, PrimaryGreen)
            
            Spacer(Modifier.height(32.dp))
            
            LoadingButton(
                text = "Check Payment Status",
                isLoading = isLoading,
                accentColor = PrimaryGreen,
                onClick = onConfirm
            )
            
            Spacer(Modifier.height(12.dp))
            
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Dismiss", color = TextSecondary, fontWeight = FontWeight.Medium)
            }
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

// ─── Custom Text Field ────────────────────────────────────────────────────────

@Composable
fun LANTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = TextHint) } } else null,
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = if (isError) StatusError else PrimaryGreen) } },
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = OutlineColor.copy(alpha = 0.5f),
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedLabelColor = PrimaryGreen,
                unfocusedLabelColor = TextSecondary,
                cursorColor = PrimaryGreen,
                errorBorderColor = StatusError,
                errorLabelColor = StatusError
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = StatusError,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// ─── Custom Bottom Bar ────────────────────────────────────────────────────────

@Composable
fun LANBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Surface(
        color = SurfaceWhite,
        shadowElevation = 20.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LANBottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedIndex == 0,
                onClick = { onItemSelected(0) }
            )
            LANBottomNavItem(
                icon = Icons.Default.History,
                label = "Activity",
                isSelected = selectedIndex == 1,
                onClick = { onItemSelected(1) }
            )
            LANBottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = selectedIndex == 2,
                onClick = { onItemSelected(2) }
            )
        }
    }
}

/**
 * A "Pro" Bottom App Bar with 3 items: Home, Activity, Profile.
 */
@Composable
fun LANBottomAppBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onFabClick: () -> Unit = {} // Kept for compatibility but not used in 3-item layout
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = SurfaceWhite,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LANBottomNavItemSmall(Icons.Default.Home, "Home", selectedIndex == 0) { onItemSelected(0) }
            LANBottomNavItemSmall(Icons.Default.History, "Activity", selectedIndex == 1) { onItemSelected(1) }
            LANBottomNavItemSmall(Icons.Default.Person, "Profile", selectedIndex == 2) { onItemSelected(2) }
        }
    }
}

@Composable
private fun LANBottomNavItemSmall(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) PrimaryGreen else TextSecondary
    val bgColor = if (isSelected) PrimaryGreen.copy(alpha = 0.1f) else Color.Transparent
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon, 
                contentDescription = label, 
                tint = color, 
                modifier = Modifier.size(24.dp)
            )
            if (isSelected) {
                Spacer(Modifier.width(8.dp))
                Text(
                    label, 
                    style = MaterialTheme.typography.labelMedium, 
                    color = color, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LANBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) PrimaryGreen else TextSecondary
    val bgColor = if (isSelected) PrimaryGreenLight.copy(alpha = 0.4f) else Color.Transparent
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// ─── Lightning Logo ───────────────────────────────────────────────────────────

@Composable
fun LightningLogo(size: Int = 56, backgroundColor: Color = PrimaryGreen) {
    Box(
        modifier = Modifier.size(size.dp).clip(RoundedCornerShape((size * 0.25).dp)).background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text("⚡", fontSize = (size * 0.45).sp)
    }
}

// ─── Skeleton Loading (Shimmer Screens) ───────────────────────────────────────

fun Modifier.shimmerLoadingAnimation(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.5f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    background(brush = brush)
}

@Composable
fun ShimmerItem(modifier: Modifier = Modifier, shape: RoundedCornerShape = RoundedCornerShape(8.dp)) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmerLoadingAnimation()
    )
}

@Composable
fun BalanceCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            ShimmerItem(Modifier.width(100.dp).height(14.dp))
            Spacer(Modifier.height(8.dp))
            ShimmerItem(Modifier.width(180.dp).height(32.dp))
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    ShimmerItem(Modifier.width(60.dp).height(12.dp))
                    Spacer(Modifier.height(4.dp))
                    ShimmerItem(Modifier.width(80.dp).height(16.dp))
                }
                Column(horizontalAlignment = Alignment.End) {
                    ShimmerItem(Modifier.width(60.dp).height(12.dp))
                    Spacer(Modifier.height(4.dp))
                    ShimmerItem(Modifier.width(80.dp).height(16.dp))
                }
            }
        }
    }
}

@Composable
fun InvoiceCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                ShimmerItem(Modifier.width(120.dp).height(18.dp))
                Spacer(Modifier.height(6.dp))
                ShimmerItem(Modifier.width(180.dp).height(14.dp))
            }
            ShimmerItem(Modifier.width(70.dp).height(24.dp), RoundedCornerShape(12.dp))
        }
    }
}

@Composable
fun PaymentCardSkeleton() = InvoiceCardSkeleton()

@Composable
fun HealthStatusCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerItem(Modifier.size(12.dp), RoundedCornerShape(6.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                ShimmerItem(Modifier.width(100.dp).height(16.dp))
                Spacer(Modifier.height(6.dp))
                ShimmerItem(Modifier.width(160.dp).height(12.dp))
            }
            ShimmerItem(Modifier.width(60.dp).height(24.dp), RoundedCornerShape(12.dp))
        }
    }
}

@Composable
fun NodeInfoSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(6) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ShimmerItem(Modifier.width(100.dp).height(16.dp))
                    ShimmerItem(Modifier.width(140.dp).height(16.dp))
                }
            }
        }
    }
}

