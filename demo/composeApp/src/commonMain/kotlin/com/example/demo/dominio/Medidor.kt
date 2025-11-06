package com.example.demo.dominio

import kotlinx.serialization.Serializable

@Serializable
/**
 * Clase base abstracta para los diferentes tipos de medidores de consumo eléctrico.
 * Hereda las propiedades de EntidadBase.
 */
abstract class Medidor : EntidadBase() {
    abstract val codigo: String
    abstract val direccionSuministro: String
    abstract val activo: Boolean

    /**
     * Devuelve una cadena que representa el tipo de medidor (ej. "Monofásico").
     */
    abstract fun tipo(): String
}