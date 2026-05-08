package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.example.superahorro.data.Product
import com.example.superahorro.navigation.AppRoutes
import com.example.superahorro.viewmodel.PurchaseViewModel

@Composable
fun NewProductScreen(
    navController: NavController,
    isEditingPurchase: Boolean = false,
    finishRoute: String? = null,
    purchaseViewModel: PurchaseViewModel = viewModel(navController.previousBackStackEntry!!)
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
                    Text(
                        text = if (isEditingPurchase) {
                            "Modifica los productos de la compra. El total se calcula automaticamente."
                        } else {
                            "Agrega productos a la compra. El total se calcula automaticamente."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

                items(products) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Text(
                                            text = "Cant: ${product.quantity}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Precio: $${product.price}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Total: $${product.quantity * product.price}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Row {
                                    IconButton(onClick = {
                                        editingProduct = product
                                        name = product.name
                                        quantity = product.quantity.toString()
                                        price = product.price.toString()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Editar producto",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { purchaseViewModel.removeProductFromNewPurchase(product.id) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Eliminar producto",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider()
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

private fun formatMoney(amount: Double): String {
    return "${'$'}${amount.toInt()}"
}
