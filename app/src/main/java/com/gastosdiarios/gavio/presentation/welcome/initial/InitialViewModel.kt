package com.gastosdiarios.gavio.presentation.welcome.initial


import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.constants.Constants.PROVIDER_GOOGLE
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserModel
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager
) : ViewModel() {
    private val _state = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state

    fun eventHandler(e: EventHandlerlLogin) {
        when (e) {
            is EventHandlerlLogin.ContinuarConPhone -> iniciarConPhone()
            is EventHandlerlLogin.ContinuarConGoogle -> {}
            is EventHandlerlLogin.ContinuarConFacebok -> iniciarConFacebook()
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

//                firebaseAuth.signInWithCredential(firebaseCredential)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            insertUsersFirestore(
//                                UserModel(
//                                    userId = task.result.user?.uid,
//                                    name = task.result.user?.displayName,
//                                    email = task.result.user?.email,
//                                    password = "",
//                                    photoUrl = task.result.user?.photoUrl.toString(),
//                                    date = DateFormat.getDateInstance().format(Date()),
//                                    provider = PROVIDER_GOOGLE,
//                                )
//                            )
//                            // Autenticación exitosa, puedes redirigir al usuario a la siguiente pantalla
//                            onResult(true)
//                        } else {
//                            // Manejar el caso en que la autenticación falla
//                            Log.e("InitialScreen", "Error de autenticación: ${task.exception}")
//                            Toast.makeText(
//                                context,
//                                "Error al iniciar sesión con Google",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
            } catch (e: GetCredentialException) {
                when (e.type) {
                    else -> {
                        // Manejar otros tipos de error
                        Log.e("InitialScreen", "Error obteniendo credenciales de Google: ${e.message}", e)
                        Toast.makeText(context, "Error obteniendo credenciales de Google, ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    suspend fun handleSignIn(result: GetCredentialResponse, onResult: (Boolean) -> Unit) {
// 1
        val credential = result.credential
        // GoogleIdToken credential

        when (credential) {
            is CustomCredential -> {
// 2
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
// 3
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
// 4
                        val googleIdToken = googleIdTokenCredential.idToken

// 5
                        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
// 6
                        val user = Firebase.auth.signInWithCredential(authCredential).await().user
// 7
                        user?.run {
                            Log.d("TAGg", "User ID: $uid")
                            insertUsersFirestore(
                                UserModel(
                                    userId = user.uid,
                                    name = user.displayName,
                                    email = user.email,
                                    password = "",
                                    photoUrl = user.photoUrl.toString(),
                                    date = DateFormat.getDateInstance().format(Date()),
                                    provider = PROVIDER_GOOGLE,
                                )
                            )
                            onResult(true)
//                            updateUiState {
//                                AuthUiState(
//                                    alreadySignUp = true,
//                                    isLoading = false,
//                                    user = user,
//                                    isAnonymous = user.isAnonymous,
//                                    isAuthenticated = true,
//                                    authState = if (user.isAnonymous) AuthState.Authenticated else AuthState.SignedIn
//                                )
//                            }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("TAGg", "Received an invalid google id token response", e)
                    } catch (e: Exception) {
                        Log.e("TAGg", "Unexpected error")
                    }
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e("TAGg", "Unexpected type of credential")
            }
        }
    }
}