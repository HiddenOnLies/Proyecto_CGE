package com.example.demo.persistencia

import com.example.demo.dominio.LecturaConsumo

interface LecturaRepositorio {
    fun registrar(lectura: LecturaConsumo): LecturaConsumo
    fun listarPorMedidorMes(idMedidor: String, anio: Int, mes: Int): List<LecturaConsumo>
    fun ultimaLectura(idMedidor: String): LecturaConsumo?
}