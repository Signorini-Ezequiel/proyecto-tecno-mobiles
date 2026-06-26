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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val products by newPurchaseViewModel.newPurchaseProducts.collectAsStateWithLifecycle()
    val productDraft by newPurchaseViewModel.productDraft.collectAsStateWithLifecycle()
    val ticketUri by newPurchaseViewModel.ticketUri.collectAsStateWithLifecycle()
    val pendingCameraTicketUri by newPurchaseViewModel.pendingCameraTicketUri.collectAsStateWithLifecycle()
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
