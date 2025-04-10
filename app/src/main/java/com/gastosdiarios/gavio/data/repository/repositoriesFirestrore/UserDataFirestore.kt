package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.data.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.data.repository.BaseRepository
import com.gastosdiarios.gavio.data.repository.CloudFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : BaseRepository<UserData> {
    private val tag = "UserDataFirestore"

    override suspend fun getFlow(): Flow<UserData> = callbackFlow {
        val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return@callbackFlow

        val listenerRegistration = cloudFirestore.getUserData().document(uidUser)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val item = snapshot.toObject(UserData::class.java)
                    if (item != null) {
                        trySend(item)
                    }
                } else {
                    trySend(UserData())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun delete() {}

    override suspend fun createOrUpdate(entity: UserData) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserData().document(uidUser).set(entity.copy(userId = uidUser))
                .await()
        } catch (e: Exception) {
            Log.e(tag, "Error al crear userData: ${e.message}")
        }
    }

    suspend fun updateCurrentMoney(
        valueCurrentMoney: Double,
        currentMoneyIsZero: Boolean
    ) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val item = hashMapOf<String, Any>(
                "currentMoney" to valueCurrentMoney,
                "currentMoneyIsZero" to currentMoneyIsZero
            )
            cloudFirestore.getUserData().document(uidUser).update(item).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al actualizar el currentMoney: ${e.message}")
        }
    }

    suspend fun updateTotalGastos(valueTotalGastos: Double) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserData().document(uidUser).update("totalGastos", valueTotalGastos)
                .await()
        } catch (e: Exception) {
            Log.e(tag, "Error al actualizar el totalGastos: ${e.message}")
        }
    }

    suspend fun updateTotalIngresos(valueTotalIngresos: Double) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserData().document(uidUser)
                .update("totalIngresos", valueTotalIngresos).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al actualizar el totalIngresos: ${e.message}")

        }
    }

    suspend fun updateSelectedDate(valueSelectedDate: String, valueIsSelectedDate: Boolean) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return

            val item = mapOf(
                "selectedDate" to valueSelectedDate,
                "isSelectedDate" to valueIsSelectedDate
            )
            cloudFirestore.getUserData().document(uidUser).update(item).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al actualizar el selectedDate: ${e.message}")
        }
    }

    suspend fun deleteCurrentMoneyData() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return

            val updates = mapOf(
                "currentMoney" to FieldValue.delete(),
                "isCurrentMoneyIngresos" to FieldValue.delete()
            )
            cloudFirestore.getUserData().document(uidUser).update(updates).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al eliminar currentMoney data: ${e.message}")
        }
    }

    suspend fun deleteSelectedDateData() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return

            val updates = mapOf(
                "selectedDate" to FieldValue.delete(),
                "isSelectedDate" to FieldValue.delete()
            )
            cloudFirestore.getUserData().document(uidUser).update(updates).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al eliminar selectedDate data: ${e.message}")
        }
    }

    suspend fun deleteTotalIngresos() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserData().document(uidUser)
                .update("totalIngresos", FieldValue.delete()).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al eliminar totalIngresos:${e.message}")
        }
    }

    suspend fun deleteTotalGastos() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            cloudFirestore.getUserData().document(uidUser)
                .update("totalGastos", FieldValue.delete()).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al eliminar totalGastos: ${e.message}")
        }
    }

}