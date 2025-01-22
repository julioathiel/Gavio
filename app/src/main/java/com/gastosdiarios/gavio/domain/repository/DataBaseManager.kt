package com.gastosdiarios.gavio.domain.repository

import com.gastosdiarios.gavio.domain.model.ShareDataModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosProgramadosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.SharedLinkFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryGastosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryIngresosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserPreferencesFirestore
import javax.inject.Inject

class DataBaseManager @Inject constructor(
    private val barDataFirestore: BarDataFirestore,
    private val transactionsFirestore: TransactionsFirestore,

    private val gastosPorCategoriaFirestore: GastosPorCategoriaFirestore,
    private val userCategoryIngresosFirestore: UserCategoryIngresosFirestore,
    private val userCategoryGastosFirestore: UserCategoryGastosFirestore,
    private val gastosProgramadosFirestore: GastosProgramadosFirestore,
    private val userPreferencesFirestore: UserPreferencesFirestore,
    private val userDataFirestore: UserDataFirestore,
    private val sharedLinkFirestore: SharedLinkFirestore
) {
    //----------------------------------------------//

    suspend fun getSharedLink(): ShareDataModel? = sharedLinkFirestore.get()
    suspend fun getBarDataGraph(): List<BarDataModel> = barDataFirestore.get()
    suspend fun getTransactions(): List<TransactionModel> = transactionsFirestore.get()
    suspend fun getGastosPorCategoria(): List<GastosPorCategoriaModel> = gastosPorCategoriaFirestore.get()
    suspend fun getUserCategoryGastos(): List<UserCreateCategoryModel> = userCategoryGastosFirestore.get()
    suspend fun getUserCategoryIngresos(): List<UserCreateCategoryModel> = userCategoryIngresosFirestore.get()
    suspend fun getGastosProgramados(): List<GastosProgramadosModel> = gastosProgramadosFirestore.get()
    //----------------------------------------------//

    //FUNCION PARA LA PANTALLA DE TRANSACTIONS
    suspend fun deleteAllScreenTransactions() {
        userDataFirestore.deleteCurrentMoneyData()
        userDataFirestore.deleteTotalGastos()
        userDataFirestore.deleteTotalIngresos()
        transactionsFirestore.deleteAll()
        gastosPorCategoriaFirestore.deleteAll()
    }


    //----------------------------------------------//

    //  *... funciones opcionales para eliminar las tablas completas de la base de datos *  //

    suspend fun deleteAllGraphBar() = barDataFirestore.deleteAll()
    suspend fun deleteAllUserCreaCatGastos() = userCategoryGastosFirestore.deleteAll()
    suspend fun deleteAllUserCreaCatIngresos() = userCategoryIngresosFirestore.deleteAll()

    //----------------------------------------------//

  //  suspend fun deleteCurrentMoney() = currentMoneyFirestore.delete()
    private suspend fun deleteAllTransactions() = transactionsFirestore.deleteAll()
    private suspend fun deleteAllGastosPorCategory() = gastosPorCategoriaFirestore.deleteAll()


    suspend fun deleteTransaction(item: TransactionModel) {
        transactionsFirestore.delete(item)
    }

    suspend fun resetAllApp() {
        userDataFirestore.deleteSelectedDateData()
        deleteAllTransactions()
        deleteAllGastosPorCategory()
        userDataFirestore.updateTotalGastos(0.0)
        userDataFirestore.updateTotalIngresos(0.0)
        userDataFirestore.updateCurrentMoney(0.0, true)
    }
}