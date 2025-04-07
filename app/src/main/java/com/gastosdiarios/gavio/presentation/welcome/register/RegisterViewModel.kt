package com.gastosdiarios.gavio.presentation.welcome.register

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.utils.Constants.MIN_PASS_LENGTH
import com.gastosdiarios.gavio.utils.Constants.PASS_PATTERN
import com.gastosdiarios.gavio.utils.Constants.PROVIDER_EMAIL
import com.gastosdiarios.gavio.data.ui_state.LoginUiState
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserModel
import com.gastosdiarios.gavio.data.repository.CloudFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val cloudFirestore: CloudFirestore
) : ViewModel() {

    val tag = "registerViewModel"
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    private val _registerState = mutableStateOf(false)
    val registerState: State<Boolean> = _registerState

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    //creando cuenta de correo electronico
    fun onCreandoCuenta(context: Context, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(user?.displayName)
                    .setPhotoUri(user?.photoUrl)
                    .build()

                user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                    if (profileTask.isSuccessful) {
                        Log.d(tag, "Perfil actualizado correctamente")
                        insertUsersFirestore(
                            UserModel(
                                userId = user.uid,
                                name = user.displayName,
                                email = user.email,
                                password = uiState.value.password,
                                photoUrl = user.photoUrl.toString(),
                                provider = PROVIDER_EMAIL
                            )
                        )
                    } else {
                        // Manejar el error de actualización del perfil
                        Log.e(tag, "Error al actualizar el perfil: ${profileTask.exception}")
                        Toast.makeText(
                            context,
                            "Error al actualizar el perfil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                _registerState.value = true
            } else {
                Log.e(tag, "Error al registrar usuario: ${task.exception}")
                when (val exception = task.exception) {
                    is FirebaseAuthWeakPasswordException -> {
                        Log.e(tag, "Contraseña débil: ${exception.errorCode}")
                        Toast.makeText(context, "Contraseña débil", Toast.LENGTH_SHORT).show()
                    }

                    is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(
                            context,
                            "error de correo electrónico ya en uso",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Log.e(tag, "Error al registrar usuario: ${exception?.message}")
                        Toast.makeText(
                            context,
                            "Error al registrar usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                _registerState.value = false
            }
        }
    }

    private fun String.passwordMatches(repeated: String): Boolean {
        return this == repeated
    }

    //funcion que sirve para registrarse por correo y contraseña
    private fun insertUsersFirestore(user: UserModel) {
        viewModelScope.launch(Dispatchers.IO) {
           cloudFirestore.insertUserToFirestore(user)
        }
    }

    fun enabledButtonRegister(): Boolean {
        //si la direccion de email es valida, que contenga un @, un punto, etc
        return email.isValidEmail() && password.isValidPassword()
    }

    private fun String.isValidEmail(): Boolean {
        return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun String.isValidPassword(): Boolean {
        return this.isNotBlank() &&
                this.length >= MIN_PASS_LENGTH &&
                Pattern.compile(PASS_PATTERN).matcher(this).matches()
    }
}