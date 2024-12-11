package com.gastosdiarios.gavio.data.commons

import androidx.annotation.StringRes

sealed class SnackbarMessage {
    class StringSnackbar(val message: String) : SnackbarMessage()
    class ResourceSnackbar(@StringRes val message: Int) : SnackbarMessage()

    companion object {

    }
}