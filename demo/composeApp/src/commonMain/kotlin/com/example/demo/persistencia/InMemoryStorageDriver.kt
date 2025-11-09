package com.example.demo.persistencia

/**
 * Implementación de "driver" de almacenamiento que guarda todo en memoria RAM.
 * Los datos se pierden al cerrar la aplicación. Útil para pruebas.
 */
class InMemoryStorageDriver : IStorageDriver {

    // Almacén principal, es un mapa mutable que guarda todo en memoria.
    // La clave es un String y el valor es el dato serializado en ByteArray.
    private val storage = mutableMapOf<String, ByteArray>()

    // Guarda o sobrescribe un dato (array de bytes) asociado a una clave.
    override fun put(key: String, data: ByteArray): Boolean {
        storage[key] = data
        return true // En memoria, esta operación (casi) siempre tiene éxito.
    }

    // Recupera un dato (array de bytes) usando la clave. Devuelve null si no existe.
    override fun get(key: String): ByteArray? = storage[key]

    // Devuelve una lista de todas las claves que comienzan con un prefijo específico.
    override fun keys(prefix: String): List<String> = storage.keys.filter { it.startsWith(prefix) }

    // Elimina un dato por su clave. Devuelve true si la eliminación fue exitosa.
    override fun remove(key: String): Boolean = storage.remove(key) != null
}