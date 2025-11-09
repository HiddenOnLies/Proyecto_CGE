@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable // Permite convertir la clase a JSON u otros formatos
/**
 * Representa la lectura mensual de consumo eléctrico registrada por un medidor.
 * Contiene el identificador del medidor, el periodo (año y mes)
 * y la cantidad de energía consumida en kWh.
 **/
data class LecturaConsumo(
    // Propiedades de EntidadBase
    override val id: String,
    @Serializable(with = InstantSerializer::class)
    override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    override val updatedAt: Instant,

    // Propiedades específicas de LecturaConsumo
    val idMedidor: String,
    val anio: Int,
    val mes: Int,
    val kwhLeidos: Double
) : EntidadBase()