package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
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
             val uidUser = authFirebaseImp.getCurrentUser()?.uid
             val snapshot =
                 cloudFirestore.getUserPreferences().document(uidUser!!).get().await()
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
             val uidUser = authFirebaseImp.getCurrentUser()?.uid
             cloudFirestore.getUserPreferences().document(uidUser!!).delete().await()
         } catch (e: Exception) {
             Log.d(tag, "error al eliminar el userPreferences: ${e.message}")
         }
     }

     override suspend fun createOrUpdate(entity: UserPreferences) {
         try {
             val uidUser = authFirebaseImp.getCurrentUser()?.uid

             val item = entity.copy(userId = uidUser)
             Log.d(tag, "createOrUpdate: $item")
             cloudFirestore.getUserPreferences().document(uidUser!!).set(item).await()
         } catch (e: Exception) {
             Log.d(tag, "error al actualizar el userPreferences: ${e.message}")
         }
     }

 }