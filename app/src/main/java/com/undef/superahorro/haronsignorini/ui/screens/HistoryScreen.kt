package com.undef.superahorro.haronsignorini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseViewModel

@Composable
fun HistoryScreen(
    navController: NavController,
    purchaseViewModel: PurchaseViewModel = viewModel()
) {
    val purchases by purchaseViewModel.purchases.collectAsState()
    val groupedPurchases = purchases
        .sortedByDescending { it.sortKey() }
        .groupBy { it.monthLabel() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Historial",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Compras agrupadas por mes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            groupedPurchases.forEach { (month, monthPurchases) ->
                item(key = "header_$month") {
                    MonthSeparator(month = month)
                }

                items(
                    items = monthPurchases,
                    key = { it.id }
                ) { purchase ->
                    HistoryPurchaseCard(
                        purchase = purchase,
                        onClick = {
                            navController.navigate(
                                AppRoutes.PurchaseDetail.createRoute(purchase.id)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthSeparator(month: String) {
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = month,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun HistoryPurchaseCard(
    purchase: Purchase,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = purchase.marketName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = purchase.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${'$'}${purchase.total.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun Purchase.sortKey(): Int {
    val parts = date.split("/")
    val day = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val month = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val year = parts.getOrNull(2)?.toIntOrNull() ?: 0
    return year * 10000 + month * 100 + day
}

private fun Purchase.monthLabel(): String {
    val parts = date.split("/")
    val month = parts.getOrNull(1)?.toIntOrNull() ?: return "Sin fecha"
    val year = parts.getOrNull(2) ?: return "Sin fecha"
    val monthName = when (month) {
        1 -> "Enero"
        2 -> "Febrero"
        3 -> "Marzo"
        4 -> "Abril"
        5 -> "Mayo"
        6 -> "Junio"
        7 -> "Julio"
        8 -> "Agosto"
        9 -> "Septiembre"
        10 -> "Octubre"
        11 -> "Noviembre"
        12 -> "Diciembre"
        else -> return "Sin fecha"
    }
    return "$monthName $year"
}
