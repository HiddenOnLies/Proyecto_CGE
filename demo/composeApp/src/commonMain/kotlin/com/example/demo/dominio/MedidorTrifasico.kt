@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@SerialName("MedidorTrifasico")
/**
 * Representa un medidor de tipo trifásico.
 * Hereda de la clase abstracta Medidor.
 */
data class MedidorTrifasico(
    // Propiedades de EntidadBase
    override val id: String,
    @Serializable(with = InstantSerializer::class)
    override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    override val updatedAt: Instant,

    // Propiedades de Medidor
    override val codigo: String,
    override val direccionSuministro: String,
    override val activo: Boolean,

    // Propiedades específicas de MedidorTrifasico
    val potenciaMaxKw: Double,
    val factorPotencia: Double
) : Medidor() {

    override fun tipo(): String = "Trifásico"
}