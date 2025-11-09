package com.example.demo.persistencia

import com.example.demo.dominio.Boleta

// Implementación del repositorio de boletas.
// Se encarga de la lógica de cómo se guardan y recuperan las boletas
// usando una capa de persistencia genérica.
class BoletaRepoImpl(
    // Dependencia privada para el almacenamiento (ej. un archivo o BD).
    private val persistencia: PersistenciaDatos
) : BoletaRepositorio {

    // Prefijo para organizar las claves de boletas en el almacén.
    private val prefix = "boleta-"

    // Guarda una boleta.
    override fun guardar(boleta: Boleta): Boleta {
        // Genera una clave única (ej: "boleta-RUT-2025-11").
        val key = "$prefix${boleta.idCliente}-${boleta.anio}-${boleta.mes}"

        // Usa la capa de persistencia para guardar el objeto con esa clave.
        persistencia.save(key, boleta)
        return boleta
    }

    // Obtiene una boleta específica.
    override fun obtener(rutCliente: String, anio: Int, mes: Int): Boleta? {
        // Reconstruye la clave exacta que debe tener la boleta.
        val key = "$prefix$rutCliente-$anio-$mes"

        // Lee el objeto desde persistencia, puede devolver null si no existe.
        return persistencia.read(key)
    }

    // Lista todas las boletas de un cliente.
    override fun listarPorCliente(rutCliente: String): List<Boleta> {
        // 1. Busca todas las claves que empiecen con "boleta-RUT-".
        val claves = persistencia.listKeys("$prefix$rutCliente-")

        // 2. Para cada clave encontrada, intenta leer el objeto Boleta.
        // 3. 'mapNotNull' descarta automáticamente cualquier 'null' (datos corruptos o no encontrados).
        return claves.mapNotNull { key ->
            persistencia.read<BoEscribe>(key)
        }
    }
}