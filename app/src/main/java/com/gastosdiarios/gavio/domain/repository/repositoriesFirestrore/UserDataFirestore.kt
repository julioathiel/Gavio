package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.BaseRepository
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : BaseRepository<UserData> {
    private val tag = "UserDataFirestore"

    override suspend fun get(): UserData? {
        return try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return null
            val snapshot = cloudFirestore.getUserData().document(uidUser).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(UserData::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Error al obtener el UserData: ${e.message}")
            null
        }
    }

    override suspend fun delete() {
    }

    override suspend fun createOrUpdate(entity: UserData) {
       try {
           val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return

           cloudFirestore.getUserData().document(uidUser).set(entity.copy(userId = uidUser)).await()
       }catch (e:Exception){
           Log.e(tag,"Error al crear userData: ${e.message}")
       }
    }

    suspend fun updateCurrentMoney(
        valueCurrentMoney: Double,
        valueIsCurrentMoneyIngresos: Boolean
    ) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid ?: return
            val item = hashMapOf<String, Any>(
                "currentMoney" to valueCurrentMoney,
                "isCurrentMoneyIngresos" to valueIsCurrentMoneyIngresos
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
            val item = hashMapOf<String, Any>(
                "selectedDate" to valueSelectedDate,
                "isSelectedDate" to valueIsSelectedDate
            )
            cloudFirestore.getUserData().document(uidUser)
                .update(item).await()
        } catch (e: Exception) {
            Log.e(tag, "Error al actualizar el selectedDate: ${e.message}")
        }
    }

}