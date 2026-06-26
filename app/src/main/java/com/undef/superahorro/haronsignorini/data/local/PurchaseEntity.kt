package com.undef.superahorro.haronsignorini.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val marketName: String,
    val date: String,
    val time: String = "",
    val total: Double,
    val productsCount: Int,
    val ticketUri: String?
)
