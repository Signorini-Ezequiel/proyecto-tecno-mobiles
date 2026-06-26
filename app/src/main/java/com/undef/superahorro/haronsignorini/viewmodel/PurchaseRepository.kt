package com.undef.superahorro.haronsignorini.viewmodel

import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.data.SessionManager
import com.undef.superahorro.haronsignorini.data.local.PurchaseDao
import com.undef.superahorro.haronsignorini.data.local.toDomain
import com.undef.superahorro.haronsignorini.data.local.toEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class PurchaseRepository @Inject constructor(
    private val purchaseDao: PurchaseDao,
    private val sessionManager: SessionManager
) {
    val purchases: Flow<List<Purchase>> = sessionManager.currentEmailFlow.flatMapLatest { userEmail ->
        if (userEmail.isNullOrBlank()) {
            flowOf(emptyList())
        } else {
            flow {
                purchaseDao.claimUnassignedPurchases(userEmail)
                emitAll(
                    purchaseDao.getPurchasesByUser(userEmail).map { purchasesWithProducts ->
                        purchasesWithProducts.map { it.toDomain() }
                    }
                )
            }
        }
    }

    suspend fun getPurchaseByIdFromDatabase(id: Int): Purchase? {
        val userEmail = sessionManager.getLoggedInEmail() ?: return null
        return purchaseDao.getPurchaseById(id, userEmail).first()?.toDomain()
    }

    suspend fun getProductsForPurchase(purchaseId: Int): List<Product> {
        return purchaseDao.getProductsForPurchase(purchaseId).first().map { it.toDomain() }
    }

    suspend fun addPurchase(purchase: Purchase): Int {
        val userEmail = sessionManager.getLoggedInEmail() ?: return 0
        val purchaseId = purchaseDao.insertPurchase(
            purchase.copy(userEmail = userEmail).toEntity(id = 0)
        ).toInt()
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
        val userEmail = sessionManager.getLoggedInEmail() ?: return
        val products = purchase.products.map { product ->
            product.toEntity(purchaseId = purchase.id, keepId = false)
        }
        purchaseDao.replacePurchaseWithProducts(
            purchase = purchase.copy(userEmail = userEmail).toEntity(),
            products = products
        )
    }

    suspend fun updateProduct(purchaseId: Int, product: Product) {
        purchaseDao.updateProduct(product.toEntity(purchaseId = purchaseId))
    }

    suspend fun deletePurchase(purchase: Purchase) {
        val userEmail = sessionManager.getLoggedInEmail() ?: return
        purchaseDao.deletePurchase(purchase.copy(userEmail = userEmail).toEntity())
    }

    suspend fun deletePurchaseById(purchaseId: Int) {
        val userEmail = sessionManager.getLoggedInEmail() ?: return
        purchaseDao.deletePurchaseByIdForUser(purchaseId, userEmail)
    }

    suspend fun deleteProduct(product: Product, purchaseId: Int) {
        purchaseDao.deleteProduct(product.toEntity(purchaseId = purchaseId))
    }

    suspend fun deleteProductById(productId: Int) {
        purchaseDao.deleteProductById(productId)
    }
}
