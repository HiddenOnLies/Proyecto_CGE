package com.example.demo.dominio

// Clase que representa la tarifa comercial con su estructura de costos y cálculo.
data class TarifaComercial(
    val cargoFijo: Double,
    val precioKwh: Double,
    val recargoComercial: Double,
    val iva: Double
) : Tarifa {
    // Nombre descriptivo de la tarifa.
    override fun nombre(): String = "Tarifa Comercial"

    // Cálculo detallado de la tarifa basado en el consumo en kWh.
    override fun calcular(kwh: Double): TarifaDetalle {
        val subtotal = kwh * precioKwh
        val cargos = cargoFijo + recargoComercial
        val montoIva = (subtotal + cargos) * iva
        val total = subtotal + cargos + montoIva
        return TarifaDetalle(kwh, subtotal, cargos, montoIva, total)
    }
}