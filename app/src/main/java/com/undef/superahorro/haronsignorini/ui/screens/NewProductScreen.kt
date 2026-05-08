package com.undef.superahorro.haronsignorini.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseViewModel

@Composable
fun NewProductScreen(
    navController: NavController,
    isEditingPurchase: Boolean = false,
    finishRoute: String? = null,
    purchaseViewModel: PurchaseViewModel
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var showValidation by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    val nameHasError = showValidation && name.isBlank()
    val quantityHasError = showValidation && (quantity.isBlank() || quantity.toIntOrNull() ?: 0 <= 0)
    val priceHasError = showValidation && (price.isBlank() || price.toDoubleOrNull() ?: 0.0 <= 0.0)

    val products by purchaseViewModel.newPurchaseProducts.collectAsState()
    val total = purchaseViewModel.getNewPurchaseTotal()
    val market = purchaseViewModel.getNewPurchaseMarket()
    val purchaseDate = purchaseViewModel.getNewPurchaseDate()
    val quantityValue = quantity.toIntOrNull() ?: 0
    val priceValue = price.toDoubleOrNull() ?: 0.0
    val currentProductTotal = quantityValue * priceValue

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isEditingPurchase) "Editar productos" else "Agregar productos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryPill(
                            modifier = Modifier.weight(1f),
                            label = "Supermercado",
                            value = market.ifBlank { "Sin definir" }
                        )
                        SummaryPill(
                            modifier = Modifier.weight(1f),
                            label = "Fecha",
                            value = purchaseDate.ifBlank { "Sin definir" }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ProductTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nombre",
                            placeholder = "Ej: Leche entera",
                            isError = nameHasError,
                            supportingText = if (nameHasError) "Campo requerido" else null,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Inventory2,
                                    contentDescription = null
                                )
                            }
                        )

                        ProductTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = "Cantidad",
                            placeholder = "Ej: 2",
                            isError = quantityHasError,
                            supportingText = if (quantityHasError) "Debe ser un número mayor a 0" else null,
                            keyboardType = KeyboardType.Number,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Numbers,
                                    contentDescription = null
                                )
                            }
                        )

                        ProductTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = "Precio",
                            placeholder = "Ej: 1800",
                            isError = priceHasError,
                            supportingText = if (priceHasError) "Debe ser un número mayor a 0" else null,
                            keyboardType = KeyboardType.Number,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.AttachMoney,
                                    contentDescription = null
                                )
                            }
                        )

                        PurchaseTotalsStrip(
                            productTotal = currentProductTotal,
                            purchaseTotal = total
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val qty = quantity.toIntOrNull() ?: 0
                                val prc = price.toDoubleOrNull() ?: 0.0
                                if (name.isNotBlank() && qty > 0 && prc > 0) {
                                    if (editingProduct != null) {
                                        purchaseViewModel.updateProductInNewPurchase(editingProduct!!.id, name, qty, prc)
                                        editingProduct = null
                                    } else {
                                        purchaseViewModel.addProductToNewPurchase(name, qty, prc)
                                    }
                                    name = ""
                                    quantity = ""
                                    price = ""
                                    showValidation = false
                                } else {
                                    showValidation = true
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = if (editingProduct != null) Icons.Filled.Save else Icons.Filled.Add,
                                contentDescription = if (editingProduct != null) "Guardar cambios" else "Agregar producto"
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = if (editingProduct != null) "Guardar cambios" else "Agregar producto"
                            )
                        }

                        if (editingProduct != null) {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    editingProduct = null
                                    name = ""
                                    quantity = ""
                                    price = ""
                                    showValidation = false
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Cancelar edición")
                            }
                        }
                    }
                }
            }

            if (products.isNotEmpty()) {
                item {
                    Text(
                        text = "Productos agregados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    ProductsTable(
                        products = products,
                        onEdit = { product ->
                            editingProduct = product
                            name = product.name
                            quantity = product.quantity.toString()
                            price = product.price.toString()
                        },
                        onDelete = { product ->
                            purchaseViewModel.removeProductFromNewPurchase(product.id)
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = formatMoney(total),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (isEditingPurchase) {
                                if (!navController.popBackStack(AppRoutes.PurchaseDetail.route, false)) {
                                    finishRoute?.let { navController.navigate(it) }
                                }
                            } else if (purchaseViewModel.saveNewPurchase()) {
                                navController.navigate(AppRoutes.Home.route) {
                                    popUpTo(AppRoutes.Home.route) { inclusive = true }
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = if (isEditingPurchase) "Terminar edicion" else "Guardar compra"
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = if (isEditingPurchase) "Listo" else "Guardar compra"
                        )
                    }
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
                    Text("Volver")
                }
            }
        }
    }
}

@Composable
private fun ProductTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean,
    supportingText: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = isError,
        supportingText = supportingText?.let {
            { Text(it) }
        },
        leadingIcon = leadingIcon,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun PurchaseTotalsStrip(
    productTotal: Double,
    purchaseTotal: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryPill(
            modifier = Modifier.weight(1f),
            label = "Total producto",
            value = formatMoney(productTotal)
        )
        SummaryPill(
            modifier = Modifier.weight(1f),
            label = "Total compra",
            value = formatMoney(purchaseTotal)
        )
    }
}

@Composable
private fun SummaryPill(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ProductsTable(
    products: List<Product>,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ProductsTableHeader()

            products.forEachIndexed { index, product ->
                ProductTableRow(
                    product = product,
                    striped = index % 2 != 0,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun ProductsTableHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductNameCell(
                text = "Producto",
                header = true,
                modifier = Modifier.weight(1f)
            )
            CompactTableCell(
                text = "Cant.",
                width = 42.dp,
                header = true
            )
            CompactTableCell(
                text = "Precio",
                width = 50.dp,
                header = true
            )
            CompactTableCell(
                text = "Subt.",
                width = 50.dp,
                header = true
            )
            CompactTableCell(
                text = "Acc.",
                width = 56.dp,
                header = true
            )
        }
    }
}

@Composable
private fun ProductTableRow(
    product: Product,
    striped: Boolean,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (striped) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductNameCell(
                text = product.name,
                modifier = Modifier.weight(1f)
            )
            CompactTableCell(
                text = product.quantity.toString(),
                width = 42.dp
            )
            CompactTableCell(
                text = formatMoney(product.price),
                width = 50.dp
            )
            CompactTableCell(
                text = formatMoney(product.quantity * product.price),
                width = 50.dp,
                emphasized = true
            )
            Row(
                modifier = Modifier.width(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onEdit(product) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar producto",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = { onDelete(product) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar producto",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductNameCell(
    text: String,
    modifier: Modifier = Modifier,
    header: Boolean = false,
    emphasized: Boolean = false
) {
    Text(
        modifier = modifier.padding(end = 8.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = when {
            header -> FontWeight.SemiBold
            emphasized -> FontWeight.SemiBold
            else -> FontWeight.Normal
        },
        color = when {
            header -> MaterialTheme.colorScheme.onSurface
            emphasized -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun CompactTableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    header: Boolean = false,
    emphasized: Boolean = false
) {
    Text(
        modifier = Modifier.width(width),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = when {
            header -> FontWeight.SemiBold
            emphasized -> FontWeight.SemiBold
            else -> FontWeight.Normal
        },
        color = when {
            header -> MaterialTheme.colorScheme.onSurface
            emphasized -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Clip
    )
}

private fun formatMoney(amount: Double): String {
    return "${'$'}${amount.toInt()}"
}
