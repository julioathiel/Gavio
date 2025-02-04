package com.gastosdiarios.gavio.data.ui_state

import com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserData

sealed class UserDataUiState {
    data object Loading : UserDataUiState()
    data class Success(val userData: UserData) : UserDataUiState()
    data class Error(val exception: Throwable) : UserDataUiState()
    data object Empty : UserDataUiState()
}