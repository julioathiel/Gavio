package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalIngresosModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.BaseRepository
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TotalIngresosFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : BaseRepository<TotalIngresosModel> {

    private val tagData = "totalIngresosFirestore"

    override suspend fun get(): TotalIngresosModel? {
        return try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val snapshot = cloudFirestore.getTotalIngresosCollection().document(uidUser!!).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(TotalIngresosModel::class.java)
            } else {
                null // Return null since the document was just created
            }
        } catch (e: java.lang.Exception) {
            Log.d(tagData, "error no se pudo obtener el documento totalIngresos: ${e.message}")
            null // Return null in case of an error
        }
    }

    override suspend fun createOrUpdate(entity: TotalIngresosModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val item = TotalIngresosModel(userId = uidUser, totalIngresos = entity.totalIngresos)

            cloudFirestore.getTotalIngresosCollection().document(uidUser!!).set(item).await()
        } catch (e: Exception) {
            Log.d(tagData, "error al actualizar el totalIngresos: ${e.message}")
        }
    }

    override suspend fun delete() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            cloudFirestore.getTotalIngresosCollection().document(uidUser!!).delete().await()
        }catch (e: Exception){
            Log.d(tagData, "error al eliminar el totalIngresos: ${e.message}")
        }
    }
}