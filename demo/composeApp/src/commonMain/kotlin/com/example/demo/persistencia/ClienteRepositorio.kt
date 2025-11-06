package com.example.demo.persistencia

import com.example.demo.dominio.Cliente

interface ClienteRepositorio {
    fun crear(cliente: Cliente): Cliente
    fun actualizar(cliente: Cliente): Cliente
    fun eliminar(rut: String): Boolean
    fun obtenerPorRut(rut: String): Cliente?
    fun listar(filtro: String = ""): List<Cliente>
}