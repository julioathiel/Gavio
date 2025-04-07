package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.utils.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.BarDataModel
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

class BarDataFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) :
    ListBaseRepository<BarDataModel> {

    private val tag = "barDataFirestore"

    override suspend fun getFlow(): Flow<List<BarDataModel>> = callbackFlow {

        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: run {
            close()
            return@callbackFlow
        }

        val listener = cloudFirestore.getBarDataCollection()
            .document(uidUser)
            .collection(COLLECTION_LIST)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tag, "lista barra grafica falida", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { snapShot ->
                        snapShot.toObject(BarDataModel::class.java)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun create(entity: BarDataModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = UUID.randomUUID().toString()

            cloudFirestore.getBarDataCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(entity.copy(uid = uidItem))
                .await()
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