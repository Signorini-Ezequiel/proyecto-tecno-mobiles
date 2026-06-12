package com.undef.superahorro.haronsignorini.data.local

import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.data.Purchase

fun PurchaseWithProducts.toDomain(): Purchase {
    return Purchase(
        id = purchase.id,
        marketName = purchase.marketName,
        date = purchase.date,
        time = purchase.time,
        total = purchase.total,
        productsCount = purchase.productsCount,
        products = products.map { it.toDomain() },
        ticketUri = purchase.ticketUri
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        code = code,
        name = name,
        description = description,
        quantity = quantity,
        price = price
    )
}

fun Purchase.toEntity(id: Int = this.id): PurchaseEntity {
    return PurchaseEntity(
        id = id,
        marketName = marketName,
        date = date,
        time = time,
        total = total,
        productsCount = productsCount,
        ticketUri = ticketUri
    )
}

fun Product.toEntity(purchaseId: Int, keepId: Boolean = true): ProductEntity {
    return ProductEntity(
        id = if (keepId) id else 0,
        purchaseId = purchaseId,
        code = code.ifBlank { "PROD-${if (id > 0) id else purchaseId}" },
        name = name,
        description = description.ifBlank { name },
        quantity = quantity,
        price = price
    )
}
