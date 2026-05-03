package com.example.superahorro.viewmodel

import androidx.lifecycle.ViewModel
import com.example.superahorro.data.Purchase
import com.example.superahorro.data.mockPurchases

class PurchaseViewModel : ViewModel() {
    val purchases: List<Purchase> = mockPurchases

    fun getPurchaseById(id: Int): Purchase? {
        return purchases.find { it.id == id }
    }
}
