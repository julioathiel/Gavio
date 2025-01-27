package com.gastosdiarios.gavio.domain.repository

import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.ShareDataModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
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
    private val barDataRepo: BarDataFirestore,
    private val transactionsRepo: TransactionsFirestore,
    private val gastosPorCategoriaRepo: GastosPorCategoriaFirestore,
    private val userCategoryIngresosRepo: UserCategoryIngresosFirestore,
    private val userCategoryGastosRepo: UserCategoryGastosFirestore,
    private val gastosProgramadosRepo: GastosProgramadosFirestore,
    private val userPreferencesRepo: UserPreferencesFirestore,
    private val userDataRepo: UserDataFirestore,
    private val sharedLinkRepo: SharedLinkFirestore
) {
    //----------------------------------------------//

    suspend fun getSharedLink(): ShareDataModel = sharedLinkRepo.get()
    suspend fun getUserData(): UserData? = userDataRepo.get()
    suspend fun getUserPreferences(): UserPreferences? = userPreferencesRepo.get()
    suspend fun getBarDataGraph(): List<BarDataModel> = barDataRepo.get()
    suspend fun getTransactions(): List<TransactionModel> = transactionsRepo.get()
    suspend fun getGastosPorCategoria(): List<GastosPorCategoriaModel> =
        gastosPorCategoriaRepo.get()

    suspend fun getUserCategoryGastos(): List<UserCreateCategoryModel> =
        userCategoryGastosRepo.get()

    suspend fun getUserCategoryIngresos(): List<UserCreateCategoryModel> =
        userCategoryIngresosRepo.get()

    suspend fun getGastosProgramados(): List<GastosProgramadosModel> = gastosProgramadosRepo.get()
    //----------------------------------------------//

    //FUNCION PARA LA PANTALLA DE TRANSACTIONS
    suspend fun deleteAllScreenTransactions() {
        userDataRepo.deleteCurrentMoneyData()
        userDataRepo.deleteTotalGastos()
        userDataRepo.deleteTotalIngresos()
        transactionsRepo.deleteAll()
        gastosPorCategoriaRepo.deleteAll()
    }


    //----------------------------------------------//

    //  *... funciones opcionales para eliminar las tablas completas de la base de datos *  //

    suspend fun deleteAllGraphBar() = barDataRepo.deleteAll()
    suspend fun deleteAllUserCreaCatGastos() = userCategoryGastosRepo.deleteAll()
    suspend fun deleteAllUserCreaCatIngresos() = userCategoryIngresosRepo.deleteAll()

    //----------------------------------------------//

    //  suspend fun deleteCurrentMoney() = currentMoneyRepo.delete()
    suspend fun deleteAllTransactions() = transactionsRepo.deleteAll()
    suspend fun deleteAllGastosPorCategory() = gastosPorCategoriaRepo.deleteAll()
    suspend fun deleteAllGastosProgramados() = gastosProgramadosRepo.deleteAll()

    suspend fun deleteTransaction(item: TransactionModel) {
        transactionsRepo.delete(item)
    }

    suspend fun resetAllApp() {
        userDataRepo.deleteSelectedDateData()
        deleteAllTransactions()
        deleteAllGastosPorCategory()
        userDataRepo.updateTotalGastos(0.0)
        userDataRepo.updateTotalIngresos(0.0)
        userDataRepo.updateCurrentMoney(0.0, true)
    }

    suspend fun updateAllApp() {
        deleteAllTransactions()
        deleteAllGastosPorCategory()
        updateTotalIngresos(0.0)
        updateTotalGastos(0.0)
        updateCurrentMoney(0.0, true)
    }


    //-----------------------USER DATA Repo-----------------------//
    suspend fun updateCurrentMoney(currentMoney: Double, currentMoneyIsZero: Boolean) {
        userDataRepo.updateCurrentMoney(currentMoney, currentMoneyIsZero)
    }

    suspend fun updateTotalGastos(totalGastos: Double) {
        userDataRepo.updateTotalGastos(totalGastos)
    }

    suspend fun updateTotalIngresos(totalIngresos: Double) {
        userDataRepo.updateTotalIngresos(totalIngresos)
    }

    suspend fun updateSelectedDate(selectedDate: String, isSelectedDate: Boolean) {
        userDataRepo.updateSelectedDate(selectedDate, isSelectedDate)
    }
    //-------------------------------------------------------------------------//


    //-----------------------USER PREFERENCES Repo-----------------------//
    suspend fun updateLimitMonth(limitMonth: Int) {
        userPreferencesRepo.updateLimitMonth(limitMonth)
    }

    suspend fun updateBiometricSecurity(value: Boolean) {
        userPreferencesRepo.updateBiometricSecurity(value)
    }

    suspend fun updateThemeMode(value: ThemeMode) {
        userPreferencesRepo.updateThemeMode(value)
    }

    suspend fun updateHourMinute(hour: Int, minute: Int) {
        userPreferencesRepo.updateHourMinute(hour, minute)
    }

    //-------------------------------------------------------------------------//

}