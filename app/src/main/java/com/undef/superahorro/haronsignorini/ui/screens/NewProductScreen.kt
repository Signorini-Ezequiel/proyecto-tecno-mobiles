package com.undef.superahorro.haronsignorini.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.ui.components.ConfirmationDialog
import com.undef.superahorro.haronsignorini.util.createTicketImageUri
import com.undef.superahorro.haronsignorini.viewmodel.NewPurchaseViewModel

@Composable
fun NewProductScreen(
    navController: NavController,
    isEditingPurchase: Boolean = false,
    finishRoute: String? = null,
    newPurchaseViewModel: NewPurchaseViewModel
) {
    var showValidation by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var productPendingDelete by remember { mutableStateOf<Product?>(null) }
    var showDiscardPurchaseDialog by remember { mutableStateOf(false) }
    var isCameraPermissionDenied by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val products by newPurchaseViewModel.newPurchaseProducts.collectAsState()
    val productDraft by newPurchaseViewModel.productDraft.collectAsState()
    val ticketUri by newPurchaseViewModel.ticketUri.collectAsState()
    val pendingCameraTicketUri by newPurchaseViewModel.pendingCameraTicketUri.collectAsState()
    val formState = ProductFormState(
        code = productDraft.code,
        name = productDraft.name,
        description = productDraft.description,
        quantity = productDraft.quantity,
        price = productDraft.price
    )
    val total = newPurchaseViewModel.getNewPurchaseTotal()
    val currentProductTotal = formState.quantityValue * formState.priceValue
    val ticketPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        newPurchaseViewModel.setTicketUri(uri?.toString())
    }
    val ticketCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { wasTaken ->
        newPurchaseViewModel.confirmTicketPhotoTaken(wasTaken)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraPermissionDenied = false
            val uri = createTicketImageUri(context)
            newPurchaseViewModel.prepareTicketPhoto(uri.toString())
            ticketCameraLauncher.launch(uri)
        } else {
            isCameraPermissionDenied = true
        }
    }

    fun launchTicketCamera() {
        isCameraPermissionDenied = false
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val uri = createTicketImageUri(context)
            newPurchaseViewModel.prepareTicketPhoto(uri.toString())
            ticketCameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProductHeaderSection(isEditingPurchase = isEditingPurchase)
            }
            item {
                PurchaseSummarySection(
                    market = newPurchaseViewModel.getNewPurchaseMarket(),
                    purchaseDate = newPurchaseViewModel.getNewPurchaseDate()
                )
            }
            item {
                ProductForm(
                    formState = formState,
                    showValidation = showValidation,
                    editingProduct = editingProduct,
                    currentProductTotal = currentProductTotal,
                    purchaseTotal = total,
                    onNameChange = {
                        newPurchaseViewModel.setProductDraft(
                            formState.code,
                            it,
                            formState.description,
                            formState.quantity,
                            formState.price
                        )
                    },
                    onCodeChange = {
                        newPurchaseViewModel.setProductDraft(
                            it,
                            formState.name,
                            formState.description,
                            formState.quantity,
                            formState.price
                        )
                    },
                    onDescriptionChange = {
                        newPurchaseViewModel.setProductDraft(
                            formState.code,
                            formState.name,
                            it,
                            formState.quantity,
                            formState.price
                        )
                    },
                    onQuantityChange = {
                        newPurchaseViewModel.setProductDraft(
                            formState.code,
                            formState.name,
                            formState.description,
                            it,
                            formState.price
                        )
                    },
                    onPriceChange = {
                        newPurchaseViewModel.setProductDraft(
                            formState.code,
                            formState.name,
                            formState.description,
                            formState.quantity,
                            it
                        )
                    },
                    onSubmit = {
                        val qty = formState.quantityValue
                        val prc = formState.priceValue
                        if (formState.name.isNotBlank() && qty > 0 && prc > 0) {
                            if (editingProduct != null) {
                                newPurchaseViewModel.updateProductInNewPurchase(
                                    editingProduct!!.id,
                                    formState.code,
                                    formState.name,
                                    formState.description,
                                    qty,
                                    prc
                                )
                                editingProduct = null
                            } else {
                                newPurchaseViewModel.addProductToNewPurchase(
                                    formState.code,
                                    formState.name,
                                    formState.description,
                                    qty,
                                    prc
                                )
                            }
                            showValidation = false
                        } else {
                            showValidation = true
                        }
                    },
                    onCancelEdit = {
                        editingProduct = null
                        newPurchaseViewModel.clearProductDraft()
                        showValidation = false
                    }
                )
            }
            item {
                TicketSection(
                    ticketUri = ticketUri,
                    isTakingPhoto = pendingCameraTicketUri != null,
                    isCameraPermissionDenied = isCameraPermissionDenied,
                    onSelectTicket = {
                        ticketPickerLauncher.launch("image/*")
                    },
                    onTakePhoto = {
                        launchTicketCamera()
                    }
                )
            }

            if (products.isNotEmpty()) {
                item {
                    AddedProductsSectionTitle()
                }
                item {
                    ProductsTable(
                        products = products,
                        onEdit = { product ->
                            editingProduct = product
                            newPurchaseViewModel.setProductDraft(
                                code = product.code,
                                name = product.name,
                                description = product.description,
                                quantity = product.quantity.toString(),
                                price = product.price.toString()
                            )
                        },
                        onDelete = { product ->
                            productPendingDelete = product
                        }
                    )
                }
            }

            item {
                PurchaseTotalRow(total = total)
            }
            item {
                SavePurchaseButton(
                    isEditingPurchase = isEditingPurchase,
                    onClick = {
                        if (isEditingPurchase) {
                            newPurchaseViewModel.saveNewPurchase()
                            if (!navController.popBackStack(AppRoutes.PurchaseDetail.route, false)) {
                                finishRoute?.let { navController.navigate(it) }
                            }
                        } else if (newPurchaseViewModel.saveNewPurchase()) {
                            navController.navigate(AppRoutes.Home.route) {
                                popUpTo(AppRoutes.Home.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
            item {
                DiscardPurchaseButton(
                    isEditingPurchase = isEditingPurchase,
                    onClick = { showDiscardPurchaseDialog = true }
                )
            }

            item {
                BackButton(onClick = { navController.popBackStack() })
            }
        }
    }

    productPendingDelete?.let { product ->
        ConfirmationDialog(
            title = stringResource(R.string.delete_product),
            message = stringResource(R.string.delete_product_confirmation, product.name),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                newPurchaseViewModel.removeProductFromNewPurchase(product.id)
                if (editingProduct?.id == product.id) {
                    editingProduct = null
                    newPurchaseViewModel.clearProductDraft()
                    showValidation = false
                }
                productPendingDelete = null
            },
            onDismiss = { productPendingDelete = null }
        )
    }

    if (showDiscardPurchaseDialog) {
        ConfirmationDialog(
            title = if (isEditingPurchase) {
                stringResource(R.string.delete_purchase)
            } else {
                stringResource(R.string.discard_purchase)
            },
            message = if (isEditingPurchase) {
                stringResource(R.string.delete_current_purchase_confirmation)
            } else {
                stringResource(R.string.discard_purchase_confirmation)
            },
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                showDiscardPurchaseDialog = false
                if (isEditingPurchase) {
                    newPurchaseViewModel.deleteCurrentPurchase()
                } else {
                    newPurchaseViewModel.clearNewPurchase()
                }
                navController.navigate(AppRoutes.Home.route) {
                    popUpTo(AppRoutes.Home.route) { inclusive = true }
                }
            },
            onDismiss = { showDiscardPurchaseDialog = false }
        )
    }
}

@Composable
private fun TicketSection(
    ticketUri: String?,
    isTakingPhoto: Boolean,
    isCameraPermissionDenied: Boolean,
    onSelectTicket: () -> Unit,
    onTakePhoto: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.purchase_ticket),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TicketPreview(ticketUri = ticketUri)
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onTakePhoto,
                enabled = !isTakingPhoto,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = stringResource(R.string.take_ticket_photo)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.take_ticket_photo)
                )
            }
            if (isCameraPermissionDenied) {
                Text(
                    text = stringResource(R.string.camera_permission_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSelectTicket,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.select_ticket))
            }
        }
    }
}

@Composable
private fun TicketPreview(ticketUri: String?) {
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

@Composable
private fun ProductHeaderSection(isEditingPurchase: Boolean) {
    Text(
        text = if (isEditingPurchase) stringResource(R.string.edit_products) else stringResource(R.string.add_products),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun PurchaseSummarySection(
    market: String,
    purchaseDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryPill(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.market),
                value = market.ifBlank { stringResource(R.string.undefined) }
            )
            SummaryPill(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.date),
                value = purchaseDate.ifBlank { stringResource(R.string.undefined) }
            )
        }
    }
}

@Composable
private fun ProductForm(
    formState: ProductFormState,
    showValidation: Boolean,
    editingProduct: Product?,
    currentProductTotal: Double,
    purchaseTotal: Double,
    onCodeChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancelEdit: () -> Unit
) {
    val nameHasError = showValidation && formState.name.isBlank()
    val quantityHasError = showValidation && formState.quantityValue <= 0
    val priceHasError = showValidation && formState.priceValue <= 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProductTextField(
                value = formState.code,
                onValueChange = onCodeChange,
                label = stringResource(R.string.product_code),
                placeholder = stringResource(R.string.product_code_placeholder),
                isError = false,
                supportingText = null,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Code, contentDescription = null)
                }
            )
            ProductTextField(
                value = formState.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.product_name),
                placeholder = stringResource(R.string.product_placeholder),
                isError = nameHasError,
                supportingText = if (nameHasError) stringResource(R.string.field_required) else null,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Inventory2, contentDescription = null)
                }
            )
            ProductTextField(
                value = formState.description,
                onValueChange = onDescriptionChange,
                label = stringResource(R.string.product_description),
                placeholder = stringResource(R.string.product_description_placeholder),
                isError = false,
                supportingText = null,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Description, contentDescription = null)
                }
            )
            ProductTextField(
                value = formState.quantity,
                onValueChange = onQuantityChange,
                label = stringResource(R.string.quantity),
                placeholder = stringResource(R.string.quantity_placeholder),
                isError = quantityHasError,
                supportingText = if (quantityHasError) stringResource(R.string.number_greater_than_zero) else null,
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Numbers, contentDescription = null)
                }
            )
            ProductTextField(
                value = formState.price,
                onValueChange = onPriceChange,
                label = stringResource(R.string.price),
                placeholder = stringResource(R.string.price_placeholder),
                isError = priceHasError,
                supportingText = if (priceHasError) stringResource(R.string.number_greater_than_zero) else null,
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.AttachMoney, contentDescription = null)
                }
            )
            PurchaseTotalsStrip(
                productTotal = currentProductTotal,
                purchaseTotal = purchaseTotal
            )
            ProductSubmitButton(
                editingProduct = editingProduct,
                onClick = onSubmit
            )
            if (editingProduct != null) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCancelEdit,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.cancel_edit))
                }
            }
        }
    }
}

@Composable
private fun ProductSubmitButton(
    editingProduct: Product?,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        val text = if (editingProduct != null) {
            stringResource(R.string.save_changes)
        } else {
            stringResource(R.string.add_product)
        }
        Icon(
            imageVector = if (editingProduct != null) Icons.Filled.Save else Icons.Filled.Add,
            contentDescription = text
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text
        )
    }
}

@Composable
private fun AddedProductsSectionTitle() {
    Text(
        text = stringResource(R.string.added_products),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun PurchaseTotalRow(total: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${stringResource(R.string.total)}:",
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

@Composable
private fun SavePurchaseButton(
    isEditingPurchase: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        val text = if (isEditingPurchase) {
            stringResource(R.string.finish)
        } else {
            stringResource(R.string.save_purchase)
        }
        Icon(
            imageVector = Icons.Filled.Save,
            contentDescription = if (isEditingPurchase) {
                stringResource(R.string.finish_editing)
            } else {
                stringResource(R.string.save_purchase)
            }
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text
        )
    }
}

@Composable
private fun DiscardPurchaseButton(
    isEditingPurchase: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        val text = if (isEditingPurchase) {
            stringResource(R.string.delete_purchase)
        } else {
            stringResource(R.string.discard_purchase)
        }
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = text
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(stringResource(R.string.back))
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
            label = stringResource(R.string.total_product),
            value = formatMoney(productTotal)
        )
        SummaryPill(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.total_purchase),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                text = stringResource(R.string.product),
                header = true,
                modifier = Modifier.weight(1f)
            )
            CompactTableCell(
                text = stringResource(R.string.quantity_short),
                width = 42.dp,
                header = true
            )
            CompactTableCell(
                text = stringResource(R.string.price),
                width = 50.dp,
                header = true
            )
            CompactTableCell(
                text = stringResource(R.string.subtotal_short),
                width = 50.dp,
                header = true
            )
            CompactTableCell(
                text = stringResource(R.string.actions_short),
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
                        contentDescription = stringResource(R.string.edit_product),
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
                        contentDescription = stringResource(R.string.delete_product),
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

private data class ProductFormState(
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val quantity: String = "",
    val price: String = ""
) {
    val quantityValue: Int = quantity.toIntOrNull() ?: 0
    val priceValue: Double = price.toDoubleOrNull() ?: 0.0
}

private fun formatMoney(amount: Double): String {
    return "${'$'}${amount.toInt()}"
}
