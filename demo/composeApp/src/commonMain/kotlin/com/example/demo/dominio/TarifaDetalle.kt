package com.example.demo.dominio

import kotlinx.serialization.Serializable

@Serializable
// Clase que representa el detalle del cálculo de una tarifa.
data class TarifaDetalle(
    val kwh: Double,
    val subtotal: Double,
    val cargos: Double,
    val iva: Double,
    val total: Double
)