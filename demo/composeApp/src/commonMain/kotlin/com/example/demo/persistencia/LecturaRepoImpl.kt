package com.example.demo.persistencia

import com.example.demo.dominio.LecturaConsumo

// Implementación del repositorio de lecturas de consumo.
class LecturaRepoImpl(
    // Dependencia privada para el almacenamiento genérico.
    private val persistencia: PersistenciaDatos
) : LecturaRepositorio {

    // Prefijo para las claves de lectura (ej: "lectura-IDMEDIDOR-AÑO-MES").
    private val prefix = "lectura-"

    // Registra una nueva lectura de consumo.
    override fun registrar(lectura: LecturaConsumo): LecturaConsumo {
        // Genera una clave única para la lectura.
        val key = "$prefix${lectura.idMedidor}-${lectura.anio}-${lectura.mes}"

        // Guarda la lectura usando la clave.
        persistencia.save(key, lectura)
        return lectura
    }

    // Lista las lecturas para un medidor en un mes/año específico.
    override fun listarPorMedidorMes(idMedidor: String, anio: Int, mes: Int): List<LecturaConsumo> {
        // Este modelo asume solo una lectura por mes.
        val key = "$prefix$idMedidor-$anio-$mes"

        // Intenta leer esa única lectura y la devuelve en una lista, o una lista vacía.
        return listOfNotNull(persistencia.read<LecturaConsumo>(key))
    }

    // Obtiene la última lectura registrada para un medidor (la más reciente por fecha).
    override fun ultimaLectura(idMedidor: String): LecturaConsumo? {
        // Implementación simplificada:
        // 1. Obtiene todas las claves de lecturas para ese medidor.
        return persistencia.listKeys("$prefix$idMedidor-")
            // 2. Lee cada objeto LecturaConsumo (descartando nulos).
            .mapNotNull { key -> persistencia.read<LecturaConsumo>(key) }
            // 3. Encuentra la lectura con el valor máximo de "año * 100 + mes".
            //    Esto (ej: 202511 > 202510) sirve para comparar fechas fácilmente.
            .maxByOrNull { it.anio * 100 + it.mes }
    }
}