package com.gastosdiarios.gavio.data.events_handlers

import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel

sealed class OnActionsMovimientos {
    data class DeleteItem(val lisTransactions: List<TransactionModel>, val item: TransactionModel) : OnActionsMovimientos()
    data class EditItem(
        val title: String,
        val nuevoValor: String,
        val description: String,
        val item: TransactionModel
    ) : OnActionsMovimientos()
}