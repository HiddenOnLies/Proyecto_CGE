package com.example.demo.persistencia

import com.example.demo.dominio.Cliente

class ClienteRepoImpl(private val persistencia: PersistenciaDatos) : ClienteRepositorio {
    private val prefix = "cliente-"

    override fun crear(cliente: Cliente): Cliente {
        persistencia.save(prefix + cliente.rut, cliente)
        return cliente
    }

    override fun actualizar(cliente: Cliente): Cliente {
        // En una implementación simple, crear y actualizar hacen lo mismo.
        return crear(cliente)
    }

    override fun eliminar(rut: String): Boolean = persistencia.delete(prefix + rut)

    override fun obtenerPorRut(rut: String): Cliente? = persistencia.read(prefix + rut)

    override fun listar(filtro: String): List<Cliente> {
        return persistencia.listKeys(prefix)
            .mapNotNull { key -> persistencia.read<Cliente>(key) }
            .filter { it.nombre.contains(filtro, ignoreCase = true) || it.rut.contains(filtro) }
    }
}