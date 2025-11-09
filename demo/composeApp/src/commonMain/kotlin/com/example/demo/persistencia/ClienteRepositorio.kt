package com.example.demo.persistencia

import com.example.demo.dominio.Cliente

// Define el contrato para el repositorio de Clientes.
// Abstrae las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para los clientes.
interface ClienteRepositorio {

    // Guarda un nuevo cliente.
    fun crear(cliente: Cliente): Cliente

    // Actualiza un cliente existente.
    fun actualizar(cliente: Cliente): Cliente

    // Elimina un cliente por su RUT. Devuelve true si tuvo éxito.
    fun eliminar(rut: String): Boolean

    // Obtiene un cliente por su RUT. Devuelve null si no se encuentra.
    fun obtenerPorRut(rut: String): Cliente?

    // Lista todos los clientes, con un filtro opcional por nombre or RUT.
    // El filtro por defecto es una cadena vacía (listar todo).
    fun listar(filtro: String = ""): List<Cliente>
}