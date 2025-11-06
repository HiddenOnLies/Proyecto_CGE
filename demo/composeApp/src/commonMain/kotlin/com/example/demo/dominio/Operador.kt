@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Operador(
    // Propiedades de EntidadBase
    override val id: String,
    @Serializable(with = InstantSerializer::class)
    override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    override val updatedAt: Instant,

    // Propiedades de Persona
    override val rut: String,
    override val nombre: String,
    override val email: String,

    // Propiedades específicas de Operador
    val perfil: String

) : Persona()