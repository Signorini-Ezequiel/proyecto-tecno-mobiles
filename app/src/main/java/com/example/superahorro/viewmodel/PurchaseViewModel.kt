package com.example.superahorro.viewmodel

import androidx.lifecycle.ViewModel
import com.example.superahorro.data.Product
import com.example.superahorro.data.Purchase
import com.example.superahorro.data.mockPurchases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PurchaseViewModel : ViewModel() {
    private val _purchases = MutableStateFlow(mockPurchases)
    val purchases: StateFlow<List<Purchase>> = _purchases.asStateFlow()

    // Estado temporal para nueva compra
    private var newPurchaseMarket: String = ""
    private var newPurchaseDate: String = ""
    private val _newPurchaseProducts = MutableStateFlow<List<Product>>(emptyList())
    val newPurchaseProducts: StateFlow<List<Product>> = _newPurchaseProducts.asStateFlow()

    fun setNewPurchaseDetails(marketName: String, date: String) {
        newPurchaseMarket = marketName
        newPurchaseDate = date
    }

    fun loadPurchaseDraft(purchase: Purchase) {
        newPurchaseMarket = purchase.marketName
        newPurchaseDate = purchase.date
        _newPurchaseProducts.value = purchase.products
    }

    fun addProductToNewPurchase(name: String, quantity: Int, price: Double) {
        val id = (_newPurchaseProducts.value.maxOfOrNull { it.id } ?: 0) + 1
        val newProduct = Product(id, name, quantity, price)
        _newPurchaseProducts.value = _newPurchaseProducts.value + newProduct
    }

    fun getNewPurchaseProducts(): List<Product> = _newPurchaseProducts.value

    fun getNewPurchaseMarket(): String = newPurchaseMarket
    fun getNewPurchaseDate(): String = newPurchaseDate

    fun saveNewPurchase(): Boolean {
        if (newPurchaseMarket.isBlank() || newPurchaseDate.isBlank() || _newPurchaseProducts.value.isEmpty()) {
            return false
        }
        val id = (_purchases.value.maxOfOrNull { it.id } ?: 0) + 1
        val total = getNewPurchaseTotal()
        val purchase = Purchase(
            id = id,
            marketName = newPurchaseMarket,
            date = newPurchaseDate,
            total = total,
            productsCount = _newPurchaseProducts.value.size,
            products = _newPurchaseProducts.value
        )
        _purchases.value = _purchases.value + purchase
        // Limpiar estado temporal
        clearNewPurchase()
        return true
    }

    fun clearNewPurchase() {
        newPurchaseMarket = ""
        newPurchaseDate = ""
        _newPurchaseProducts.value = emptyList()
    }

    fun removeProductFromNewPurchase(productId: Int) {
        _newPurchaseProducts.value = _newPurchaseProducts.value.filter { it.id != productId }
    }

    fun updateProductInNewPurchase(productId: Int, name: String, quantity: Int, price: Double) {
        _newPurchaseProducts.value = _newPurchaseProducts.value.map {
            if (it.id == productId) it.copy(name = name, quantity = quantity, price = price) else it
        }
    }

    fun getNewPurchaseTotal(): Double = _newPurchaseProducts.value.sumOf { it.price * it.quantity }

    fun getPurchaseById(id: Int): Purchase? {
        return _purchases.value.find { it.id == id }
    }
}
