package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.domain.model.modelFirebase.DateModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.BaseRepository
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DateFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : BaseRepository<DateModel> {

    private val tagData = "dateFirestore"

    override suspend fun get(): DateModel? {
        return try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val snapshot = cloudFirestore.getDateCollection().document(uidUser!!).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(DateModel::class.java)
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(tagData, "error no se pudo obtener el documento date: ${e.message}")
            null // Return null in case of an error
        }
    }

    override suspend fun createOrUpdate(entity: DateModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val item = DateModel(userId = uidUser, date = entity.date, isSelected = entity.isSelected)

            cloudFirestore.getDateCollection().document(uidUser!!).set(item).await()
        } catch (e: Exception) {
            Log.d(tagData, "error al actualizar el date: ${e.message}")
        }
    }

    override suspend fun delete() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            cloudFirestore.getDateCollection().document(uidUser!!).delete().await()
        } catch (e: Exception) {
            Log.d(tagData, "error al eliminar el date: ${e.message}")
        }
    }

}