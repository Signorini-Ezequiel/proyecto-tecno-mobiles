package com.undef.superahorro.haronsignorini.data

data class Purchase(
    val id: Int,
    val marketName: String,
    val date: String,
    val time: String = "",
    val total: Double,
    val productsCount: Int,
    val products: List<Product> = emptyList(),
    val ticketUri: String? = null
)

data class Product(
    val id: Int,
    val code: String = "",
    val name: String,
    val description: String = "",
    val quantity: Int,
    val price: Double
)
