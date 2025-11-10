package com.example.demo

// Define una interfaz compartida (en commonMain).
// Cualquier plataforma (Desktop, Web, etc.) debe implementar esto.
interface Platform {
    // Requiere que cada plataforma proporcione una propiedad 'name' (un String).
    val name: String
}

// Declara una función "esperada" (expect).
// Promete que cada plataforma (jvmMain, jsMain) proporcionará
// una implementación "real" (actual) que devuelva un objeto Platform.
expect fun getPlatform(): Platform