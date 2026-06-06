package bi.lan.lan.presentation.remittance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.presentation.components.AnimatedStatCard
import bi.lan.lan.presentation.components.GlassCard
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.CircleShape
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = koinViewModel()
) {
    val totalCount by viewModel.totalCount.collectAsState(initial = 0)
    val totalVolume by viewModel.totalVolume.collectAsState(initial = 0L)
    val averageAmount by viewModel.averageAmount.collectAsState(initial = 0.0)
    val largestAmount by viewModel.largestAmount.collectAsState(initial = 0L)
    val monthlyVolume by viewModel.monthlyVolume.collectAsState(initial = emptyList())
    val statusDist by viewModel.statusDistribution.collectAsState(initial = emptyMap())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Analytics", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // General Stats
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedStatCard(
                    title = "Total Transactions",
                    value = "$totalCount",
                    subtitle = "All statuses",
                    modifier = Modifier.weight(1f)
                )
                AnimatedStatCard(
                    title = "Total Volume",
                    value = "$totalVolume sats",
                    subtitle = "Successful only",
                    indicatorColor = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedStatCard(
                    title = "Average Remit",
                    value = "${String.format(java.util.Locale.US, "%.1f", averageAmount)} sats",
                    subtitle = "Per paid request",
                    indicatorColor = SecondaryBlue,
                    modifier = Modifier.weight(1f)
                )
                AnimatedStatCard(
                    title = "Largest Remit",
                    value = "$largestAmount sats",
                    subtitle = "Record high",
                    indicatorColor = StatusSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Monthly Volume Chart Section
            Text(
                text = "Volume by Month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (monthlyVolume.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No transaction data available yet", color = TextSecondaryDark)
                    }
                }
            } else {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        MonthlyVolumeChart(monthlyVolume = monthlyVolume)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Status Distribution Chart Section
            Text(
                text = "Status Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                val paid = statusDist["PAID"] ?: 0
                val pending = statusDist["PENDING"] ?: 0
                val failed = statusDist["FAILED"] ?: 0
                val total = paid + pending + failed

                if (total == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No distribution data available", color = TextSecondaryDark)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DistributionBar(
                            label = "Paid (${String.format(java.util.Locale.US, "%.1f", (paid.toDouble() / total) * 100)}%)",
                            count = paid,
                            total = total,
                            color = StatusSuccess
                        )
                        DistributionBar(
                            label = "Pending (${String.format(java.util.Locale.US, "%.1f", (pending.toDouble() / total) * 100)}%)",
                            count = pending,
                            total = total,
                            color = StatusWarning
                        )
                        DistributionBar(
                            label = "Failed (${String.format(java.util.Locale.US, "%.1f", (failed.toDouble() / total) * 100)}%)",
                            count = failed,
                            total = total,
                            color = StatusError
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyVolumeChart(
    monthlyVolume: List<Pair<String, Long>>,
    modifier: Modifier = Modifier
) {
    val maxVolume = monthlyVolume.maxOf { it.second }.coerceAtLeast(1L)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            monthlyVolume.forEach { (_, volume) ->
                val ratio = volume.toFloat() / maxVolume
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .fillMaxHeight(ratio)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(PrimaryGreen, SecondaryBlue)
                            )
                        )
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            monthlyVolume.forEach { (month, _) ->
                Text(
                    text = month.split(" ").firstOrNull() ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(36.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DistributionBar(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val fraction = if (total > 0) count.toFloat() / total else 0f
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            Text("$count", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
