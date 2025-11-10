package com.example.demo

// Implementación de la interfaz Platform para la plataforma JVM (Desktop).
class JVMPlatform: Platform {
    // Define el nombre de la plataforma, obteniendo la versión de Java del sistema.
    override val name: String = "Java ${System.getProperty("java.version")}"
}

// Implementación "real" (actual) de getPlatform para la JVM.
// Devuelve una instancia de JVMPlatform.
actual fun getPlatform(): Platform = JVMPlatform()