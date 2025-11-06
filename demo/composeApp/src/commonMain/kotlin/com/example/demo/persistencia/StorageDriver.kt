package com.example.demo.persistencia

/**
 * Interfaz de bajo nivel para un sistema de almacenamiento clave-valor.
 * Trabaja directamente con ByteArrays para ser agnóstico al formato de los datos.
 */
interface IStorageDriver {
    fun put(key: String, data: ByteArray): Boolean
    fun get(key: String): ByteArray?
    fun keys(prefix: String): List<String>
    fun remove(key: String): Boolean
}