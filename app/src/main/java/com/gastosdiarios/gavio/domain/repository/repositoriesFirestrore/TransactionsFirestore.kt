package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.gastosdiarios.gavio.domain.repository.ListBaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Repositorio para manejar una lista de TransactionModel
class TransactionsFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ListBaseRepository<TransactionModel> {

    private val tagData = "transactionFirestore"

    override suspend fun get(): List<TransactionModel> {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid
        return try {
            cloudFirestore.getAllTransactionsCollection()
                .document(uidUser!!)
                .collection(COLLECTION_LIST)
                .get()
                .await()
                .documents.mapNotNull { document ->
                    document.toObject(TransactionModel::class.java)
                }
        } catch (e: Exception) {
            Log.d(tagData, "Error al obtener la lista de transacciones: ${e.message}")
            emptyList()
        }
    }

    override suspend fun create(entity: TransactionModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val uidItem = System.currentTimeMillis().hashCode().toString()

            val item = TransactionModel(
                uid = uidItem,
                title = entity.title,
                subTitle = entity.subTitle,
                cash = entity.cash,
                select = entity.select,
                date = entity.date,
                icon = entity.icon,
                index = entity.index
            )
            cloudFirestore.getAllTransactionsCollection().document(uidUser!!)
                .collection(COLLECTION_LIST).document(uidItem).set(item).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al crear lista item en lista de transacciones: ${e.message}")
        }
    }

    override suspend fun update(entity: TransactionModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid

            Log.d(tagData, "update: $entity")
            val item = TransactionModel(
                uid = entity.uid,
                icon = entity.icon,
                title = entity.title,
                subTitle = entity.subTitle,
                cash = entity.cash,
                select = entity.select,
                date = entity.date,
                index = entity.index
            )

            cloudFirestore.getAllTransactionsCollection().document(uidUser!!)
                .collection(COLLECTION_LIST).document(entity.uid!!).set(item).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al actualizar la transacción: ${e.message}")
        }
    }

    override suspend fun delete(entity: TransactionModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            cloudFirestore.getAllTransactionsCollection().document(uidUser!!)
                .collection(COLLECTION_LIST).document(entity.uid!!).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al eliminar la transacción con ID ${entity.uid}: ${e.message}")
        }
    }

    override suspend fun deleteAll() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val collectionRef = cloudFirestore.getAllTransactionsCollection().document(uidUser!!)
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