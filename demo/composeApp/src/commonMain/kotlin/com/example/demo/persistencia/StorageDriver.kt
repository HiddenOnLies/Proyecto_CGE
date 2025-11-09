package com.example.demo.persistencia

/**
 * Interfaz de bajo nivel para un sistema de almacenamiento clave-valor (K/V).
 * Trabaja directamente con ByteArrays (datos crudos) para ser agnóstico
 * al formato de serialización (JSON, XML, etc.).
 */
interface IStorageDriver {

    /**
     * Guarda o sobrescribe un array de bytes en una clave específica.
     * Devuelve true si la operación fue exitosa.
     */
    fun put(key: String, data: ByteArray): Boolean

    /**
     * Recupera un array de bytes usando una clave.
     * Devuelve null si la clave no se encuentra.
     */
    fun get(key: String): ByteArray?

    /**
     * Lista todas las claves que comienzan con un prefijo dado.
     */
    fun keys(prefix: String): List<String>

    /**
     * Elimina un par clave-valor usando la clave.
     * Devuelve true si la eliminación fue exitosa (si la clave existía).
     */
    fun remove(key: String): Boolean
}