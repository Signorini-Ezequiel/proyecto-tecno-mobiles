package com.undef.superahorro.haronsignorini.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class AppDropdownMenuItem(
    val text: String,
    val icon: ImageVector,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)

@Composable
fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<AppDropdownMenuItem>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item.text) },
                leadingIcon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                enabled = item.enabled,
                onClick = {
                    onDismissRequest()
                    item.onClick()
                }
            )
        }
    }
}
