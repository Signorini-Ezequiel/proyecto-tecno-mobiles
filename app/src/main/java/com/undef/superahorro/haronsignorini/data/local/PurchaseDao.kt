package com.undef.superahorro.haronsignorini.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Transaction
    @Query("SELECT * FROM purchases WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getPurchasesByUser(userEmail: String): Flow<List<PurchaseWithProducts>>

    @Transaction
    @Query("SELECT * FROM purchases WHERE id = :purchaseId AND userEmail = :userEmail")
    fun getPurchaseById(purchaseId: Int, userEmail: String): Flow<PurchaseWithProducts?>

    @Query("SELECT * FROM products WHERE purchaseId = :purchaseId ORDER BY id ASC")
    fun getProductsForPurchase(purchaseId: Int): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updatePurchase(purchase: PurchaseEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deletePurchase(purchase: PurchaseEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM purchases WHERE id = :purchaseId")
    suspend fun deletePurchaseById(purchaseId: Int)

    @Query("DELETE FROM purchases WHERE id = :purchaseId AND userEmail = :userEmail")
    suspend fun deletePurchaseByIdForUser(purchaseId: Int, userEmail: String)

    @Query("UPDATE purchases SET userEmail = :userEmail WHERE userEmail = ''")
    suspend fun claimUnassignedPurchases(userEmail: String)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Int)

    @Query("DELETE FROM products WHERE purchaseId = :purchaseId")
    suspend fun deleteProductsForPurchase(purchaseId: Int)

    @Transaction
    suspend fun replacePurchaseWithProducts(
        purchase: PurchaseEntity,
        products: List<ProductEntity>
    ) {
        updatePurchase(purchase)
        deleteProductsForPurchase(purchase.id)
        insertProducts(products)
    }
}
