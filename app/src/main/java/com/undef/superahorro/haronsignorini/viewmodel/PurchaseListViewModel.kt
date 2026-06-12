package com.undef.superahorro.haronsignorini.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.haronsignorini.data.Purchase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class PurchaseListViewModel(application: Application) : AndroidViewModel(application) {
    val purchases: StateFlow<List<Purchase>> = PurchaseRepository.purchases

    init {
        PurchaseRepository.initialize(application, viewModelScope)
    }

    fun getPurchaseById(id: Int): Purchase? {
        return PurchaseRepository.getPurchaseById(id)
    }

    fun deletePurchase(purchaseId: Int) {
        viewModelScope.launch {
            PurchaseRepository.deletePurchaseById(purchaseId)
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            PurchaseRepository.deleteProductById(productId)
        }
    }
}
