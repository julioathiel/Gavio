package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.gastosdiarios.gavio.domain.repository.ListBaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class GastosPorCategoriaFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ListBaseRepository<GastosPorCategoriaModel> {

    private val tagData = "gastosPorCategoriaFirestore"

    override suspend fun get(): List<GastosPorCategoriaModel> {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return emptyList()
        return try {
            cloudFirestore.getGastosPorCategoriaCollection()
                .document(uidUser)
                .collection(COLLECTION_LIST)
                .get()
                .await()
                .documents.mapNotNull { snapShot ->
                    snapShot.toObject(GastosPorCategoriaModel::class.java)
                }
        } catch (e: Exception) {
            Log.d(tagData, "Error al obtener la lista de gastos por categorias: ${e.message}")
            emptyList()
        }
    }

    override suspend fun create(entity: GastosPorCategoriaModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = UUID.randomUUID().toString()

            cloudFirestore.getGastosPorCategoriaCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem)
                .set(
                    entity.copy(uid = uidItem)
                ).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al crear lista item en lista de transacciones: ${e.message}")
        }
    }


    override suspend fun update(entity: GastosPorCategoriaModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getGastosPorCategoriaCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(entity).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al actualizar EL ITEM DE GASTOS POR CATEGORIA: ${e.message}")
        }
    }


    override suspend fun delete(entity: GastosPorCategoriaModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getGastosPorCategoriaCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(
                tagData,
                "Error al eliminar el item de GastosPorCategoria con ID ${entity.uid}: ${e.message}"
            )
        }
    }

    override suspend fun deleteAll() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val snapshot = cloudFirestore.getGastosPorCategoriaCollection().document(uidUser)
                .collection(COLLECTION_LIST)

            val documents = snapshot.get().await().documents

            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al eliminar toda la lista de gastos por categoría: ${e.message}")
        }
    }
}