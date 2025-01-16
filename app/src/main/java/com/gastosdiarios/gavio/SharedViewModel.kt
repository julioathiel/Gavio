package com.gastosdiarios.gavio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val transactionsFirestore: TransactionsFirestore
) : ViewModel() {
    private val _transactionUiState = MutableStateFlow(ListUiState<TransactionModel>())
    val transactionUiState: StateFlow<ListUiState<TransactionModel>> =
        _transactionUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading
        .onStart {
//            _isLoading.value = true // Emitir estado de carga antes de llamar a getAllTransactions()
            getAllTransactions()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)


    private fun getAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                val data: List<TransactionModel> = withContext(Dispatchers.IO) {
                    transactionsFirestore.get()
                }
                _transactionUiState.update { it.copy(items = data) }
                _isLoading.update { false }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error fetching transactions", e)
            }
        }
    }
}