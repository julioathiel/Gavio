package com.gastosdiarios.gavio.domain.repository

import com.gastosdiarios.gavio.domain.model.ShareDataModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.CurrentMoneyModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.DateModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalGastosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalIngresosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.CurrentMoneyFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.DateFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosProgramadosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.SharedLinkFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalGastosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalIngresosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryGastosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryIngresosFirestore
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class DataBaseManager @Inject constructor(
    private val dateFirestore: DateFirestore,
    private val barDataFirestore: BarDataFirestore,
    private val totalGastosFirestore: TotalGastosFirestore,
    private val transactionsFirestore: TransactionsFirestore,
    private val currentMoneyFirestore: CurrentMoneyFirestore,
    private val totalIngresosFirestore: TotalIngresosFirestore,
    private val gastosPorCategoriaFirestore: GastosPorCategoriaFirestore,
    private val userCategoryIngresosFirestore: UserCategoryIngresosFirestore,
    private val userCategoryGastosFirestore: UserCategoryGastosFirestore,
    private val gastosProgramadosFirestore: GastosProgramadosFirestore,
    private val sharedLinkFirestore: SharedLinkFirestore
) {
    //----------------------------------------------//

    suspend fun getDate(): DateModel? = dateFirestore.get()
    suspend fun getSharedLink(): ShareDataModel? = sharedLinkFirestore.get()
    suspend fun getTotalGastos(): TotalGastosModel? = totalGastosFirestore.get()
    suspend fun getCurrentMoney(): CurrentMoneyModel? = currentMoneyFirestore.get()
    suspend fun getTotalIngresos(): TotalIngresosModel? = totalIngresosFirestore.get()
    suspend fun getBarDataGraph(): List<BarDataModel> = barDataFirestore.get()
    suspend fun getTransactions(): List<TransactionModel> = transactionsFirestore.get()
    suspend fun getGastosPorCategoria(): List<GastosPorCategoriaModel> = gastosPorCategoriaFirestore.get()
    suspend fun getUserCategoryGastos(): List<UserCreateCategoryModel> = userCategoryGastosFirestore.get()
    suspend fun getUserCategoryIngresos(): List<UserCreateCategoryModel> = userCategoryIngresosFirestore.get()
    suspend fun getGastosProgramados(): List<GastosProgramadosModel> = gastosProgramadosFirestore.get()
    //----------------------------------------------//

    //FUNCION PARA LA PANTALLA DE TRANSACTIONS
    suspend fun deleteAllScreenTransactions() {
        totalGastosFirestore.delete()
        currentMoneyFirestore.delete()
        totalIngresosFirestore.delete()
        transactionsFirestore.deleteAll()
        gastosPorCategoriaFirestore.deleteAll()
    }


    //----------------------------------------------//

    //  *... funciones opcionales para eliminar las tablas completas de la base de datos *  //

    suspend fun deleteAllGraphBar() = barDataFirestore.deleteAll()
    suspend fun deleteAllUserCreaCatGastos() = userCategoryGastosFirestore.deleteAll()
    suspend fun deleteAllUserCreaCatIngresos() = userCategoryIngresosFirestore.deleteAll()

    //----------------------------------------------//

    suspend fun deleteCurrentMoney() = currentMoneyFirestore.delete()
    private suspend fun deleteAllTransactions() = transactionsFirestore.deleteAll()
    private suspend fun deleteAllGastosPorCategory() = gastosPorCategoriaFirestore.deleteAll()


    suspend fun deleteTransaction(item: TransactionModel) {
        transactionsFirestore.delete(item)
    }

    suspend fun resetAllApp() {
        dateFirestore.delete()
        deleteAllTransactions()
        deleteAllGastosPorCategory()
        totalGastosFirestore.createOrUpdate(TotalGastosModel(totalGastos = 0.0))
        totalIngresosFirestore.createOrUpdate(TotalIngresosModel(totalIngresos = 0.0))
        currentMoneyFirestore.createOrUpdate(CurrentMoneyModel(money = 0.0, checked = true))
    }
}