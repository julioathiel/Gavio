package com.gastosdiarios.gavio.domain.repository

import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.SnackbarManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.TwitterAuthProvider
import javax.inject.Inject

class AuthFirebaseImp @Inject constructor(private val firebaseAuth: FirebaseAuth) : AuthRepository {
    private val tag = "AuthFirebaseImp"
    /*
    Permite autenticar al usuario utilizando un token de Google.
     Este método se utiliza cuando un usuario elige iniciar sesión con su cuenta de Google en tu aplicación.
     */
    override suspend fun signInWithGoogle(tokenId: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        return firebaseAuth.signInWithCredential(credential)
    }

    /*
    Permite autenticar al usuario utilizando un token de Facebook.
     Se usa cuando un usuario decide iniciar sesión con su cuenta de Facebook en tu aplicación.
    */
    override suspend fun signInWithFacebook(token: String): Task<AuthResult> {
        val credential = FacebookAuthProvider.getCredential(token)
        return firebaseAuth.signInWithCredential(credential)
    }

    /*
    Permite autenticar al usuario utilizando tokens de Twitter.
     Se utiliza cuando un usuario elige iniciar sesión con su cuenta de Twitter en tu aplicación.
     */
    override suspend fun signInWithTwitter(token: String, twitterSecret: String): Task<AuthResult> {
        val credential = TwitterAuthProvider.getCredential(token, twitterSecret)
        return firebaseAuth.signInWithCredential(credential)
    }

    /*
    Permite al usuario iniciar sesión de forma anónima, sin la necesidad de crear una cuenta.
     Es útil para permitir a los usuarios acceder a ciertas funciones de la aplicación sin registrarse.
     */
    override suspend fun signInAnonymously(): Task<AuthResult> {
        return firebaseAuth.signInAnonymously()
    }

    /*
    Crea un usuario en Firebase Authentication utilizando una dirección de correo electrónico y una contraseña.
     Este método es para el registro de nuevos usuarios en tu aplicación.
     */
    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }


    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    /*
    Envía un correo electrónico al usuario con un enlace para restablecer su contraseña.
     Es útil cuando un usuario olvida su contraseña y necesita restablecerla.
     */
    override suspend fun sendPasswordResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    /*
    Permite autenticar al usuario utilizando un token personalizado.
     Este método se usa en casos de autenticación personalizada o específica de la aplicación.
     */
    override suspend fun signInWithCustomToken(customToken: String): Task<AuthResult> {
        return firebaseAuth.signInWithCustomToken(customToken)
    }

    //Cierra la sesión del usuario actual, lo desconecta de la aplicación.
    override fun signOut() {
        firebaseAuth.signOut()
    }

    /*
    Elimina la cuenta del usuario actual.
    Es importante tener en cuenta que esta acción es irreversible y eliminará permanentemente la cuenta del usuario
     */
    override suspend fun deleteUser(): Task<Void>? {
        val user = firebaseAuth.currentUser
        return  user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                SnackbarManager.showMessage(R.string.user_deleted)
            }
        }
    }

    // Otros métodos de autenticación personalizada o específica
    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}