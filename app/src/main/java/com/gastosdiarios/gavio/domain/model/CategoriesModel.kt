package com.gastosdiarios.gavio.domain.model

import androidx.annotation.DrawableRes
import com.gastosdiarios.gavio.R

interface CategoriesModel {
    val name: String
    @get:DrawableRes
    var icon: Int
}

data class CategoryIngresos(override val name: String, override var icon: Int) : CategoriesModel
data class CategoryGastos(override val name: String, override var icon: Int) : CategoriesModel
data class CategoryDefault(override val name: String, override var icon: Int) : CategoriesModel
data class CategoryCreate(override val name: String, override var icon: Int) : CategoriesModel

val categoriaDefault = listOf(
    CategoryDefault("Elige una categoría", R.drawable.ic_empty)
)

// categoria predeterminada para ingresos
val defaultCategoriesIngresosList = mutableListOf(
    CategoryIngresos("Sueldo", R.drawable.ic_sueldo),
    CategoryIngresos("Deposito", R.drawable.ic_banco),
    CategoryIngresos("Ahorros", R.drawable.ic_ahorro)
)

// categoria predeterminada para gastos
val defaultCategoriesGastosList = mutableListOf(
    CategoryGastos("Alquiler / Hipoteca", R.drawable.ic_alquiler_hipoteca),
    CategoryGastos("Transporte", R.drawable.ic_transporte),
    CategoryGastos("Hogar", R.drawable.ic_home),
    CategoryGastos("Trabajo", R.drawable.ic_trabajo),
    CategoryGastos("Combustible", R.drawable.ic_combustible),
    CategoryGastos("Supermercado", R.drawable.ic_supermercado),
    CategoryGastos("Alimentos", R.drawable.ic_tienda_comestibles),
    CategoryGastos("Tarjeta", R.drawable.ic_tarjeta_credito),
    CategoryGastos("Shopping", R.drawable.ic_shopping),
    CategoryGastos("Farmacia", R.drawable.ic_local_pharmacy),
    CategoryGastos("Automóvil", R.drawable.ic_automovil),
    CategoryGastos("Salud", R.drawable.ic_doctor),
    CategoryGastos("Entretenimiento", R.drawable.ic_entretenimiento),
    CategoryGastos("Mascota", R.drawable.ic_mascota),
    CategoryGastos("Vacaciones", R.drawable.ic_vacaciones),
    CategoryGastos("Vestimenta", R.drawable.ic_vestimenta),
    CategoryGastos("Servicios", R.drawable.ic_servicios),
    CategoryGastos("Educación", R.drawable.ic_school),
    CategoryGastos("Telefonía", R.drawable.ic_call)
)

val userCategoriesIngresosList = mutableListOf<CategoryGastos>()
val userCategoriesGastosList = mutableListOf<CategoryGastos>()

//no poner nombres sino no poder encontrar icono seleccionado al editar
//categoria predeterminada para crear categorias
val categoriesGastosNuevos = listOf(
    CategoryCreate("", R.drawable.ic_pescar),
    CategoryCreate("", R.drawable.ic_natacion),
    CategoryCreate("", R.drawable.ic_futbol),
    CategoryCreate("", R.drawable.ic_local_cafe),
    CategoryCreate("", R.drawable.ic_local_pizza),
    CategoryCreate("", R.drawable.ic_helado),
    CategoryCreate("", R.drawable.ic_pastel),
    CategoryCreate("", R.drawable.ic_tarjeta_credito),
    CategoryCreate("", R.drawable.ic_regalos),
    CategoryCreate("", R.drawable.ic_avion),
    CategoryCreate("", R.drawable.ic_bicicleta),
    CategoryCreate("", R.drawable.ic_bar_vino),
    CategoryCreate("", R.drawable.ic_ahorro),
    CategoryCreate("", R.drawable.ic_cigarrillos),
    CategoryCreate("", R.drawable.ic_comida_rapida),
    CategoryCreate("", R.drawable.ic_hotel),
    CategoryCreate("", R.drawable.ic_restaurante),
    CategoryCreate("", R.drawable.ic_alquiler_hipoteca),
    CategoryCreate("", R.drawable.ic_local_car_wash),
    CategoryCreate("", R.drawable.ic_transporte),
    CategoryCreate("", R.drawable.ic_auto_electrico),
    CategoryCreate("", R.drawable.ic_home),
    CategoryCreate("", R.drawable.ic_trabajo),
    CategoryCreate("", R.drawable.ic_ev_station),
    CategoryCreate("", R.drawable.ic_combustible_gas),
    CategoryCreate("", R.drawable.ic_combustible),
    CategoryCreate("", R.drawable.ic_supermercado),
    CategoryCreate("", R.drawable.ic_tienda_comestibles),
    CategoryCreate("", R.drawable.ic_shopping),
    CategoryCreate("", R.drawable.ic_hospital),
    CategoryCreate("", R.drawable.ic_local_pharmacy),
    CategoryCreate("", R.drawable.ic_automovil),
    CategoryCreate("", R.drawable.ic_doctor),
    CategoryCreate("", R.drawable.ic_entretenimiento),
    CategoryCreate("", R.drawable.ic_mascota),
    CategoryCreate("", R.drawable.ic_construccion),
    CategoryCreate("", R.drawable.ic_vacaciones),
    CategoryCreate("", R.drawable.ic_vestimenta),
    CategoryCreate("", R.drawable.ic_servicios),
    CategoryCreate("", R.drawable.ic_school),
    CategoryCreate("", R.drawable.ic_otros),
    CategoryCreate("", R.drawable.ic_electrical_services),
    CategoryCreate("", R.drawable.ic_call),
    CategoryCreate("", R.drawable.ic_estadio),
    CategoryCreate("", R.drawable.ic_apartamento),
    CategoryCreate("", R.drawable.ic_asistencia_anciano),
    CategoryCreate("", R.drawable.ic_cerveza),
    CategoryCreate("", R.drawable.ic_barra_filled),
    CategoryCreate("", R.drawable.ic_lavadero_ropa),
    CategoryCreate("", R.drawable.ic_corazon),
    CategoryCreate("", R.drawable.ic_folder),
    CategoryCreate("",R.drawable.ic_viajes),
    CategoryCreate("",R.drawable.ic_fitness_center),
)
