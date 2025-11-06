package com.example.demo.persistencia

import com.example.demo.dominio.LecturaConsumo

class LecturaRepoImpl(private val persistencia: PersistenciaDatos) : LecturaRepositorio {
    private val prefix = "lectura-"

    override fun registrar(lectura: LecturaConsumo): LecturaConsumo {
        val key = "$prefix${lectura.idMedidor}-${lectura.anio}-${lectura.mes}"
        persistencia.save(key, lectura)
        return lectura
    }

    override fun listarPorMedidorMes(idMedidor: String, anio: Int, mes: Int): List<LecturaConsumo> {
        // En este modelo simple, solo hay una lectura por mes.
        val key = "$prefix$idMedidor-$anio-$mes"
        return listOfNotNull(persistencia.read<LecturaConsumo>(key))
    }

    override fun ultimaLectura(idMedidor: String): LecturaConsumo? {
        // Implementación simplificada: busca en todas las lecturas del medidor y devuelve la más reciente.
        return persistencia.listKeys("$prefix$idMedidor-")
            .mapNotNull { key -> persistencia.read<LecturaConsumo>(key) }
            .maxByOrNull { it.anio * 100 + it.mes }
    }
}