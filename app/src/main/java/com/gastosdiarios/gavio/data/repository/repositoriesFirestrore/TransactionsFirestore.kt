package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.utils.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.TransactionModel
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

// Repositorio para manejar una lista de TransactionModel
class TransactionsFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ListBaseRepository<TransactionModel> {

    private val tagData = "transactionFirestore"

    override suspend fun getFlow(): Flow<List<TransactionModel>> = callbackFlow {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return@callbackFlow

        val listenerRegistration = cloudFirestore.getAllTransactionsCollection()
            .document(uidUser)
            .collection(COLLECTION_LIST)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { document ->
                        document.toObject(TransactionModel::class.java)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun create(entity: TransactionModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = UUID.randomUUID().toString()
            val item = entity.copy(uid = uidItem)

            cloudFirestore.getAllTransactionsCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(item).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al crear lista item en lista de transacciones: ${e.message}")
        }
    }

    override suspend fun update(entity: TransactionModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getAllTransactionsCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(entity).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al actualizar la transacción: ${e.message}")
        }
    }

    override suspend fun delete(entity: TransactionModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getAllTransactionsCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al eliminar la transacción con ID ${entity.uid}: ${e.message}")
        }
    }

    override suspend fun deleteAll() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val collectionRef = cloudFirestore.getAllTransactionsCollection().document(uidUser)
                .collection(COLLECTION_LIST)

            // Obtener todos los documentos de la colección
            val documents = collectionRef.get().await().documents

            // Eliminar cada documento
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al eliminar todas las transacciones: ${e.message}")
        }
    }
}