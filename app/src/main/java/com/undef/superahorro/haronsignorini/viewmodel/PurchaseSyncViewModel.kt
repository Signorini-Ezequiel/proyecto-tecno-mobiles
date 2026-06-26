package com.undef.superahorro.haronsignorini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.haronsignorini.data.Purchase
import com.undef.superahorro.haronsignorini.data.remote.PurchaseSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PurchaseSyncUiState {
    data object Idle : PurchaseSyncUiState
    data object Loading : PurchaseSyncUiState
    data object Success : PurchaseSyncUiState
    data object Error : PurchaseSyncUiState
}

@HiltViewModel
class PurchaseSyncViewModel @Inject constructor(
    private val repository: PurchaseSyncRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<PurchaseSyncUiState>(PurchaseSyncUiState.Idle)
    val uiState: StateFlow<PurchaseSyncUiState> = _uiState.asStateFlow()

    fun syncPurchase(purchase: Purchase) {
        viewModelScope.launch {
            _uiState.value = PurchaseSyncUiState.Loading
            try {
                repository.syncPurchase(purchase)
                _uiState.value = PurchaseSyncUiState.Success
            } catch (_: Exception) {
                _uiState.value = PurchaseSyncUiState.Error
            }
        }
    }

    fun clearResult() {
        _uiState.value = PurchaseSyncUiState.Idle
    }
}
