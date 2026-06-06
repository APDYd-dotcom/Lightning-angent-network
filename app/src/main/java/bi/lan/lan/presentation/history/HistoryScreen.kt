package bi.lan.lan.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.data.local.RemittanceEntity
import bi.lan.lan.presentation.components.GlassCard
import bi.lan.lan.presentation.components.StatusChip
import bi.lan.lan.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onItemClick: (RemittanceEntity) -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val remittances by viewModel.remittances.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Remittance History", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
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
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search by reference or description", color = TextSecondaryDark) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = SurfaceDark,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark,
                    focusedPlaceholderColor = TextSecondaryDark,
                    unfocusedPlaceholderColor = TextSecondaryDark
                ),
                singleLine = true
            )

            // Horizontal Filter Chips for Status
            Text(
                text = "Filter Status",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondaryDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                val statuses = listOf("ALL" to "All", "PAID" to "Paid", "PENDING" to "Pending", "FAILED" to "Failed")
                items(statuses) { (key, label) ->
                    FilterChip(
                        selected = statusFilter == key,
                        onClick = { viewModel.onStatusFilterChange(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryGreen,
                            containerColor = SurfaceDark,
                            labelColor = TextSecondaryDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = statusFilter == key,
                            borderColor = Color.Transparent,
                            selectedBorderColor = PrimaryGreen
                        )
                    )
                }
            }

            // Horizontal Filter Chips for Date range
            Text(
                text = "Time Period",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondaryDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                val dates = listOf("ALL" to "All Time", "TODAY" to "Today", "WEEK" to "This Week", "MONTH" to "This Month")
                items(dates) { (key, label) ->
                    FilterChip(
                        selected = dateFilter == key,
                        onClick = { viewModel.onDateFilterChange(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SecondaryBlue.copy(alpha = 0.2f),
                            selectedLabelColor = SecondaryBlue,
                            containerColor = SurfaceDark,
                            labelColor = TextSecondaryDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = dateFilter == key,
                            borderColor = Color.Transparent,
                            selectedBorderColor = SecondaryBlue
                        )
                    )
                }
            }

            // Sort Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${remittances.size} remittances found",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )

                Box {
                    TextButton(
                        onClick = { showSortMenu = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = PrimaryGreen)
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        val sortLabel = when (sortBy) {
                            "DATE_DESC" -> "Newest First"
                            "DATE_ASC" -> "Oldest First"
                            "AMOUNT_DESC" -> "Highest Amount"
                            "AMOUNT_ASC" -> "Lowest Amount"
                            else -> "Sort By"
                        }
                        Text(sortLabel, style = MaterialTheme.typography.labelLarge)
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        val sortOptions = listOf(
                            "DATE_DESC" to "Newest First",
                            "DATE_ASC" to "Oldest First",
                            "AMOUNT_DESC" to "Highest Amount",
                            "AMOUNT_ASC" to "Lowest Amount"
                        )
                        sortOptions.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label, color = TextPrimaryDark) },
                                onClick = {
                                    viewModel.onSortChange(key)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Remittance Items List
            if (remittances.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No remittances match", color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("Try adjusting your search or filters", color = TextSecondaryDark.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(remittances) { remittance ->
                        RemittanceHistoryItemCard(
                            remittance = remittance,
                            onClick = { onItemClick(remittance) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemittanceHistoryItemCard(
    remittance: RemittanceEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(remittance.createdAt))

    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = remittance.reference,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "⚡",
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
                if (remittance.description.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = remittance.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${remittance.amount} sats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (remittance.status.uppercase() == "PAID") StatusSuccess else TextPrimaryDark
                )
                Spacer(Modifier.height(6.dp))
                StatusChip(status = remittance.status)
            }
        }
    }
}
