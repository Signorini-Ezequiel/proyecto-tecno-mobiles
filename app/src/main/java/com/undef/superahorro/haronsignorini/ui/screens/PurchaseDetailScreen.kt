package com.undef.superahorro.haronsignorini.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.ui.components.AppDropdownMenu
import com.undef.superahorro.haronsignorini.ui.components.AppDropdownMenuItem
import com.undef.superahorro.haronsignorini.ui.components.ConfirmationDialog
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseListViewModel
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseSyncUiState
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseSyncViewModel

@Composable
fun PurchaseDetailScreen(
    navController: NavController,
    purchase: Purchase?,
    purchaseListViewModel: PurchaseListViewModel,
    purchaseSyncViewModel: PurchaseSyncViewModel = hiltViewModel()
) {
    val syncState by purchaseSyncViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var isActionsMenuExpanded by remember { mutableStateOf(false) }
    var showDeletePurchaseDialog by remember { mutableStateOf(false) }
    val syncSuccessMessage = stringResource(R.string.purchase_sync_success)
    val syncErrorMessage = stringResource(R.string.purchase_sync_error)
    val sharePurchaseTitle = stringResource(R.string.share_purchase)

    LaunchedEffect(syncState) {
        when (syncState) {
            PurchaseSyncUiState.Success -> {
                snackbarHostState.showSnackbar(syncSuccessMessage)
                purchaseSyncViewModel.clearResult()
            }
            PurchaseSyncUiState.Error -> {
                snackbarHostState.showSnackbar(syncErrorMessage)
                purchaseSyncViewModel.clearResult()
            }
            PurchaseSyncUiState.Idle,
            PurchaseSyncUiState.Loading -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.purchase_detail_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (purchase != null) {
                        Box {
                            IconButton(onClick = { isActionsMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = stringResource(R.string.purchase_actions)
                                )
                            }
                            AppDropdownMenu(
                                expanded = isActionsMenuExpanded,
                                onDismissRequest = { isActionsMenuExpanded = false },
                                items = listOf(
                                    AppDropdownMenuItem(
                                        text = if (syncState == PurchaseSyncUiState.Loading) {
                                            stringResource(R.string.syncing_purchase)
                                        } else {
                                            stringResource(R.string.sync_purchase)
                                        },
                                        icon = Icons.Filled.Sync,
                                        enabled = syncState != PurchaseSyncUiState.Loading,
                                        onClick = { purchaseSyncViewModel.syncPurchase(purchase) }
                                    ),
                                    AppDropdownMenuItem(
                                        text = stringResource(R.string.share_purchase),
                                        icon = Icons.Filled.Share,
                                        onClick = {
                                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, purchase.toShareText())
                                            }
                                            context.startActivity(
                                                Intent.createChooser(sendIntent, sharePurchaseTitle)
                                            )
                                        }
                                    ),
                                    AppDropdownMenuItem(
                                        text = stringResource(R.string.edit_purchase),
                                        icon = Icons.Filled.Edit,
                                        onClick = {
                                            navController.navigate(AppRoutes.EditPurchase.createRoute(purchase.id))
                                        }
                                    ),
                                    AppDropdownMenuItem(
                                        text = stringResource(R.string.delete_purchase),
                                        icon = Icons.Filled.Delete,
                                        onClick = { showDeletePurchaseDialog = true }
                                    )
                                )
                            )
                        }
                    }
                }
            }

            if (purchase == null) {
                item {
                    Text(
                        text = stringResource(R.string.purchase_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                item {
                    PurchaseSummaryCard(purchase = purchase)
                }

                item {
                    PurchaseTicketCard(ticketUri = purchase.ticketUri)
                }

                item {
                    Text(
                        text = stringResource(R.string.products),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(
                    items = purchase.products,
                    key = { it.id }
                ) { product ->
                    ProductItem(product = product)
                }
            }

            item {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.back))
                }
            }
        }
    }

    if (purchase != null && showDeletePurchaseDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.delete_purchase),
            message = stringResource(R.string.delete_purchase_confirmation, purchase.marketName),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                showDeletePurchaseDialog = false
                purchaseListViewModel.deletePurchase(purchase.id)
                navController.popBackStack()
            },
            onDismiss = { showDeletePurchaseDialog = false }
        )
    }
}

@Composable
private fun PurchaseTicketCard(ticketUri: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.purchase_ticket),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                if (ticketUri == null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.ticket_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    AsyncImage(
                        model = ticketUri,
                        contentDescription = stringResource(R.string.ticket_preview_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun PurchaseSummaryCard(purchase: Purchase) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = purchase.marketName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            DetailRow(label = stringResource(R.string.date), value = purchase.date)
            DetailRow(label = stringResource(R.string.total), value = formatMoney(purchase.total))
            DetailRow(label = stringResource(R.string.products), value = purchase.productsCount.toString())
        }
    }
}

@Composable
private fun ProductItem(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = buildString {
                        append(stringResource(R.string.product_code_short, product.code.ifBlank { "-" }))
                        append(" · ")
                        append(stringResource(R.string.quantity_detail, product.quantity))
                        if (product.description.isNotBlank()) {
                            append("\n")
                            append(product.description)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatMoney(product.price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatMoney(amount: Double): String {
    return "${'$'}${amount.toInt()}"
}

private fun Purchase.toShareText(): String {
    val productsText = products.joinToString(separator = "\n") { product ->
        "* ${product.code.ifBlank { "-" }} - ${product.name}: ${product.description.ifBlank { "-" }}"
    }

    return """
        Supermercado: $marketName
        Fecha: $date
        Hora: ${time.ifBlank { "-" }}
        Total: ${formatMoney(total)}

        Productos:

        $productsText
    """.trimIndent()
}
