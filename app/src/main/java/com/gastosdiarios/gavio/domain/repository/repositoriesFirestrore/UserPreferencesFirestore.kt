package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.BaseRepository
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserPreferencesFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : BaseRepository<UserPreferences> {
    private val tag = "userPreferencesFirestore"

    override suspend fun get(): UserPreferences? {
        return try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid  ?: return null
            val snapshot = cloudFirestore.getUserPreferences().document(uidUser).get().await()

            if (snapshot.exists()) {
                snapshot.toObject(UserPreferences::class.java)
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(tag, "error no se pudo obtener el documento userPreferences: ${e.message}")
            null
        }
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

            if (uidUser != null) {
                cloudFirestore.getUserPreferences().document(uidUser)
                    .update("biometricSecurity", value)
            }
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
        }
    }

    suspend fun updateThemeMode(value: ModeDarkThemeEnum) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserPreferences().document(uidUser)
                .update("themeMode", value.name).await()
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
        }
    }

    suspend fun updateDateMax(value: Int) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserPreferences().document(uidUser)
                .update("dateMax", value).await()
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
        }
    }

    suspend fun updateHourMinute(hour: Int, minute: Int) {
        try {
            val item = hashMapOf<String, Any>(
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