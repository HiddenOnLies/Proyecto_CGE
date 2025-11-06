package com.example.demo.persistencia

import com.example.demo.dominio.Boleta

interface BoletaRepositorio {
    fun guardar(boleta: Boleta): Boleta
    fun obtener(rutCliente: String, anio: Int, mes: Int): Boleta?
    fun listarPorCliente(rutCliente: String): List<Boleta>
}