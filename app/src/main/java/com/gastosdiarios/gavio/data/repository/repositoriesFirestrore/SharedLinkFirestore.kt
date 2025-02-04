package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.utils.Constants.SHARE_LINK
import com.gastosdiarios.gavio.data.domain.model.ShareDataModel
import com.gastosdiarios.gavio.data.repository.CloudFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SharedLinkFirestore @Inject constructor(private val cloudFirestore: CloudFirestore) {
    val tag = "ShareFirestore"
    suspend fun get(): ShareDataModel {
        return try {
            val document = cloudFirestore.getShareCollection().document(SHARE_LINK).get().await()
            ShareDataModel(document.getString("link"))
        } catch (e: Exception) {
            Log.e(tag, "Error al obtener el documento", e)
            ShareDataModel("")
        }
    }
}