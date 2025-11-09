package com.example.demo.persistencia

import com.example.demo.dominio.Medidor

class MedidorRepoImpl(private val persistencia: PersistenciaDatos) : MedidorRepositorio {
    private val prefix = "medidor-"
    // Necesitamos un índice para saber qué medidores pertenecen a qué cliente
    private val indexClienteMedidor = "idx-cliente-medidores-"

    override fun crear(medidor: Medidor, rutCliente: String): Medidor {
        persistencia.save(prefix + medidor.codigo, medidor)
        // Actualiza el índice
        val medidoresDelCliente = obtenerIdsDeCliente(rutCliente).toMutableSet()
        medidoresDelCliente.add(medidor.codigo)
        persistencia.save(indexClienteMedidor + rutCliente, medidoresDelCliente.toList())
        return medidor
    }

    override fun obtenerPorCodigo(codigo: String): Medidor? = persistencia.read(prefix + codigo)

    override fun listarPorCliente(rutCliente: String): List<Medidor> {
        val medidorIds = obtenerIdsDeCliente(rutCliente)
        return medidorIds.mapNotNull { id -> obtenerPorCodigo(id) }
    }

    override fun eliminar(codigo: String): Boolean = persistencia.delete(prefix + codigo)

    private fun obtenerIdsDeCliente(rutCliente: String): List<String> {
        return persistencia.read<List<String>>(indexClienteMedidor + rutCliente) ?: emptyList()
    }

    override fun actualizar(medidor: Medidor): Medidor {
        // La función 'save' sobrescribe el archivo existente con el mismo 'key',
        // lo que funciona perfectamente como una operación de actualización.
        persistencia.save(prefix + medidor.codigo, medidor)
        return medidor
    }
}