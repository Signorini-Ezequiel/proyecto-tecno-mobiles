package com.undef.superahorro.haronsignorini.data

data class Purchase(
    val id: Int,
    val marketName: String,
    val date: String,
    val total: Double,
    val productsCount: Int,
    val products: List<Product> = emptyList()
)

data class Product(
    val id: Int,
    val name: String,
    val quantity: Int,
    val price: Double
)
