package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalGastosModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.BaseRepository
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TotalGastosFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) :
    BaseRepository<TotalGastosModel> {

    private val tagData = "totalGastosFirestore"

    override suspend fun get(): TotalGastosModel? {
        return try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val snapshot = cloudFirestore.getTotalGastosCollection().document(uidUser!!).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(TotalGastosModel::class.java)
            } else {
                null // Return null since the document was just created
            }
        } catch (e: Exception) {
            Log.d(tagData, "error no se pudo obtener el documento totalGastos: ${e.message}")
            null // Return null in case of an error
        }
    }

    override suspend fun createOrUpdate(entity: TotalGastosModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val item = TotalGastosModel(userId = uidUser, totalGastos = entity.totalGastos)

            cloudFirestore.getTotalGastosCollection().document(uidUser!!).set(item).await()
        } catch (e: Exception) {
            Log.d(tagData, "error al actualizar el totalGastos: ${e.message}")
        }
    }

    override suspend fun delete() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            cloudFirestore.getTotalGastosCollection().document(uidUser!!).delete().await()
        } catch (e: Exception) {
            Log.d(tagData, "error al eliminar el totalGastos: ${e.message}")
        }
    }
}