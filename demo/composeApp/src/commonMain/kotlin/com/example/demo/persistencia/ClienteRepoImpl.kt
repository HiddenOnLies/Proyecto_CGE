package com.example.demo.persistencia

import com.example.demo.dominio.Cliente

// Implementación del repositorio de clientes.
// Gestiona el guardado y recuperación de objetos Cliente.
class ClienteRepoImpl(
    // Dependencia privada para el almacenamiento genérico.
    private val persistencia: PersistenciaDatos
) : ClienteRepositorio {

    // Prefijo para las claves de cliente (ej: "cliente-RUT").
    private val prefix = "cliente-"

    // Guarda un cliente nuevo. Usa el RUT como parte de la clave.
    override fun crear(cliente: Cliente): Cliente {
        persistencia.save(prefix + cliente.rut, cliente)
        return cliente
    }

    // Actualiza un cliente. En este caso, sobrescribe el anterior.
    override fun actualizar(cliente: Cliente): Cliente {
        // Para un almacén clave-valor, 'actualizar' es lo mismo que 'crear'.
        return crear(cliente)
    }

    // Elimina un cliente por su RUT.
    override fun eliminar(rut: String): Boolean = persistencia.delete(prefix + rut)

    // Obtiene un cliente por su RUT. Devuelve null si no lo encuentra.
    override fun obtenerPorRut(rut: String): Cliente? = persistencia.read(prefix + rut)

    // Lista todos los clientes, permitiendo un filtro por nombre o RUT.
    override fun listar(filtro: String): List<Cliente> {
        // 1. Obtiene todas las claves que comienzan con "cliente-".
        return persistencia.listKeys(prefix)
            // 2. Lee cada objeto Cliente (descartando nulos).
            .mapNotNull { key -> persistencia.read<Cliente>(key) }
            // 3. Filtra la lista si el nombre (ignorando mayúsculas) o el RUT contienen el texto del filtro.
            .filter { it.nombre.contains(filtro, ignoreCase = true) || it.rut.contains(filtro) }
    }
}