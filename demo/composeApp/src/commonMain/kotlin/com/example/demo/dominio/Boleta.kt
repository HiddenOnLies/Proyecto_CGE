@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlinx.serialization.Serializable
import kotlin.math.round
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Marca la clase como serializable (permite convertirla a JSON, etc.)
@Serializable
/**
 * Representa una boleta o factura de consumo eléctrico.
 * Contiene información del cliente, periodo, consumo total y detalle de cobros.
 */
data class Boleta(
    override val id: String,
    @Serializable(with = InstantSerializer::class)
    override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    override val updatedAt: Instant,
    val idCliente: String,
    val anio: Int,
    val mes: Int,
    val kwhTotal: Double,
    val detalle: TarifaDetalle,
    var estado: EstadoBoleta
    
    /**
     * Convierte la información de la boleta en una tabla PDF con dos columnas:
     * una para los conceptos y otra para los valores.
     */
) : EntidadBase(), ExportablePDF {
    override fun toPdfTable(): PdfTable {
        val headers = listOf("Concepto", "Valor")
        val rows = listOf(
            listOf("Cliente ID", idCliente),
            listOf("Período", "$mes/$anio"),
            listOf("Consumo (kWh)", kwhTotal.toCurrencyString()),
            listOf("Subtotal", detalle.subtotal.toCurrencyString()),
            listOf("Cargos Adicionales", detalle.cargos.toCurrencyString()),
            listOf("IVA (19%)", detalle.iva.toCurrencyString()),
            listOf("Total a Pagar", detalle.total.toCurrencyString())
        )
        return PdfTable(headers, rows)
    }
}

/**
 * Funcion que convierte un valor Double en un String con formato monetario,
 * redondeando a dos decimales.
 * Ejemplo: 1234.5 -> "1234.50"
 */
fun Double.toCurrencyString(): String {
    val roundedValue = round(this * 100) / 100.0

    val parts = roundedValue.toString().split('.')
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) {
        parts[1].padEnd(2, '0')
    } else {
        "00"
    }

    return "$integerPart.$decimalPart"
}