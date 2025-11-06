package com.example.demo.persistencia

import com.example.demo.dominio.Boleta

class BoletaRepoImpl(private val persistencia: PersistenciaDatos) : BoletaRepositorio {
    private val prefix = "boleta-"

    override fun guardar(boleta: Boleta): Boleta {
        val key = "$prefix${boleta.idCliente}-${boleta.anio}-${boleta.mes}"
        persistencia.save(key, boleta)
        return boleta
    }

    override fun obtener(rutCliente: String, anio: Int, mes: Int): Boleta? {
        val key = "$prefix$rutCliente-$anio-$mes"
        return persistencia.read(key)
    }

    override fun listarPorCliente(rutCliente: String): List<Boleta> {
        return persistencia.listKeys("$prefix$rutCliente-")
            .mapNotNull { key -> persistencia.read<Boleta>(key) }
    }
}