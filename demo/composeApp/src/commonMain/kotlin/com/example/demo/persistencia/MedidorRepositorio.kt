package com.example.demo.persistencia

import com.example.demo.dominio.Medidor

interface MedidorRepositorio {
    fun crear(medidor: Medidor, rutCliente: String): Medidor
    fun obtenerPorCodigo(codigo: String): Medidor?
    fun listarPorCliente(rutCliente: String): List<Medidor>
    fun eliminar(codigo: String): Boolean
}