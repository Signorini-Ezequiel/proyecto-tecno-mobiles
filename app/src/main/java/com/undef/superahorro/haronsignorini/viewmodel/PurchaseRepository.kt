package com.undef.superahorro.haronsignorini.viewmodel

import android.content.Context
import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.data.local.PurchaseDao
import com.undef.superahorro.haronsignorini.data.local.SuperAhorroDatabase
import com.undef.superahorro.haronsignorini.data.local.toDomain
import com.undef.superahorro.haronsignorini.data.local.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

object PurchaseRepository {
    private val _purchases = MutableStateFlow<List<Purchase>>(emptyList())
    val purchases: StateFlow<List<Purchase>> = _purchases.asStateFlow()
    private var purchaseDao: PurchaseDao? = null
    private var initialized = false
    private var purchasesJob: Job? = null

    fun initialize(context: Context, scope: CoroutineScope) {
        if (initialized && purchasesJob?.isActive == true) {
            return
        }
        purchaseDao = SuperAhorroDatabase.getDatabase(context).purchaseDao()
        initialized = true
        purchasesJob = scope.launch {
            requireDao().getPurchases().collect { purchasesWithProducts ->
                _purchases.value = purchasesWithProducts.map { it.toDomain() }
            }
        }
    }

    fun getPurchaseById(id: Int): Purchase? {
        return _purchases.value.find { it.id == id }
    }

    suspend fun getPurchaseByIdFromDatabase(id: Int): Purchase? {
        return requireDao().getPurchaseById(id).first()?.toDomain()
    }

    suspend fun getProductsForPurchase(purchaseId: Int): List<Product> {
        return requireDao().getProductsForPurchase(purchaseId).first().map { it.toDomain() }
    }

    suspend fun addPurchase(purchase: Purchase): Int {
        val dao = requireDao()
        val purchaseId = dao.insertPurchase(purchase.toEntity(id = 0)).toInt()
        dao.insertProducts(
            purchase.products.map { product ->
                product.toEntity(purchaseId = purchaseId, keepId = false)
            }
        )
        return purchaseId
    }

    suspend fun addProduct(purchaseId: Int, product: Product): Int {
        return requireDao().insertProduct(product.toEntity(purchaseId = purchaseId, keepId = false)).toInt()
    }

    suspend fun updatePurchase(purchase: Purchase) {
        val products = purchase.products.map { product ->
            product.toEntity(purchaseId = purchase.id, keepId = false)
        }
        requireDao().replacePurchaseWithProducts(
            purchase = purchase.toEntity(),
            products = products
        )
    }

    suspend fun updateProduct(purchaseId: Int, product: Product) {
        requireDao().updateProduct(product.toEntity(purchaseId = purchaseId))
    }

    suspend fun deletePurchase(purchase: Purchase) {
        requireDao().deletePurchase(purchase.toEntity())
    }

    suspend fun deletePurchaseById(purchaseId: Int) {
        requireDao().deletePurchaseById(purchaseId)
    }

    suspend fun deleteProduct(product: Product, purchaseId: Int) {
        requireDao().deleteProduct(product.toEntity(purchaseId = purchaseId))
    }

    suspend fun deleteProductById(productId: Int) {
        requireDao().deleteProductById(productId)
    }

    private fun requireDao(): PurchaseDao {
        return checkNotNull(purchaseDao) {
            "PurchaseRepository must be initialized before use."
        }
    }
}
