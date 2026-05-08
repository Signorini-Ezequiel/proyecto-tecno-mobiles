package com.example.superahorro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superahorro.viewmodel.PurchaseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StatsScreen(purchaseViewModel: PurchaseViewModel = viewModel()) {
    val purchases by purchaseViewModel.purchases.collectAsState()
    val availableMonths = purchases
        .mapNotNull { it.date.toMonthBucketOrNull() }
        .distinct()
        .sortedByDescending { it.sortKey }
    val defaultMonthIndex = availableMonths.indexOfFirst { it.isCurrentMonth() }.takeIf { it >= 0 } ?: 0
    val selectedMonthIndexState = remember(availableMonths.map { it.sortKey }) {
        mutableIntStateOf(defaultMonthIndex.coerceIn(0, (availableMonths.lastIndex).coerceAtLeast(0)))
    }
    val selectedMonth = availableMonths.getOrNull(selectedMonthIndexState.intValue)
    val selectedMonthPurchases = purchases.filter { it.date.toMonthBucketOrNull() == selectedMonth }
    val totalSpent = selectedMonthPurchases.sumOf { it.total }
    val spendingByMarket = selectedMonthPurchases
        .groupBy { it.marketName }
        .mapValues { entry -> entry.value.sumOf { it.total } }
        .toList()
        .sortedByDescending { it.second }
    val selectedMonthLabel = selectedMonth?.label ?: "Sin datos"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Estadisticas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Analisis visual por mes con datos simulados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                MonthSelectorCard(
                    selectedMonthLabel = selectedMonthLabel,
                    canGoPrevious = selectedMonthIndexState.intValue < availableMonths.lastIndex,
                    canGoNext = selectedMonthIndexState.intValue > 0,
                    onPrevious = {
                        if (selectedMonthIndexState.intValue < availableMonths.lastIndex) {
                            selectedMonthIndexState.intValue += 1
                        }
                    },
                    onNext = {
                        if (selectedMonthIndexState.intValue > 0) {
                            selectedMonthIndexState.intValue -= 1
                        }
                    }
                )
            }

            item {
                TotalSpentCard(
                    totalSpent = totalSpent,
                    purchasesCount = selectedMonthPurchases.size,
                    currentMonthLabel = selectedMonthLabel
                )
            }

            item {
                Text(
                    text = "Gasto por supermercado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(
                items = spendingByMarket,
                key = { it.first }
            ) { (market, total) ->
                MarketSpendingRow(
                    market = market,
                    total = total
                )
            }

            item {
                PieChartCard(
                    spendingByMarket = spendingByMarket,
                    totalSpent = totalSpent
                )
            }
        }
    }
}

@Composable
private fun TotalSpentCard(
    totalSpent: Double,
    purchasesCount: Int,
    currentMonthLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Gasto total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
            )
            Text(
                text = formatMoney(totalSpent),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "$purchasesCount compras registradas en $currentMonthLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
private fun MonthSelectorCard(
    selectedMonthLabel: String,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevious,
                enabled = canGoPrevious
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Mes anterior"
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Mes seleccionado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedMonthLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = onNext,
                enabled = canGoNext
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Mes siguiente"
                )
            }
        }
    }
}

@Composable
private fun MarketSpendingRow(
    market: String,
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = market,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatMoney(total),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PieChartCard(
    spendingByMarket: List<Pair<String, Double>>,
    totalSpent: Double
) {
    val chartColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        Color(0xFF5B8C5A),
        Color(0xFFE07A5F),
        Color(0xFF3D405B),
        Color(0xFFF2CC8F)
    )
    val innerCircleColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Grafico de torta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (spendingByMarket.isEmpty() || totalSpent <= 0.0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Todavia no hay datos para visualizar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        var startAngle = -90f
                        spendingByMarket.forEachIndexed { index, (_, total) ->
                            val sweepAngle = ((total / totalSpent) * 360f).toFloat()
                            drawArc(
                                color = chartColors[index % chartColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size = Size(size.width, size.height)
                            )
                            startAngle += sweepAngle
                        }

                        drawCircle(
                            color = innerCircleColor,
                            radius = size.minDimension * 0.28f
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1.1f)
                            .height(220.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(
                            items = spendingByMarket,
                            key = { _, item -> item.first }
                        ) { index, (market, total) ->
                            val percentage = ((total / totalSpent) * 100).toInt()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(min = 12.dp, max = 12.dp)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(chartColors[index % chartColors.size])
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = market,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${formatMoney(total)} · $percentage%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (index != spendingByMarket.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatMoney(amount: Double): String {
    return "${'$'}${amount.toInt()}"
}

private data class MonthBucket(
    val year: Int,
    val month: Int,
    val label: String
) {
    val sortKey: Int = year * 100 + month
}

private fun MonthBucket.isCurrentMonth(): Boolean {
    val currentCalendar = Calendar.getInstance()
    return year == currentCalendar.get(Calendar.YEAR) &&
        month == currentCalendar.get(Calendar.MONTH) + 1
}

private fun String.toMonthBucketOrNull(): MonthBucket? {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val parsedDate = formatter.parse(this) ?: return null
    val purchaseCalendar = Calendar.getInstance().apply { time = parsedDate }
    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale("es", "AR"))
    val label = monthFormatter.format(parsedDate)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "AR")) else it.toString() }
    return MonthBucket(
        year = purchaseCalendar.get(Calendar.YEAR),
        month = purchaseCalendar.get(Calendar.MONTH) + 1,
        label = label
    )
}
