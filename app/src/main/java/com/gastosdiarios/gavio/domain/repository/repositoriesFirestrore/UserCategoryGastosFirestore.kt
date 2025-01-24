package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.gastosdiarios.gavio.domain.repository.ListBaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class UserCategoryGastosFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ListBaseRepository<UserCreateCategoryModel> {

    private val tagData = "userCategoryGastosFirestore"

    override suspend fun get(): List<UserCreateCategoryModel> {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return emptyList()
        return try {
            cloudFirestore.getUserCategoryGastosCollection()
                .document(uidUser)
                .collection(COLLECTION_LIST)
                .get()
                .await()
                .documents.mapNotNull { snapShot ->
                    snapShot.toObject(UserCreateCategoryModel::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun create(entity: UserCreateCategoryModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = UUID.randomUUID().toString()

//            val item = UserCreateCategoryModel(
//                uid = uidItem,
//                categoryName = entity.categoryName,
//                categoryIcon = entity.categoryIcon,
//                categoryType = entity.categoryType
//            )

            cloudFirestore.getUserCategoryGastosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(
                    entity.copy(uid = uidItem)

                ).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al crear lista item de categorias de gastos: ${e.message}")
        }
    }

    override suspend fun update(entity: UserCreateCategoryModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            val item = UserCreateCategoryModel(
                uid = entity.uid,
                categoryName = entity.categoryName,
                categoryIcon = entity.categoryIcon,
                categoryType = entity.categoryType
            )

            cloudFirestore.getUserCategoryGastosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).set(entity).await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al actualizar el item de categorias de gastos: ${e.message}")
        }
    }

    override suspend fun delete(entity: UserCreateCategoryModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val uidItem = entity.uid ?: return

            cloudFirestore.getUserCategoryGastosCollection().document(uidUser)
                .collection(COLLECTION_LIST).document(uidItem).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.i(
                tagData,
                "Error al eliminar el item de categorias de gastos con ID ${entity.uid}: ${e.message}"
            )
        }
    }

    override suspend fun deleteAll() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return

            val snapshot = cloudFirestore.getUserCategoryGastosCollection().document(uidUser)
                .collection(COLLECTION_LIST)

            val documents = snapshot.get().await()
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.i(tagData, "Error al eliminar todas la lista de categorias de gastos: ${e.message}")
        }
    }
}