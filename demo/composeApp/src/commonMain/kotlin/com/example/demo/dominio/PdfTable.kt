package com.example.demo.dominio

/**
 * Representa una tabla de datos simple con encabezados y filas.
 */
data class PdfTable(
    val headers: List<String>,
    val rows: List<List<String>>
)