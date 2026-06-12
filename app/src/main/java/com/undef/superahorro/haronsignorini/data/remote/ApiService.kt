package com.undef.superahorro.haronsignorini.data.remote

import com.undef.superahorro.haronsignorini.data.remote.dto.UserDto
import com.undef.superahorro.haronsignorini.data.remote.dto.PurchaseSyncRequest
import com.undef.superahorro.haronsignorini.data.remote.dto.PurchaseSyncResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @POST("posts")
    suspend fun syncPurchase(
        @Body request: PurchaseSyncRequest
    ): PurchaseSyncResponse
}
