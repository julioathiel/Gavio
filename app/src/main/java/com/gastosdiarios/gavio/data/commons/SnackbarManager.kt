package com.gastosdiarios.gavio.data.commons

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SnackbarManager {
    private val _messages = MutableStateFlow<SnackbarMessage?>(null)
    val messages: StateFlow<SnackbarMessage?> = _messages.asStateFlow()

    fun showMessage(message: String) {
        _messages.value = SnackbarMessage.StringSnackbar(message)
    }

    fun clearSnackbarState() {
        _messages.value = null
    }
}

sealed class SnackbarMessage {
    class StringSnackbar(val message: String) : SnackbarMessage()
}