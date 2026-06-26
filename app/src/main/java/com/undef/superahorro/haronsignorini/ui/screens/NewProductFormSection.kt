package com.undef.superahorro.haronsignorini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.data.Product
@Composable
internal fun ProductForm(
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
internal fun ProductSubmitButton(
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

internal data class ProductFormState(
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val quantity: String = "",
    val price: String = ""
) {
    val quantityValue: Int = quantity.toIntOrNull() ?: 0
    val priceValue: Double = price.toDoubleOrNull() ?: 0.0
}
