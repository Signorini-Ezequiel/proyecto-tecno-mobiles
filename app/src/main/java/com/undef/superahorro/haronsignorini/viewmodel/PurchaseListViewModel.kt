package com.undef.superahorro.haronsignorini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.haronsignorini.data.Purchase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
open class PurchaseListViewModel @Inject constructor(
    private val repository: PurchaseRepository
) : ViewModel() {
    val purchases: StateFlow<List<Purchase>> = repository.purchases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun getPurchaseById(id: Int): Purchase? {
        return purchases.value.find { it.id == id }
    }

    fun deletePurchase(purchaseId: Int) {
        viewModelScope.launch {
            repository.deletePurchaseById(purchaseId)
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
        }
    }
}
