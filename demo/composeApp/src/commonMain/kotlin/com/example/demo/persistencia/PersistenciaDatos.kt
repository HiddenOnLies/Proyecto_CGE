package com.example.demo.persistencia

import com.example.demo.dominio.Medidor
import com.example.demo.dominio.MedidorMonofasico
import com.example.demo.dominio.MedidorTrifasico
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Clase central que gestiona la serialización y el acceso al storage driver.
 * Traduce objetos (ej: Cliente, Boleta) a JSON y viceversa.
 * Es la implementación de "PersistenciaDatos" requerida en la pauta.
 */
class PersistenciaDatos(
    // Dependencia del "driver" (ej: en memoria, en archivo) que guarda los bytes.
    val driver: IStorageDriver
) {

    // Configura el motor de serialización JSON.
    val json = Json {
        // Formatea el JSON para que sea legible (bueno para depurar).
        prettyPrint = true

        // Define el módulo de serialización.
        serializersModule = SerializersModule {
            // Configura el POLIMORFISMO para la clase base Medidor.
            // Esto es crucial para que Json sepa cómo guardar y leer
            // las subclases MedidorMonofasico y MedidorTrifasico.
            polymorphic(Medidor::class) {
                subclass(MedidorMonofasico::class)
                subclass(MedidorTrifasico::class)
            }
        }
    }

    // Guarda cualquier objeto (T) convirtiéndolo a un string JSON.
    // 'inline fun <reified T>' permite que la función sepa el tipo de T en tiempo de ejecución.
    inline fun <reified T> save(key: String, data: T): Boolean {
        // Convierte el objeto (ej: un Cliente) a un string JSON.
        val jsonString = json.encodeToString(data)

        // Llama al driver para guardar el string como un array de bytes.
        return driver.put(key, jsonString.encodeToByteArray())
    }

    // Lee un objeto (T) desde el almacén.
    inline fun <reified T> read(key: String): T? {
        // 1. Pide los bytes al driver. Si no hay nada (null), devuelve null.
        return driver.get(key)?.let { byteArray ->
            // 2. Si hay bytes, intenta convertirlos de vuelta a un objeto T.
            try {
                // Convierte el array de bytes a String, y luego el String JSON al objeto.
                json.decodeFromString<T>(byteArray.decodeToString())
            } catch (e: Exception) {
                // Si falla (ej. JSON corrupto, clase movida), imprime un error y devuelve null.
                println("Error al deserializar $key: ${e.message}")
                null
            }
        }
    }

    // Devuelve todas las claves que coinciden con un prefijo.
    // Delega directamente al driver.
    fun listKeys(prefix: String): List<String> = driver.keys(prefix)

    // Elimina un objeto por su clave. Delega directamente al driver.
    fun delete(key: String): Boolean = driver.remove(key)
}