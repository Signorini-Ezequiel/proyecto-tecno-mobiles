package com.undef.superahorro.haronsignorini.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StatsScreen(purchaseViewModel: PurchaseListViewModel = hiltViewModel()) {
    val purchases by purchaseViewModel.purchases.collectAsStateWithLifecycle()
    val context = LocalContext.current
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
    val selectedMonthLabel = selectedMonth?.label ?: context.getString(R.string.no_data)

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
                StatisticsHeader()
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
                StatisticsCards(
                    totalSpent = totalSpent,
                    purchasesCount = selectedMonthPurchases.size,
                    currentMonthLabel = selectedMonthLabel
                )
            }
            item {
                MarketSpendingTitle()
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
                ChartsSection(
                    spendingByMarket = spendingByMarket,
                    totalSpent = totalSpent
                )
            }
            item {
                ExportDataButton(
                    enabled = purchases.isNotEmpty(),
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.export_data))
                            putExtra(Intent.EXTRA_TEXT, purchases.toCsv())
                        }
                        context.startActivity(
                            Intent.createChooser(
                                sendIntent,
                                context.getString(R.string.export_data)
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun StatisticsHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.stats),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.stats_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatisticsCards(
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
                text = stringResource(R.string.total_spent),
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
                text = stringResource(R.string.registered_purchases_in_month, purchasesCount, currentMonthLabel),
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
                    contentDescription = stringResource(R.string.previous_month)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.selected_month),
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
                    contentDescription = stringResource(R.string.next_month)
                )
            }
        }
    }
}

@Composable
private fun MarketSpendingTitle() {
    Text(
        text = stringResource(R.string.market_spending),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
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
private fun ChartsSection(
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
                text = stringResource(R.string.pie_chart),
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
                        text = stringResource(R.string.no_chart_data),
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
                                        text = stringResource(
                                            R.string.chart_legend_value,
                                            formatMoney(total),
                                            percentage
                                        ),
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

@Composable
private fun ExportDataButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            imageVector = Icons.Filled.IosShare,
            contentDescription = stringResource(R.string.export_data)
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.export_data)
        )
    }
}

private fun List<com.undef.superahorro.haronsignorini.data.Purchase>.toCsv(): String {
    val header = "id,fecha,hora,supermercado,total,producto_codigo,producto_nombre,producto_descripcion,cantidad,precio"
    val rows = flatMap { purchase ->
        if (purchase.products.isEmpty()) {
            listOf(
                listOf(
                    purchase.id.toString(),
                    purchase.date,
                    purchase.time,
                    purchase.marketName,
                    purchase.total.toString(),
                    "",
                    "",
                    "",
                    "",
                    ""
                ).toCsvLine()
            )
        } else {
            purchase.products.map { product ->
                listOf(
                    purchase.id.toString(),
                    purchase.date,
                    purchase.time,
                    purchase.marketName,
                    purchase.total.toString(),
                    product.code,
                    product.name,
                    product.description,
                    product.quantity.toString(),
                    product.price.toString()
                ).toCsvLine()
            }
        }
    }
    return (listOf(header) + rows).joinToString("\n")
}

private fun List<String>.toCsvLine(): String {
    return joinToString(",") { value ->
        "\"${value.replace("\"", "\"\"")}\""
    }
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
    val displayLocale = Locale.forLanguageTag("es-AR")
    val monthFormatter = SimpleDateFormat("MMMM yyyy", displayLocale)
    val label = monthFormatter.format(parsedDate)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(displayLocale) else it.toString() }
    return MonthBucket(
        year = purchaseCalendar.get(Calendar.YEAR),
        month = purchaseCalendar.get(Calendar.MONTH) + 1,
        label = label
    )
}
