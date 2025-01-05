package com.gastosdiarios.gavio.presentation.create_gastos_default

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CreateGastosDefaultViewModel@Inject constructor(
    private val dbm: DataBaseManager
) : ViewModel()  {
    private val _transactionUiState = MutableStateFlow(ListUiState<TransactionModel>())
    val transactionUiState: StateFlow<ListUiState<TransactionModel>> = _transactionUiState.asStateFlow()
    init {
        getAllTransactions()
    }

    private fun getAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactionUiState.update { it.copy(isLoading = true) }
            val data: List<TransactionModel> = dbm.getTransactions()
            _transactionUiState.update {
                it.copy(items = data, isLoading = false)
            }
        }
    }
}