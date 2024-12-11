package com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.domain.model.modelFirebase.CurrentMoneyModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.BaseRepository
import com.gastosdiarios.gavio.domain.repository.CloudFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CurrentMoneyFirestore @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val authFirebaseImp: AuthFirebaseImp,
    private val firebaseAuth: FirebaseAuth,
    private val userId: String?
) : BaseRepository<CurrentMoneyModel> {

    private val tagData = "currentMoneyFirestore"

    override suspend fun get(): CurrentMoneyModel? {
        return try {
            if(firebaseAuth.currentUser != null){
                // Verifica si el usuario está autenticado
                // El usuario está autenticado, accede al documento
                val snapshot = cloudFirestore.getCurrentMoneyCollection().document(userId!!).get().await()
                if (snapshot.exists()) {
                    snapshot.toObject(CurrentMoneyModel::class.java)
                } else {
                    null
                }
            }else{
                // El usuario no está autenticado, retorna null
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(tagData, "error no se pudo obtener el documento currentMoney: ${e.message}")
            null
        }
    }

    override suspend fun createOrUpdate(entity: CurrentMoneyModel) {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            val item = CurrentMoneyModel(userId = uidUser, money = entity.money, checked = entity.checked)

            cloudFirestore.getCurrentMoneyCollection().document(uidUser!!).set(item).await()
        } catch (e: Exception) {
            Log.d(tagData, "error al actualizar el currentMoney: ${e.message}")
        }
    }

    override suspend fun delete() {
        try {
            val uidUser = authFirebaseImp.getCurrentUser()?.uid
            cloudFirestore.getCurrentMoneyCollection().document(uidUser!!).delete().await()
        }catch (e: Exception){
            Log.d(tagData, "error al eliminar el currentMoney: ${e.message}")
        }
    }
}