package com.undef.superahorro.haronsignorini.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.haronsignorini.data.Product
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.data.SessionManager
import com.undef.superahorro.haronsignorini.util.persistTicketImageUri
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class NewPurchaseViewModel @Inject constructor(
    private val repository: PurchaseRepository,
    private val sessionManager: SessionManager,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private var editingPurchaseId: Int? = null
    private var editingPurchaseTime: String? = null
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
        editingPurchaseTime = purchase.time
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
            userEmail = sessionManager.getLoggedInEmail().orEmpty(),
            marketName = _newPurchaseMarket.value,
            date = _newPurchaseDate.value,
            time = editingPurchaseTime ?: currentPurchaseTime(),
            total = getNewPurchaseTotal(),
            productsCount = _newPurchaseProducts.value.size,
            products = _newPurchaseProducts.value,
            ticketUri = _ticketUri.value
        )
        viewModelScope.launch {
            if (purchaseId == null) {
                repository.addPurchase(purchase)
            } else {
                repository.updatePurchase(purchase)
            }
        }
        clearNewPurchase()
        return true
    }

    fun deleteCurrentPurchase() {
        val purchaseId = editingPurchaseId
        viewModelScope.launch {
            if (purchaseId != null) {
                repository.deletePurchaseById(purchaseId)
            }
            clearNewPurchase()
        }
    }

    fun clearNewPurchase() {
        editingPurchaseId = null
        editingPurchaseTime = null
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
                    persistTicketImageUri(appContext, it)
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
