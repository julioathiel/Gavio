package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.utils.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.data.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.data.repository.CloudFirestore
import com.gastosdiarios.gavio.data.repository.ListBaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class GastosPorCategoriaFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ListBaseRepository<GastosPorCategoriaModel> {

    private val tagData = "gastosPorCategoriaFirestore"

    override suspend fun getFlow(): Flow<List<GastosPorCategoriaModel>> = callbackFlow {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: run {
            close()
            return@callbackFlow
        }
        val listener = cloudFirestore.getGastosPorCategoriaCollection()
            .document(uidUser)
            .collection(COLLECTION_LIST)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tagData, "lista gastos por categoria fallida", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { snapShot ->
                        snapShot.toObject(GastosPorCategoriaModel::class.java)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
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