package com.undef.superahorro.haronsignorini.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.ui.components.ConfirmationDialog
import com.undef.superahorro.haronsignorini.viewmodel.NewPurchaseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPurchaseScreen(
    navController: NavController,
    purchase: Purchase? = null,
    isEditing: Boolean = false,
    newPurchaseViewModel: NewPurchaseViewModel = hiltViewModel()
) {
    val marketName by newPurchaseViewModel.newPurchaseMarket.collectAsStateWithLifecycle()
    val date by newPurchaseViewModel.newPurchaseDate.collectAsStateWithLifecycle()
    var showValidation by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val marketHasError = showValidation && marketName.isBlank()
    val dateHasError = showValidation && date.isBlank()

    LaunchedEffect(purchase?.id, isEditing) {
        if (isEditing && purchase != null) {
            newPurchaseViewModel.loadPurchaseDraft(purchase)
        } else if (!isEditing) {
            newPurchaseViewModel.prepareNewPurchaseDraft()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isEditing) stringResource(R.string.edit_purchase) else stringResource(R.string.new_purchase),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isEditing) {
                        stringResource(R.string.edit_purchase_subtitle)
                    } else {
                        stringResource(R.string.new_purchase_subtitle)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                    PurchaseTextField(
                        value = marketName,
                        onValueChange = {
                            newPurchaseViewModel.setNewPurchaseDetails(it, date)
                        },
                        label = stringResource(R.string.market),
                        placeholder = stringResource(R.string.market_placeholder),
                        isError = marketHasError,
                        supportingText = if (marketHasError) stringResource(R.string.field_required) else null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Store,
                                contentDescription = null
                            )
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { },
                            label = { Text(stringResource(R.string.date)) },
                            placeholder = { Text(stringResource(R.string.date_placeholder)) },
                            isError = dateHasError,
                            supportingText = if (dateHasError) {
                                { Text(stringResource(R.string.field_required)) }
                            } else {
                                null
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = stringResource(R.string.select_date)
                                )
                            },
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (dateHasError) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                                disabledLabelColor = if (dateHasError) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledSupportingTextColor = MaterialTheme.colorScheme.error,
                                disabledContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        newPurchaseViewModel.setNewPurchaseDetails(
                                            marketName,
                                            millis.toPurchaseDateString()
                                        )
                                    }
                                    showDatePicker = false
                                }) {
                                    Text(stringResource(R.string.ok))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (marketName.isNotBlank() && date.isNotBlank()) {
                        newPurchaseViewModel.setNewPurchaseDetails(marketName, date)
                        if (isEditing && purchase != null) {
                            navController.navigate(AppRoutes.EditPurchaseProducts.createRoute(purchase.id))
                        } else {
                            navController.navigate(AppRoutes.NewProduct.route)
                        }
                    } else {
                        showValidation = true
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.AddShoppingCart,
                    contentDescription = if (isEditing) stringResource(R.string.edit_products) else stringResource(R.string.add_products)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = if (isEditing) stringResource(R.string.edit_products) else stringResource(R.string.add_products)
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    newPurchaseViewModel.setNewPurchaseDetails(marketName, date)
                    if (newPurchaseViewModel.saveNewPurchase()) {
                        if (isEditing && purchase != null) {
                            if (!navController.popBackStack()) {
                                navController.navigate(AppRoutes.PurchaseDetail.createRoute(purchase.id))
                            }
                        } else {
                            navController.navigate(AppRoutes.Home.route) {
                                popUpTo(AppRoutes.Home.route) { inclusive = true }
                            }
                        }
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
                    imageVector = Icons.Filled.Save,
                    contentDescription = stringResource(R.string.save_purchase)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = if (isEditing) stringResource(R.string.save_changes) else stringResource(R.string.save_purchase)
                )
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDeleteDialog = true },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = if (isEditing) {
                        stringResource(R.string.delete_purchase)
                    } else {
                        stringResource(R.string.discard_purchase)
                    }
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = if (isEditing) {
                        stringResource(R.string.delete_purchase)
                    } else {
                        stringResource(R.string.discard_purchase)
                    }
                )
            }

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

    if (showDeleteDialog) {
        ConfirmationDialog(
            title = if (isEditing) {
                stringResource(R.string.delete_purchase)
            } else {
                stringResource(R.string.discard_purchase)
            },
            message = if (isEditing && purchase != null) {
                stringResource(R.string.delete_purchase_confirmation, purchase.marketName)
            } else {
                stringResource(R.string.discard_purchase_confirmation)
            },
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                showDeleteDialog = false
                if (isEditing && purchase != null) {
                    newPurchaseViewModel.deleteCurrentPurchase()
                } else {
                    newPurchaseViewModel.clearNewPurchase()
                }
                navController.navigate(AppRoutes.Home.route) {
                    popUpTo(AppRoutes.Home.route) { inclusive = true }
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun PurchaseTextField(
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

private fun Long.toPurchaseDateString(): String {
    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = this@toPurchaseDateString
    }
    val localCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR))
        set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(localCalendar.timeInMillis))
}
