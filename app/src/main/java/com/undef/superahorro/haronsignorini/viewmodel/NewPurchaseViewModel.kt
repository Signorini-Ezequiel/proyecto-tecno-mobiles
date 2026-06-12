package com.undef.superahorro.haronsignorini.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.util.persistTicketImageUri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPurchaseViewModel(application: Application) : AndroidViewModel(application) {
    private var editingPurchaseId: Int? = null
    private val _newPurchaseMarket = MutableStateFlow("")
    val newPurchaseMarket: StateFlow<String> = _newPurchaseMarket.asStateFlow()
    private val _newPurchaseDate = MutableStateFlow("")
    val newPurchaseDate: StateFlow<String> = _newPurchaseDate.asStateFlow()
    private val _newPurchaseProducts = MutableStateFlow<List<Product>>(emptyList())
    val newPurchaseProducts: StateFlow<List<Product>> = _newPurchaseProducts.asStateFlow()
    private val _productDraft = MutableStateFlow(ProductDraft())
    val productDraft: StateFlow<ProductDraft> = _productDraft.asStateFlow()
    private val _ticketUri = MutableStateFlow<String?>(null)
    val ticketUri: StateFlow<String?> = _ticketUri.asStateFlow()
    private val _pendingCameraTicketUri = MutableStateFlow<String?>(null)
    val pendingCameraTicketUri: StateFlow<String?> = _pendingCameraTicketUri.asStateFlow()

    init {
        PurchaseRepository.initialize(application, viewModelScope)
    }

    fun setNewPurchaseDetails(marketName: String, date: String) {
        _newPurchaseMarket.value = marketName
        _newPurchaseDate.value = date
    }

    fun prepareNewPurchaseDraft() {
        if (editingPurchaseId != null) {
            clearNewPurchase()
        }
    }

    fun loadPurchaseDraft(purchase: Purchase) {
        editingPurchaseId = purchase.id
        _newPurchaseMarket.value = purchase.marketName
        _newPurchaseDate.value = purchase.date
        _newPurchaseProducts.value = purchase.products
        _ticketUri.value = purchase.ticketUri
        _productDraft.value = ProductDraft()
    }

    fun addProductToNewPurchase(
        code: String,
        name: String,
        description: String,
        quantity: Int,
        price: Double
    ) {
        val id = (_newPurchaseProducts.value.maxOfOrNull { it.id } ?: 0) + 1
        val newProduct = Product(
            id = id,
            code = code.ifBlank { "PROD-$id" },
            name = name,
            description = description.ifBlank { name },
            quantity = quantity,
            price = price
        )
        _newPurchaseProducts.value = _newPurchaseProducts.value + newProduct
        clearProductDraft()
    }

    fun setProductDraft(
        code: String,
        name: String,
        description: String,
        quantity: String,
        price: String
    ) {
        _productDraft.value = ProductDraft(
            code = code,
            name = name,
            description = description,
            quantity = quantity,
            price = price
        )
    }

    fun clearProductDraft() {
        _productDraft.value = ProductDraft()
    }

    fun getNewPurchaseMarket(): String = _newPurchaseMarket.value

    fun getNewPurchaseDate(): String = _newPurchaseDate.value

    fun saveNewPurchase(): Boolean {
        if (!isDraftValid()) {
            return false
        }
        val purchaseId = editingPurchaseId
        val purchase = Purchase(
            id = purchaseId ?: 0,
            marketName = _newPurchaseMarket.value,
            date = _newPurchaseDate.value,
            time = purchaseId?.let { PurchaseRepository.getPurchaseById(it)?.time }
                ?: currentPurchaseTime(),
            total = getNewPurchaseTotal(),
            productsCount = _newPurchaseProducts.value.size,
            products = _newPurchaseProducts.value,
            ticketUri = _ticketUri.value
        )
        viewModelScope.launch {
            if (purchaseId == null) {
                PurchaseRepository.addPurchase(purchase)
            } else {
                PurchaseRepository.updatePurchase(purchase)
            }
        }
        clearNewPurchase()
        return true
    }

    fun deleteCurrentPurchase() {
        val purchaseId = editingPurchaseId
        viewModelScope.launch {
            if (purchaseId != null) {
                PurchaseRepository.deletePurchaseById(purchaseId)
            }
            clearNewPurchase()
        }
    }

    fun clearNewPurchase() {
        editingPurchaseId = null
        _newPurchaseMarket.value = ""
        _newPurchaseDate.value = ""
        _newPurchaseProducts.value = emptyList()
        _productDraft.value = ProductDraft()
        _ticketUri.value = null
        _pendingCameraTicketUri.value = null
    }

    fun setTicketUri(uri: String?) {
        viewModelScope.launch {
            _ticketUri.value = uri?.let {
                withContext(Dispatchers.IO) {
                    persistTicketImageUri(getApplication(), it)
                }
            }
            _pendingCameraTicketUri.value = null
        }
    }

    fun prepareTicketPhoto(uri: String) {
        viewModelScope.launch {
            _pendingCameraTicketUri.value = uri
        }
    }

    fun confirmTicketPhotoTaken(wasTaken: Boolean) {
        viewModelScope.launch {
            val uri = _pendingCameraTicketUri.value
            if (wasTaken && uri != null) {
                _ticketUri.value = uri
            }
            _pendingCameraTicketUri.value = null
        }
    }

    fun removeProductFromNewPurchase(productId: Int) {
        _newPurchaseProducts.value = _newPurchaseProducts.value.filter { it.id != productId }
    }

    fun updateProductInNewPurchase(
        productId: Int,
        code: String,
        name: String,
        description: String,
        quantity: Int,
        price: Double
    ) {
        _newPurchaseProducts.value = _newPurchaseProducts.value.map {
            if (it.id == productId) {
                it.copy(
                    code = code.ifBlank { it.code.ifBlank { "PROD-$productId" } },
                    name = name,
                    description = description.ifBlank { name },
                    quantity = quantity,
                    price = price
                )
            } else {
                it
            }
        }
        clearProductDraft()
    }

    fun getNewPurchaseTotal(): Double = _newPurchaseProducts.value.sumOf { it.price * it.quantity }

    private fun isDraftValid(): Boolean {
        return _newPurchaseMarket.value.isNotBlank() &&
            _newPurchaseDate.value.isNotBlank()
    }
}

private fun currentPurchaseTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}

data class ProductDraft(
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val quantity: String = "",
    val price: String = ""
)
