package com.example.demo.persistencia

import com.example.demo.dominio.Medidor

// Implementación del repositorio de Medidor.
class MedidorRepoImpl(
    // Dependencia privada para el almacenamiento genérico.
    private val persistencia: PersistenciaDatos
) : MedidorRepositorio {

    // Prefijo para la clave del medidor (ej: "medidor-CODIGO").
    private val prefix = "medidor-"

    // Clave del índice para mapear un RUT de cliente a una lista de códigos de medidor.
    // Necesario porque un K/V store no puede buscar "medidores por cliente" directamente.
    private val indexClienteMedidor = "idx-cliente-medidores-"

    // Guarda un nuevo medidor y lo asocia a un cliente actualizando el índice.
    override fun crear(medidor: Medidor, rutCliente: String): Medidor {
        // 1. Guarda el objeto Medidor en sí (ej: "medidor-M123").
        persistencia.save(prefix + medidor.codigo, medidor)

        // 2. Obtiene la lista actual de códigos de medidor de ese cliente.
        val medidoresDelCliente = obtenerIdsDeCliente(rutCliente).toMutableSet()

        // 3. Añade el nuevo código de medidor a la lista.
        medidoresDelCliente.add(medidor.codigo)

        // 4. Guarda la lista actualizada en el índice (ej: "idx-cliente-medidores-RUT1").
        persistencia.save(indexClienteMedidor + rutCliente, medidoresDelCliente.toList())
        return medidor
    }

    // Obtiene un medidor por su código. Devuelve null si no existe.
    override fun obtenerPorCodigo(codigo: String): Medidor? = persistencia.read(prefix + codigo)

    // Lista todos los medidores de un cliente usando el índice.
    override fun listarPorCliente(rutCliente: String): List<Medidor> {
        // 1. Obtiene la lista de IDs de medidor para el cliente (ej: ["M123", "M456"]).
        val medidorIds = obtenerIdsDeCliente(rutCliente)

        // 2. Carga cada objeto Medidor completo basado en su ID (descartando nulos).
        return medidorIds.mapNotNull { id -> obtenerPorCodigo(id) }
    }

    // Elimina un medidor por su código.
    // Nota: Esto no limpia el ID del medidor del índice del cliente.
    override fun eliminar(codigo: String): Boolean = persistencia.delete(prefix + codigo)

    // Función privada para leer la lista de códigos de medidor desde el índice.
    private fun obtenerIdsDeCliente(rutCliente: String): List<String> {
        // Lee la lista de códigos (ej: "idx-cliente-medidores-RUT1").
        // Devuelve una lista vacía si el índice no existe.
        return persistencia.read<List<String>>(indexClienteMedidor + rutCliente) ?: emptyList()
    }

    // Actualiza un medidor sobrescribiendo sus datos.
    override fun actualizar(medidor: Medidor): Medidor {
        // 'save' sobrescribe el objeto existente usando la misma clave.
        // No es necesario actualizar el índice si el 'codigo' y 'rutCliente' no cambian.
        persistencia.save(prefix + medidor.codigo, medidor)
        return medidor
    }
}