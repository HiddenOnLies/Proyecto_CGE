package com.example.demo.dominio

import kotlinx.serialization.Serializable

/**
 * Clase base abstracta para los diferentes tipos de medidores de consumo eléctrico.
 * Hereda las propiedades de EntidadBase.
 */
@Serializable // Permite convertir la clase a JSON u otros formatos
abstract class Medidor : EntidadBase() {
    abstract val codigo: String
    abstract val direccionSuministro: String
    abstract val activo: Boolean
    abstract fun tipo(): String
}