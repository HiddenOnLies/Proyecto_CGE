package com.example.demo.persistencia

import com.example.demo.dominio.LecturaConsumo

// Define el contrato para el repositorio de LecturaConsumo.
// Abstrae el acceso a los datos de lecturas.
interface LecturaRepositorio {

    // Guarda una nueva lectura de consumo.
    fun registrar(lectura: LecturaConsumo): LecturaConsumo

    // Lista las lecturas de un medidor en un mes y año específicos.
    fun listarPorMedidorMes(idMedidor: String, anio: Int, mes: Int): List<LecturaConsumo>

    // Obtiene la última lectura registrada de un medidor (la más reciente).
    // Devuelve null si no hay lecturas.
    fun ultimaLectura(idMedidor: String): LecturaConsumo?
}