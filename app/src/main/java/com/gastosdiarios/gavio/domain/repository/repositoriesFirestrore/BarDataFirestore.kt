package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.gastosdiarios.gavio.domain.repository.ListBaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class BarDataFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) :
    ListBaseRepository<BarDataModel> {

    private val tag = "barDataFirestore"

    override suspend fun get(): List<BarDataModel> {
        return try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return emptyList()

            cloudFirestore.getBarDataCollection()
                .document(uidUser)
                .collection(COLLECTION_LIST)
                .get()
                .await()
                .documents.mapNotNull { snapShot ->
                    snapShot.toObject(BarDataModel::class.java)
                }
        } catch (e: Exception) {
            Log.d(tag, "Error en get de BarDataFirestore: ${e.message}")
            emptyList()
        }
    }

    override suspend fun create(entity: BarDataModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = UUID.randomUUID().toString()

//            val item = BarDataModel(
//                uid = uidItem,
//                value = entity.value,
//                month = entity.month,
//                money = entity.money,
//            )

            cloudFirestore.getBarDataCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(entity.copy(uid= uidItem)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tag, "Error al crear item en lista de bardata: ${e.message}")
        }
    }

    override suspend fun update(entity: BarDataModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getBarDataCollection().document(uidUser)
                .collection(COLLECTION_LIST)
                .document(uidItem)
                .set(entity)
                .await()

        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, "Error al actualizar: ${e.message}")
        }
    }

    override suspend fun delete(entity: BarDataModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getBarDataCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, "Error al eliminar: ${e.message}")
        }
    }

    override suspend fun deleteAll() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return

            val collectionRef = cloudFirestore.getBarDataCollection()
                .document(uidUser)
                .collection(COLLECTION_LIST)
            // Obtener todos los documentos de la colección
            val documents = collectionRef.get().await().documents

            // Eliminar cada documento
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.i(tag, "Error al eliminar todos los documentos: ${e.message}")
        }
    }
}