@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@SerialName("MedidorMonofasico")
/**
 * Representa un medidor de tipo monofásico.
 * Hereda de la clase Medidor y agrega la propiedad de potencia máxima permitida.
 */
data class MedidorMonofasico(
    // Propiedades heredadas de EntidadBase
    override val id: String,
    @Serializable(with = InstantSerializer::class)
    override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    override val updatedAt: Instant,

    // Propiedades heredadas de Medidor
    override val codigo: String,
    override val direccionSuministro: String,
    override val activo: Boolean,

    // Propiedad específica de MedidorMonofasico
    val potenciaMaxKw: Double
) : Medidor() {

    override fun tipo(): String = "Monofásico" // Retorna el tipo de medidor
}