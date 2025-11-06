package com.example.demo.persistencia

/**
 * Implementación de IStorageDriver que guarda los datos en un mapa mutable en memoria.
 * Los datos se perderán cuando la aplicación se cierre. Ideal para pruebas y ejemplos.
 */
class InMemoryStorageDriver : IStorageDriver {
    private val storage = mutableMapOf<String, ByteArray>()

    override fun put(key: String, data: ByteArray): Boolean {
        storage[key] = data
        return true
    }

    override fun get(key: String): ByteArray? = storage[key]

    override fun keys(prefix: String): List<String> = storage.keys.filter { it.startsWith(prefix) }

    override fun remove(key: String): Boolean = storage.remove(key) != null
}