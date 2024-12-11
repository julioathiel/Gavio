package com.gastosdiarios.gavio.domain.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(tokenId: String): Task<AuthResult>
    suspend fun signInWithFacebook(token: String): Task<AuthResult>
    suspend fun signInWithTwitter(token: String, secret: String): Task<AuthResult>
    suspend fun signInAnonymously(): Task<AuthResult>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Task<AuthResult>
    suspend fun sendPasswordResetEmail(email: String): Task<Void>
    suspend fun signInWithCustomToken(customToken: String): Task<AuthResult>
    fun signOut()
    suspend fun deleteUser(): Task<Void>?
    fun getCurrentUser(): FirebaseUser?
}