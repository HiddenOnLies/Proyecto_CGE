package com.example.demo.persistencia

/**
 * Implementación "real" (actual) de la función `expect`
 * para la plataforma JVM (Desktop).
 *
 * Esta función se ejecuta cuando el código compartido (shared)
 * llama a `createStorageDriver()` en la app de escritorio.
 */
actual fun createStorageDriver(): IStorageDriver {
    // Devuelve una instancia del driver que guarda en archivos,
    // ya que estamos en un entorno de escritorio (JVM) que tiene sistema de archivos.
    return FileStorageDriver()
}