package com.gastosdiarios.gavio.data.repository

import com.gastosdiarios.gavio.data.domain.enums.ThemeMode
import com.gastosdiarios.gavio.data.domain.model.ShareDataModel
import com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.GastosProgramadosFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.SharedLinkFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserCategoryGastosFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserCategoryIngresosFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserDataFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserPreferencesFirestore
import kotlinx.coroutines.flow.Flow
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

    suspend fun getSharedLink(): Flow<ShareDataModel> = sharedLinkRepo.getFlow()
    suspend fun getUserData(): Flow<UserData> = userDataRepo.getFlow()
    suspend fun getUserPreferences(): Flow<UserPreferences> = userPreferencesRepo.getFlow()
    suspend fun getBarDataGraph(): Flow<List<BarDataModel>> = barDataRepo.getFlow()
    suspend fun getTransactions(): Flow<List<TransactionModel>> = transactionsRepo.getFlow()
    suspend fun getGastosPorCategoria(): Flow<List<GastosPorCategoriaModel>> = gastosPorCategoriaRepo.getFlow()
    suspend fun getUserCategoryGastos(): Flow<List<UserCreateCategoryModel>> = userCategoryGastosRepo.getFlow()
    suspend fun getUserCategoryIngresos(): Flow<List<UserCreateCategoryModel>> = userCategoryIngresosRepo.getFlow()
    suspend fun getGastosProgramados(): Flow<List<GastosProgramadosModel>> = gastosProgramadosRepo.getFlow()
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