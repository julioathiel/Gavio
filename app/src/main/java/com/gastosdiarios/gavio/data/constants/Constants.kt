package com.gastosdiarios.gavio.data.constants

object Constants {
    const val PROVIDER_EMAIL = "email"
    const val PROVIDER_GOOGLE = "google"
    const val PROVIDER_FACEBOOK = "facebook"

    const val MY_CHANNEL_ID = "gastos_diarios"
    const val NAME_NOTIFICATION_CANAL = "gastos_diarios"
    const val DESCRIPTION_TEXT_NOTIFICATION_CANAL = "Canal de notificaciones"
    const val NOTIFICATION_ID = 5
    const val HORAS_PREDEFINIDAS = 21
    const val MINUTOS_PREDEFINIDOS = 0
    const val LIMIT_MONTH = 12
    const val MIN_PASS_LENGTH = 6
    const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

    const val RC_SIGN_IN = 100 // Request code para google inicio

    const val COLLECTION_USERS = "users"
    const val COLLECTION_BAR_DATA = "bar_data"
    const val COLLECTION_CURRENT_MONEY = "current_money"
    const val COLLECTION_DATE = "date"
    const val COLLECTION_GASTOS_POR_CATEGORIA = "gastos_por_categoria"
    const val COLLECTION_TOTAL_GASTOS = "total_gastos"
    const val COLLECTION_TOTAL_INGRESOS = "total_ingresos"
    const val COLLECTION_TRANSACTIONS = "transactions"
    const val COLLECTION_GASTOS_PROGRAMADOS = "gastos_programados"
    const val COLLECTION_USER_CATEGORY_GASTOS = "user_category_gastos"
    const val COLLECTION_USER_CATEGORY_INGRESOS = "user_category_ingresos"
    const val COLLECTION_LIST= "list"
    const val COLLECTION_SHARE = "share_app"
    const val COLLECTION_USER_PREFERENCES = "user_preferences"
    const val SHARE_LINK = "share_link"
}