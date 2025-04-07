package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.data.domain.enums.ThemeMode
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.data.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.data.repository.BaseRepository
import com.gastosdiarios.gavio.data.repository.CloudFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserPreferencesFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : BaseRepository<UserPreferences> {
    private val tag = "userPreferencesFirestore"

    override suspend fun getFlow(): Flow<UserPreferences> = callbackFlow {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: run {
                close(Exception("No se pudo obtener el UID del usuario actual"))
                return@callbackFlow
            }
            val listenerRegistration = cloudFirestore.getUserPreferences().document(uidUser)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val item = snapshot.toObject(UserPreferences::class.java)
                        if (item != null) {
                            trySend(item)
                        }
                    } else {
                        trySend(UserPreferences())
                    }
                    }

           awaitClose {listenerRegistration.remove() }
    }

    override suspend fun delete() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserPreferences().document(uidUser).delete().await()
        } catch (e: Exception) {
            Log.d(tag, "error al eliminar el userPreferences: ${e.message}")
        }
    }

    override suspend fun createOrUpdate(entity: UserPreferences) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val item = entity.copy(userId = uidUser)

            cloudFirestore.getUserPreferences().document(uidUser).set(item).await()
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
        }
    }

    fun updateBiometricSecurity(value: Boolean) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserPreferences().document(uidUser)
                .update("biometricSecurity", value)
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
        }
    }

    suspend fun updateThemeMode(value: ThemeMode) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserPreferences().document(uidUser)
                .update("themeMode", value.name).await()
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
        }
    }

    suspend fun updateLimitMonth(value: Int) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserPreferences().document(uidUser)
                .update("limitMonth", value).await()
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
        }
    }

    suspend fun updateHourMinute(hour: Int, minute: Int) {
        try {
            val item = mapOf(
                "hour" to hour,
                "minute" to minute
            )

            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserPreferences().document(uidUser)
                .update(item).await()
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")

        }
    }

}