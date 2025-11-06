package com.example.demo.dominio

data class TarifaComercial(
    val cargoFijo: Double,
    val precioKwh: Double,
    val recargoComercial: Double,
    val iva: Double
) : Tarifa {
    override fun nombre(): String = "Tarifa Comercial"

    override fun calcular(kwh: Double): TarifaDetalle {
        val subtotal = kwh * precioKwh
        val cargos = cargoFijo + recargoComercial
        val montoIva = (subtotal + cargos) * iva
        val total = subtotal + cargos + montoIva
        return TarifaDetalle(kwh, subtotal, cargos, montoIva, total)
    }
}