package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.domain.model.modelFirebase.CurrentMoneyModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.DateModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.BaseRepository
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CurrentMoneyFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : BaseRepository<CurrentMoneyModel> {

    private val tag = "currentMoneyFirestore"

    override suspend fun get(): CurrentMoneyModel? {
        return try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val snapshot =
                cloudFirestore.getCurrentMoneyCollection().document(uidUser!!).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(CurrentMoneyModel::class.java)
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(tag, "error no se pudo obtener el documento currentMoney: ${e.message}")
            null
        }
    }

    override suspend fun createOrUpdate(entity: CurrentMoneyModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val item = entity.copy(userId = uidUser)
            Log.d(tag, "createOrUpdate: $item")
            cloudFirestore.getCurrentMoneyCollection().document(uidUser!!).set(item).await()
        } catch (e: Exception) {
            Log.d(tag, "error al actualizar el currentMoney: ${e.message}")
        }
    }

    override suspend fun delete() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            cloudFirestore.getCurrentMoneyCollection().document(uidUser!!).delete().await()
        } catch (e: Exception) {
            Log.d(tag, "error al eliminar el currentMoney: ${e.message}")
        }
    }
}