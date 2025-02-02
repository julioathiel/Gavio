package com.gastosdiarios.gavio.data.commons

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SnackbarManager {
    private val _messages = MutableStateFlow<SnackbarMessage?>(null)
    val messages: StateFlow<SnackbarMessage?> = _messages.asStateFlow()

    fun showMessage(message: String) {
        _messages.value = SnackbarMessage.StringSnackbar(message)
    }

    private fun clearSnackbarState() {
        _messages.value = null
    }

   suspend fun durationSnackbar(duration: Long) {
        delay(duration)
        clearSnackbarState()
    }
}

sealed class SnackbarMessage {
    class StringSnackbar(val message: String) : SnackbarMessage()
}