package com.gastosdiarios.gavio.data.repository.repositoriesFirestrore

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
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val url = snapshot.toObject(ShareDataModel::class.java)
                    if (url != null) {
                        trySend(url)
                    }
                } else {
                    trySend(ShareDataModel())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}