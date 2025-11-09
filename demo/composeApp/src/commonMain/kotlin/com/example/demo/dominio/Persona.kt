package com.example.demo.dominio

/**
 * Clase abstracta que representa a una persona en el sistema.
 */
abstract class Persona : EntidadBase() {
    abstract val rut: String
    abstract val nombre: String
    abstract val email: String
}