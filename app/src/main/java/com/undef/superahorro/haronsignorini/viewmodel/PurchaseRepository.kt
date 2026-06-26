package com.undef.superahorro.haronsignorini.viewmodel

import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.data.local.PurchaseDao
import com.undef.superahorro.haronsignorini.data.local.toDomain
import com.undef.superahorro.haronsignorini.data.local.toEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class PurchaseRepository @Inject constructor(
    private val purchaseDao: PurchaseDao
) {
    val purchases: Flow<List<Purchase>> = purchaseDao.getPurchases()
        .map { purchasesWithProducts ->
            purchasesWithProducts.map { it.toDomain() }
        }

    suspend fun getPurchaseByIdFromDatabase(id: Int): Purchase? {
        return purchaseDao.getPurchaseById(id).first()?.toDomain()
    }

    suspend fun getProductsForPurchase(purchaseId: Int): List<Product> {
        return purchaseDao.getProductsForPurchase(purchaseId).first().map { it.toDomain() }
    }

    suspend fun addPurchase(purchase: Purchase): Int {
        val purchaseId = purchaseDao.insertPurchase(purchase.toEntity(id = 0)).toInt()
        purchaseDao.insertProducts(
            purchase.products.map { product ->
                product.toEntity(purchaseId = purchaseId, keepId = false)
            }
        )
        return purchaseId
    }

    suspend fun addProduct(purchaseId: Int, product: Product): Int {
        return purchaseDao.insertProduct(product.toEntity(purchaseId = purchaseId, keepId = false)).toInt()
    }

    suspend fun updatePurchase(purchase: Purchase) {
        val products = purchase.products.map { product ->
            product.toEntity(purchaseId = purchase.id, keepId = false)
        }
        purchaseDao.replacePurchaseWithProducts(
            purchase = purchase.toEntity(),
            products = products
        )
    }

    suspend fun updateProduct(purchaseId: Int, product: Product) {
        purchaseDao.updateProduct(product.toEntity(purchaseId = purchaseId))
    }

    suspend fun deletePurchase(purchase: Purchase) {
        purchaseDao.deletePurchase(purchase.toEntity())
    }

    suspend fun deletePurchaseById(purchaseId: Int) {
        purchaseDao.deletePurchaseById(purchaseId)
    }

    suspend fun deleteProduct(product: Product, purchaseId: Int) {
        purchaseDao.deleteProduct(product.toEntity(purchaseId = purchaseId))
    }

    suspend fun deleteProductById(productId: Int) {
        purchaseDao.deleteProductById(productId)
    }
}
