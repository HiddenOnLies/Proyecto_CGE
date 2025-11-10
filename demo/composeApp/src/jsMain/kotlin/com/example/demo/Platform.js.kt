package com.example.demo

// Implementación de la interfaz Platform para la plataforma JavaScript (Web).
class JsPlatform: Platform {
    // Define el nombre de la plataforma como "Web with Kotlin/JS".
    override val name: String = "Web with Kotlin/JS"
}

// Implementación "real" (actual) de getPlatform para JS.
// Devuelve una instancia de JsPlatform.
actual fun getPlatform(): Platform = JsPlatform()