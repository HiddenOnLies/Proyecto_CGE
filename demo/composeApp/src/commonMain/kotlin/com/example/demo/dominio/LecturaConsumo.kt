@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
/**
 * Representa la lectura de consumo de un medidor en un mes y año específicos.
 * Hereda las propiedades de EntidadBase.
 */
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