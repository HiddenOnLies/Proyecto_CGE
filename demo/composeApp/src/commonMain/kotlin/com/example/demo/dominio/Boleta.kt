@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio

import kotlinx.serialization.Serializable
import kotlin.math.round
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
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
) : EntidadBase(), ExportablePDF {

    /**
     * Convierte los datos de la boleta a un formato de tabla para ser usado en un PDF.
     * Esta versión utiliza una función de formato multiplataforma.
     */
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
 * Función de extensión para Double que lo formatea como un string con 2 decimales.
 * Es 100% compatible con Kotlin Multiplatform.
 *
 * @return Un String formateado, ej: 150.0 -> "150.00", 123.456 -> "123.46"
 */
fun Double.toCurrencyString(): String {
    // Redondea matemáticamente a 2 decimales
    val roundedValue = round(this * 100) / 100.0

    // Convierte a String y separa la parte entera de la decimal
    val parts = roundedValue.toString().split('.')
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) {
        parts[1].padEnd(2, '0') // Asegura que siempre haya 2 dígitos decimales
    } else {
        "00"
    }

    return "$integerPart.$decimalPart"
}