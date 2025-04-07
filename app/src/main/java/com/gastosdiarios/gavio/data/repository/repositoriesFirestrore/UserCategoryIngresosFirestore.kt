package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.utils.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel
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

class UserCategoryIngresosFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ListBaseRepository<UserCreateCategoryModel> {

    private val tag = "userCategoryIngresosFirestore"

    override suspend fun getFlow(): Flow<List<UserCreateCategoryModel>> = callbackFlow {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: run {
            close()
            return@callbackFlow
        }

        val listener = cloudFirestore.getUserCategoryIngresosCollection()
            .document(uidUser)
            .collection(COLLECTION_LIST)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tag, "lista fallida", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { snapShot ->
                        snapShot.toObject(UserCreateCategoryModel::class.java)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun create(entity: UserCreateCategoryModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = UUID.randomUUID().toString()

            cloudFirestore.getUserCategoryIngresosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(
                    entity.copy(uid = uidItem)
                ).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tag, "Error al crear lista item de categorias de Ingresos: ${e.message}")
        }
    }

    override suspend fun update(entity: UserCreateCategoryModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getUserCategoryIngresosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(entity).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tag, "Error al actualizar el item de categorias de Ingresos: ${e.message}")
        }
    }

    override suspend fun delete(entity: UserCreateCategoryModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getUserCategoryIngresosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(
                tag,
                "Error al eliminar el item de categorias de Ingresos con ID ${entity.uid}: ${e.message}"
            )
        }
    }

    override suspend fun deleteAll() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val snapshot = cloudFirestore.getUserCategoryIngresosCollection().document(uidUser!!)
                .collection(COLLECTION_LIST)

            val documents = snapshot.get().await()
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.i(
                tag,
                "Error al eliminar todas la lista de categorias de Ingresos: ${e.message}"
            )
        }
    }
}