@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Clase base para todas las entidades del dominio.
 * Proporciona un ID único y marcas de tiempo de creación/actualización.
 */
abstract class EntidadBase {
    abstract val id: String
    abstract val createdAt: Instant
    abstract val updatedAt: Instant
}