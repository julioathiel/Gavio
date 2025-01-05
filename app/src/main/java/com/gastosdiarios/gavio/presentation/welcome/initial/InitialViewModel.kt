package com.gastosdiarios.gavio.presentation.welcome.initial


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.App.ConnectivityStatus.credentialManager
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.constants.Constants.PROVIDER_GOOGLE
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserModel
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state

    fun eventHandler(e: EventHandlerlLogin) {
        when (e) {
            is EventHandlerlLogin.ContinuarConPhone -> iniciarConPhone()
            is EventHandlerlLogin.ContinuarConGoogle -> {}
            is EventHandlerlLogin.ContinuarConFacebok -> iniciarConFacebook()
            else -> {}
        }
    }

    private fun iniciarConPhone() {}
    private fun iniciarConFacebook() {}

    //sirve para autentificarse con correo y contraseña
    private fun insertUsersFirestore(user: UserModel) {
        viewModelScope.launch {
            cloudFirestore.insertUserToFirestore(user)
        }
    }

    //nueva funcion para iniciar sesion con google
    fun signInWithGoogle(
        context: Context,
        onResult: (Boolean) -> Unit
    ) {

        viewModelScope.launch {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)//permite seleccionar cualquier cuenta de google
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            insertUsersFirestore(
                                UserModel(
                                    userId = task.result.user!!.uid,
                                    name = task.result.user!!.displayName!!,
                                    email = task.result.user!!.email!!,
                                    password = "",
                                    photoUrl = task.result.user!!.photoUrl.toString(),
                                    date = DateFormat.getDateInstance().format(Date()),
                                    provider = PROVIDER_GOOGLE,
                                )
                            )
                            // Autenticación exitosa, puedes redirigir al usuario a la siguiente pantalla
                            onResult(true)
                        } else {
                            // Manejar el caso en que la autenticación falla
                            Log.e("InitialScreen", "Error de autenticación: ${task.exception}")
                            Toast.makeText(
                                context,
                                "Error al iniciar sesión con Google",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } catch (e: GetCredentialException) {
                Toast.makeText(
                    context,
                    "Error obteniendo credenciales de Google, ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}