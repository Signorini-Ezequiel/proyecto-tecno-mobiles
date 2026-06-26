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
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseListViewModel

@Composable
fun HistoryScreen(
    navController: NavController,
    purchaseViewModel: PurchaseListViewModel = hiltViewModel()
) {
    val purchases by purchaseViewModel.purchases.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val groupedPurchases = purchases
        .sortedByDescending { it.sortKey() }
        .groupBy { it.monthLabel(context::getString) }

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
                        text = stringResource(R.string.history),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.history_subtitle),
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

private fun Purchase.monthLabel(string: (Int) -> String): String {
    val parts = date.split("/")
    val month = parts.getOrNull(1)?.toIntOrNull() ?: return string(R.string.no_date)
    val year = parts.getOrNull(2) ?: return string(R.string.no_date)
    val monthName = when (month) {
        1 -> string(R.string.month_january)
        2 -> string(R.string.month_february)
        3 -> string(R.string.month_march)
        4 -> string(R.string.month_april)
        5 -> string(R.string.month_may)
        6 -> string(R.string.month_june)
        7 -> string(R.string.month_july)
        8 -> string(R.string.month_august)
        9 -> string(R.string.month_september)
        10 -> string(R.string.month_october)
        11 -> string(R.string.month_november)
        12 -> string(R.string.month_december)
        else -> return string(R.string.no_date)
    }
    return "$monthName $year"
}
