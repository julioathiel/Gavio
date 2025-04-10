package com.gastosdiarios.gavio.presentation.configuration.user_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.data.repository.CloudFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: AuthFirebaseImp,
    private val cloudFirestore: CloudFirestore,
    private val auth: FirebaseAuth
) :
    ViewModel() {
    private val tag = "userProfileViewModel"

    fun deleteUser() {
        //elimina la cuenta
        viewModelScope.launch(Dispatchers.IO){
            try {
                val email = auth.currentUser?.email
                // 1. Eliminar datos de Firestore
                cloudFirestore.deleteUserByEmail(email!!)
                // 2. Eliminar la cuenta de autenticación
                repository.deleteUser()
            } catch (e: Exception) {
                // Manejar errores, por ejemplo, mostrar un mensaje al usuario
                Log.e(tag, "Error al eliminar usuario: ${e.message}")
            }
        }
    }

    fun signOut() {
        //cierra sesion
        repository.signOut()
    }

    fun getCurrenthUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }

}