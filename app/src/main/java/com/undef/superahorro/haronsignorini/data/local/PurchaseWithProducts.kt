package com.undef.superahorro.haronsignorini.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class PurchaseWithProducts(
    @Embedded val purchase: PurchaseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "purchaseId"
    )
    val products: List<ProductEntity>
)
