package com.example.demo.dominio

data class TarifaResidencial(
    val cargoFijo: Double,
    val precioKwh: Double,
    val iva: Double
) : Tarifa {
    override fun nombre(): String = "Tarifa Residencial"

    override fun calcular(kwh: Double): TarifaDetalle {
        val subtotal = kwh * precioKwh
        val cargos = cargoFijo
        val montoIva = (subtotal + cargos) * iva
        val total = subtotal + cargos + montoIva
        return TarifaDetalle(kwh, subtotal, cargos, montoIva, total)
    }
}