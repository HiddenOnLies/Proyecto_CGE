package com.example.demo.persistencia

import com.example.demo.dominio.Boleta

// Implementación del repositorio de boletas.
class BoletaRepoImpl(
    // Dependencia para el almacenamiento genérico (JSON, BD, etc.).
    private val persistencia: PersistenciaDatos
) : BoletaRepositorio {

    // Prefijo para claves de boletas (ej: "boleta-RUT-AÑO-MES").
    private val prefix = "boleta-"

    // Guarda o sobrescribe una boleta.
    override fun guardar(boleta: Boleta): Boleta {
        // Genera la clave única de la boleta.
        val key = "$prefix${boleta.idCliente}-${boleta.anio}-${boleta.mes}"
        // Guarda el objeto boleta usando la clave.
        persistencia.save(key, boleta)
        return boleta
    }

    // Obtiene una boleta específica por RUT, año y mes.
    override fun obtener(rutCliente: String, anio: Int, mes: Int): Boleta? {
        // Reconstruye la clave que debe tener la boleta.
        val key = "$prefix$rutCliente-$anio-$mes"
        // Lee el objeto; devuelve null si no existe.
        return persistencia.read(key)
    }

    // Lista todas las boletas de un cliente.
    override fun listarPorCliente(rutCliente: String): List<Boleta> {
        // 1. Obtiene todas las claves que empiecen con "boleta-RUT-".
        return persistencia.listKeys("$prefix$rutCliente-")
            // 2. Para cada clave, lee el objeto Boleta y descarta los nulos.
            .mapNotNull { key -> persistencia.read<Boleta>(key) }
    }
}