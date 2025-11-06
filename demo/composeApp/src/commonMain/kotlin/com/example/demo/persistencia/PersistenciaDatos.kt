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
 * Configura el motor de JSON para manejar el polimorfismo de Medidor.
 */
class PersistenciaDatos(val driver: IStorageDriver) {

    // Configura el motor de JSON para que entienda la jerarquía de Medidor
    val json = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            polymorphic(Medidor::class) {
                subclass(MedidorMonofasico::class)
                subclass(MedidorTrifasico::class)
            }
        }
    }

    // Guarda un objeto serializable
    inline fun <reified T> save(key: String, data: T): Boolean {
        val jsonString = json.encodeToString(data)
        return driver.put(key, jsonString.encodeToByteArray())
    }

    // Lee un objeto serializable
    inline fun <reified T> read(key: String): T? {
        return driver.get(key)?.let {
            try {
                json.decodeFromString<T>(it.decodeToString())
            } catch (e: Exception) {
                println("Error al deserializar: ${e.message}")
                null
            }
        }
    }

    // Lista todas las claves que comienzan con un prefijo
    fun listKeys(prefix: String): List<String> = driver.keys(prefix)

    // Elimina un objeto por su clave
    fun delete(key: String): Boolean = driver.remove(key)
}