package com.gastosdiarios.gavio.domain.model

data class DataList<T>(
        val selectedItems: List<T> = emptyList(),
        val expandedItem: T? = null,
        val selectionMode: Boolean = false,
        val isCreate: Boolean = false,
        val isDelete: Boolean = false,
        val updateItem: Boolean = false
    )