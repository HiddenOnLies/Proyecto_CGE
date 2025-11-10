package com.example.demo

// Clase compartida (en commonMain) que genera un saludo.
class Greeting {
    // Obtiene la plataforma específica (JVM, JS, etc.)
    // llamando a la función expect/actual 'getPlatform()'.
    private val platform = getPlatform()

    // Genera un string de saludo que incluye el nombre de la plataforma.
    fun greet(): String {
        // Ejemplo: "Hello, Java 1.8.0_292!" o "Hello, Web with Kotlin/JS!".
        return "Hello, ${platform.name}!"
    }
}