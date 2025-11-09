@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Clase abstracta base para todas las entidades del dominio.
 * Define propiedades comunes como el identificador único y las marcas de tiempo
 * de creación y última actualización.
 **/
abstract class EntidadBase {
    abstract val id: String
    abstract val createdAt: Instant
    abstract val updatedAt: Instant
}