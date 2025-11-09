package com.example.demo.servicios

import com.example.demo.dominio.Boleta
import com.example.demo.dominio.Cliente

/**
 * Define el contrato para un servicio que genera archivos PDF.
 * La implementación real usaría una librería de PDF (ej. iText, PDFBox).
 */
interface PdfService {

     // Genera un único archivo PDF a partir de una lista de boletas y clientes
    fun generarBoletasPDF(boletas: List<Boleta>, clientes: Map<String, Cliente>): ByteArray
}