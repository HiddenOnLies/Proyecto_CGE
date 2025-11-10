package com.example.demo.persistencia

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Implementación de "driver" de almacenamiento que guarda cada dato
 * como un archivo individual en el disco local.
 */
class FileStorageDriver(directory: String = ".cge_gestion_data") : IStorageDriver {

    // Define la ruta base para guardar los archivos (ej: /home/usuario/.cge_gestion_data).
    private val basePath = Paths.get(System.getProperty("user.home"), directory)

    init {
        // Asegura que el directorio de almacenamiento exista al crear la instancia.
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath)
            println("Directorio de almacenamiento creado en: $basePath")
        }
    }

    // Función de ayuda para obtener un objeto File a partir de una clave.
    private fun getFile(key: String): File = basePath.resolve(key).toFile()

    // Guarda el array de bytes en un archivo. La clave es el nombre del archivo.
    override fun put(key: String, data: ByteArray): Boolean {
        return try {
            // Escribe (o sobrescribe) los bytes en el archivo.
            getFile(key).writeBytes(data)
            true
        } catch (e: Exception) {
            // Devuelve false si hay un error de escritura.
            e.printStackTrace()
            false
        }
    }

    // Lee todos los bytes de un archivo.
    override fun get(key: String): ByteArray? {
        val file = getFile(key)
        // Devuelve los bytes si el archivo existe, sino null.
        return if (file.exists()) file.readBytes() else null
    }

    // Lista los nombres de todos los archivos que comienzan con un prefijo.
    override fun keys(prefix: String): List<String> {
        return basePath.toFile().listFiles { _, name -> name.startsWith(prefix) }
            // Mapea la lista de archivos a una lista de sus nombres.
            ?.map { it.name } ?: emptyList() // Devuelve lista vacía si hay error.
    }

    // Elimina un archivo usando la clave como nombre.
    override fun remove(key: String): Boolean {
        val file = getFile(key)
        // Devuelve true si el archivo existía y fue borrado.
        return if (file.exists()) file.delete() else false
    }
}