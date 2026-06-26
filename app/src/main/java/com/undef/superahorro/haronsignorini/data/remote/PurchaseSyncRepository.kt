package com.undef.superahorro.haronsignorini.data.remote

import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.data.remote.dto.PurchaseSyncRequest
import com.undef.superahorro.haronsignorini.data.remote.dto.PurchaseSyncResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseSyncRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun syncPurchase(purchase: Purchase): PurchaseSyncResponse {
        val remoteUser = apiService.getUsers().firstOrNull()
        return apiService.syncPurchase(
            purchase.toSyncRequest(userId = remoteUser?.id ?: 1)
        )
    }

    private fun Purchase.toSyncRequest(userId: Int): PurchaseSyncRequest {
        return PurchaseSyncRequest(
            title = marketName,
            body = buildString {
                append("Fecha: ")
                append(date)
                if (time.isNotBlank()) {
                    append("\nHora: ")
                    append(time)
                }
                append("\nTotal: ")
                append(total)
                append("\nProductos: ")
                append(products.joinToString { "${it.name} x${it.quantity}" })
            },
            userId = userId
        )
    }
}
