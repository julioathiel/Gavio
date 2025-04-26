package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

import android.util.Log
import com.gastosdiarios.gavio.utils.Constants.SHARE_LINK
import com.gastosdiarios.gavio.data.domain.model.ShareDataModel
import com.gastosdiarios.gavio.data.repository.CloudFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SharedLinkFirestore @Inject constructor(private val cloudFirestore: CloudFirestore) {
    val tag = "ShareFirestore"
    fun getFlow(): Flow<ShareDataModel> = callbackFlow {
        val listenerRegistration = cloudFirestore.getShareCollection().document(SHARE_LINK)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tag, "Error al obtener el documento SharedLinkFirestore: $error")
                    close(error)
                    return@addSnapshotListener
                }
                Log.d(tag, "Snapshot received: $snapshot")
                if (snapshot != null && snapshot.exists()) {
                    Log.d(tag, "Document exists")
                    val url = snapshot.toObject(ShareDataModel::class.java)
                    Log.d(tag, "Converted object: $url")
                    if (url != null) {
                        trySend(url)
                    }
                } else {
                    Log.d(tag, "Document does not exist")
                    trySend(ShareDataModel())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}