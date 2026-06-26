package com.undef.superahorro.haronsignorini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.data.Product
@Composable
internal fun ProductsTable(
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
internal fun ProductsTableHeader() {
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
internal fun ProductTableRow(
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
                text = formatProductMoney(product.price),
                width = 50.dp
            )
            CompactTableCell(
                text = formatProductMoney(product.quantity * product.price),
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
internal fun ProductNameCell(
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
internal fun CompactTableCell(
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
