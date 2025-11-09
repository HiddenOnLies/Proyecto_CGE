package com.example.demo.persistencia

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


//Implementación de IStorageDriver que guarda los datos en archivos locales.
//Cada clave corresponde a un archivo dentro de un directorio base.

class FileStorageDriver(directory: String = ".cge_gestion_data") : IStorageDriver {
    // Define la ruta base en el directorio home del usuario
    private val basePath = Paths.get(System.getProperty("user.home"), directory)

    init {
        // Crea el directorio si no existe
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath)
            println("Directorio de almacenamiento creado en: $basePath")
        }
    }

    private fun getFile(key: String): File = basePath.resolve(key).toFile()

    override fun put(key: String, data: ByteArray): Boolean {
        return try {
            getFile(key).writeBytes(data)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun get(key: String): ByteArray? {
        val file = getFile(key)
        return if (file.exists()) file.readBytes() else null
    }

    override fun keys(prefix: String): List<String> {
        return basePath.toFile().listFiles { _, name -> name.startsWith(prefix) }
            ?.map { it.name } ?: emptyList()
    }

    override fun remove(key: String): Boolean {
        val file = getFile(key)
        return if (file.exists()) file.delete() else false
    }
}