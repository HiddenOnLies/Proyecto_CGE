package com.example.demo.dominio

/**
 * Interfaz que define el comportamiento de cálculo para cualquier tipo de tarifa.
 */
interface Tarifa {
    fun nombre(): String
    fun calcular(kwh: Double): TarifaDetalle
}