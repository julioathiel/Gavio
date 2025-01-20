package com.gastosdiarios.gavio.domain.repository

import android.util.Log
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_BAR_DATA
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_CURRENT_MONEY
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_DATE
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_GASTOS_POR_CATEGORIA
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_GASTOS_PROGRAMADOS
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_LIST
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_SHARE
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_TOTAL_GASTOS
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_TOTAL_INGRESOS
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_TRANSACTIONS
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_USERS
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_USER_CATEGORY_GASTOS
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_USER_CATEGORY_INGRESOS
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_USER_DATA
import com.gastosdiarios.gavio.data.constants.Constants.COLLECTION_USER_PREFERENCES
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.CurrentMoneyModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.DateModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalGastosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalIngresosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import javax.inject.Inject

open class CloudFirestore @Inject constructor(
    private val collections: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val tag = "cloudFirestore"

    suspend fun insertUserToFirestore(user: UserModel) {
        try {
            val userId = auth.currentUser?.uid
            val existingUser = getUsersCollection().document(userId!!).get().await()
            Log.d(tag, "existingUser: $existingUser")

            if (existingUser.exists()) {
                Log.i(tag, "El usuario con correo electronico ${user.email} ya existe")
                // No cargar nada, mantener la información actual
                return
            } else {
                Log.i(tag, "El usuario con correo electronico ${user.email} no existe")
                initializeUserData(user)
                Log.i(tag, "Usuario con correo electronico ${user.email} insertado correctamente")

            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, "Error en insertUserToFirestore: ${e.message}")
        }
    }

    private suspend fun initializeUserData(user: UserModel) {
        //operacion creada para que funcione de forma atomicas las operaciones de escritura
        try {
            val mesActual = DateUtils.currentMonth()
            val uidItem = UUID.randomUUID().toString()

            collections.runTransaction { transaction ->
                val date = DateFormat.getDateInstance().format(Date())
                val userRef = getUsersCollection().document(user.userId!!)

                transaction.set(
                    userRef,
                    UserModel(
                        userId = user.userId,
                        name = user.name,
                        email = user.email,
                        password = user.password,
                        photoUrl = user.photoUrl,
                        date = date,
                        provider = user.provider
                    )
                )

                val userDataRef = getUserData().document(user.userId)
                transaction.set(
                    userDataRef,
                    UserData(
                        userId = user.userId,
                        totalGastos = 0.0,
                        totalIngresos = 0.0,
                        currentMoney = 0.0,
                        isCurrentMoneyIngresos = true,
                        selectedDate = "",
                        isSelectedDate = true
                    )
                )

                val currentMoneyRef = getCurrentMoneyCollection().document(user.userId)
                transaction.set(
                    currentMoneyRef,
                    CurrentMoneyModel(userId = user.userId, money = 0.0, checked = true)
                )

                val dateRef = getDateCollection().document(user.userId)
                transaction.set(dateRef, DateModel(userId = user.userId, isSelected = true))


                val totalGastosRef = getTotalGastosCollection().document(user.userId)
                transaction.set(
                    totalGastosRef,
                    TotalGastosModel(userId = user.userId, totalGastos = 0.0)
                )

                val totalIngresosRef = getTotalIngresosCollection().document(user.userId)
                transaction.set(
                    totalIngresosRef,
                    TotalIngresosModel(userId = user.userId, totalIngresos = 0.0)
                )
                val barDataRef = getBarDataCollection()
                    .document(user.userId)
                    .collection(COLLECTION_LIST)
                    .document(uidItem)
                transaction.set(
                    barDataRef,
                    BarDataModel(uid = uidItem, value = 0f, month = mesActual, money = "0")
                )

                val userPreferencesRef = getUserPreferences().document(user.userId)
                transaction.set(
                    userPreferencesRef,
                    UserPreferences(
                        biometricSecurity = false,
                        dateMax = 31,
                        hour = 21,
                        minute = 0,
                        themeMode = ModeDarkThemeEnum.MODE_AUTO
                    )
                )

                null
            }.await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, "Error en initializeUserData al inicializar datos del usuario: ${e.message}")
        }
    }

    suspend fun deleteUserByEmail(email: String) {
        try {
            Log.i(tag, "email: $email")
            // Busca el documento que tenga el correo electrónico proporcionado
            Log.i(tag, "Iniciando consulta...")
            val userId = auth.currentUser?.uid
            val userRef = getUsersCollection().document(userId!!) // Usa userId directamente
            val existingUserSnapshot = userRef.get().await()

            Log.i(tag, "existingUserSnapshot: $existingUserSnapshot")
            // Verifica si el documento existe
            if (existingUserSnapshot.exists()) {
                // Elimina documentos relacionados en otras colecciones
                deleteUserDataDocument(userId)
                Log.i(tag, "Usuario con correo electronico $email eliminado correctamente")
            } else {
                Log.i(tag, "No se encontro un usuario con ese correo electrónico ")
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, "Error al eliminar los documentos del usuario: ${e.message}")
        }
    }

    private suspend fun deleteUserDataDocument(userId: String) {
        val errores = mutableListOf<String>()
        try {
            val listasTransaccionesSnapshot = getAllTransactionsCollection().document(userId)
                .collection(COLLECTION_LIST).get().await()
            for (document in listasTransaccionesSnapshot.documents) {
                document.reference.delete().await()
            }
            // Eliminando lista creada por usuario de gastos por categoria
            val listaGastosPorCategoriaSnapshot =
                getGastosPorCategoriaCollection().document(userId)
                    .collection(COLLECTION_LIST).get().await()
            for (document in listaGastosPorCategoriaSnapshot.documents) {
                document.reference.delete().await()
            }
            // Eliminando lista del grafico de barras
            val listBarDataSnapshot = getBarDataCollection().document(userId).collection(
                COLLECTION_LIST
            ).get().await()
            for (document in listBarDataSnapshot.documents) {
                document.reference.delete().await()
            }
            // Eliminando lista creada por usuario de ingresos y gastos por categoria
            val listaIngresosPorCategoriaSnapshot =
                getUserCategoryIngresosCollection().document(userId)
                    .collection(COLLECTION_LIST).get().await()
            for (document in listaIngresosPorCategoriaSnapshot.documents) {
                document.reference.delete().await()
            }
            //eliminando lista gastos programados
            val listaGastosProgramadosSnapshot =
                getAllGastosProgramadosCollection().document(userId).collection(
                    COLLECTION_LIST
                ).get().await()
            for (document in listaGastosProgramadosSnapshot.documents) {
                document.reference.delete().await()
            }

            collections.runTransaction { transaction ->
                transaction.delete(getUserData().document(userId))
                transaction.delete(getUsersCollection().document(userId))
                transaction.delete(getBarDataCollection().document(userId))
                transaction.delete(getCurrentMoneyCollection().document(userId))
                transaction.delete(getDateCollection().document(userId))
                transaction.delete(getGastosPorCategoriaCollection().document(userId))
                transaction.delete(getTotalGastosCollection().document(userId))
                transaction.delete(getTotalIngresosCollection().document(userId))
                transaction.delete(getUserPreferences().document(userId))
                transaction.delete(getAllTransactionsCollection().document(userId))
                transaction.delete(getAllGastosProgramadosCollection().document(userId))
                transaction.delete(getUserCategoryGastosCollection().document(userId))
                transaction.delete(getUserCategoryIngresosCollection().document(userId))
            }.await()
        } catch (e: FirebaseFirestoreException) {
            errores.add("Error en deleteUserDataDocument al eliminar datos del usuario: ${e.message}")
        }
    }

    private fun getUsersCollection(): CollectionReference = collections.collection(COLLECTION_USERS)
    fun getBarDataCollection(): CollectionReference = collections.collection(COLLECTION_BAR_DATA)
    fun getCurrentMoneyCollection(): CollectionReference =
        collections.collection(COLLECTION_CURRENT_MONEY)

    fun getDateCollection(): CollectionReference = collections.collection(COLLECTION_DATE)
    fun getGastosPorCategoriaCollection(): CollectionReference =
        collections.collection(COLLECTION_GASTOS_POR_CATEGORIA)

    fun getTotalGastosCollection(): CollectionReference =
        collections.collection(COLLECTION_TOTAL_GASTOS)

    fun getTotalIngresosCollection(): CollectionReference =
        collections.collection(COLLECTION_TOTAL_INGRESOS)

    fun getAllTransactionsCollection(): CollectionReference =
        collections.collection(COLLECTION_TRANSACTIONS)

    fun getAllGastosProgramadosCollection(): CollectionReference =
        collections.collection(COLLECTION_GASTOS_PROGRAMADOS)

    fun getUserCategoryGastosCollection(): CollectionReference =
        collections.collection(COLLECTION_USER_CATEGORY_GASTOS)

    fun getUserCategoryIngresosCollection(): CollectionReference =
        collections.collection(COLLECTION_USER_CATEGORY_INGRESOS)

    fun getShareCollection(): CollectionReference = collections.collection(COLLECTION_SHARE)
    fun getUserPreferences(): CollectionReference =
        collections.collection(COLLECTION_USER_PREFERENCES)

    fun getUserData(): CollectionReference = collections.collection(COLLECTION_USER_DATA)
}