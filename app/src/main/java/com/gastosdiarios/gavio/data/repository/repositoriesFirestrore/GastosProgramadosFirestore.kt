package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.utils.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosProgramadosModel
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

class GastosProgramadosFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ListBaseRepository<GastosProgramadosModel> {
    private val tagData = "gastosProgramadosFirestore"

    override suspend fun getFlow(): Flow<List<GastosProgramadosModel>> = callbackFlow {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: run {
            close(Exception("No se pudo obtener el UID del usuario actual"))
            return@callbackFlow
        }
        val listener = cloudFirestore.getAllGastosProgramadosCollection()
            .document(uidUser)
            .collection(COLLECTION_LIST)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { document ->
                        document.toObject(GastosProgramadosModel::class.java)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun deleteAll() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val collectionRef =
                cloudFirestore.getAllGastosProgramadosCollection().document(uidUser!!)
                    .collection(COLLECTION_LIST)

            // Obtener todos los documentos de la colección
            val documents = collectionRef.get().await().documents

            // Eliminar cada documento
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al eliminar todas los gastos programados: ${e.message}")
        }
    }

    override suspend fun delete(entity: GastosProgramadosModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getAllGastosProgramadosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al eliminar la transacción con ID ${entity.uid}: ${e.message}")
        }
    }

    override suspend fun update(entity: GastosProgramadosModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getAllGastosProgramadosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(entity).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al actualizar lista de gastos programados: ${e.message}")
        }
    }

    override suspend fun create(entity: GastosProgramadosModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = UUID.randomUUID().toString()

            cloudFirestore.getAllGastosProgramadosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(
                    entity.copy(uid = uidItem)
                ).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al crear en lista de gastos programados: ${e.message}")
        }
    }
}