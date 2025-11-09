package com.example.demo.persistencia

import com.example.demo.dominio.Medidor

// Define el contrato para el repositorio de Medidor.
// Abstrae las operaciones CRUD y la asignación a clientes.
interface MedidorRepositorio {

    // Guarda un nuevo medidor y lo asocia a un cliente.
    fun crear(medidor: Medidor, rutCliente: String): Medidor

    // Obtiene un medidor por su código. Devuelve null si no existe.
    fun obtenerPorCodigo(codigo: String): Medidor?

    // Lista todos los medidores asociados a un cliente.
    fun listarPorCliente(rutCliente: String): List<Medidor>

    // Elimina un medidor por su código. Devuelve true si tuvo éxito.
    fun eliminar(codigo: String): Boolean

    // Actualiza los datos de un medidor existente.
    fun actualizar(medidor: Medidor): Medidor
}