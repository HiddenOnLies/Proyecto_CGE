package com.example.demo.dominio

// Interfaz para objetos que pueden ser convertidos a una tabla para un PDF.
interface ExportablePDF {
    fun toPdfTable(): PdfTable
}