package bi.lan.lan.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.lan.lan.data.model.Agent
import bi.lan.lan.data.model.Transaction
import bi.lan.lan.ui.theme.*

@Composable
fun BalanceCard(balance: Long, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text("Total Balance", color = OffWhite.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$balance sats",
                    color = PureWhite,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ElectricGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = ElectricGreen, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    val isDeposit = tx.type == "DEPOSIT"
    val icon = if (isDeposit) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward
    val color = if (isDeposit) GreenSuccess else OrangeWarning

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.description ?: tx.type, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(tx.status, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            text = "${if (isDeposit) "+" else "-"}${tx.amountSats} sats",
            fontWeight = FontWeight.Bold,
            color = if (isDeposit) GreenSuccess else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun AgentCard(agent: Agent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(agent.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("${agent.location} • ${agent.distanceKm} km", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            StatusBadge(text = if (agent.isOnline) "Online" else "Offline", isSuccess = agent.isOnline)
        }
    }
}

@Composable
fun LoadingButton(text: String, isLoading: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = DarkCharcoal, modifier = Modifier.size(24.dp))
        } else {
            Text(text, color = DarkCharcoal, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun StatusBadge(text: String, isSuccess: Boolean) {
    val color = if (isSuccess) GreenSuccess else Color.Gray
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}
