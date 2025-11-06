package com.example.demo.servicios

import com.example.demo.dominio.Boleta
import com.example.demo.dominio.Cliente

/**
 * Define el contrato para un servicio que genera archivos PDF.
 * La implementación real usaría una librería de PDF (ej. iText, PDFBox).
 */
interface PdfService {
    /**
     * Genera un único archivo PDF a partir de una lista de boletas y clientes.
     *
     * @param boletas La lista de boletas a incluir en el reporte.
     * @param clientes Un mapa de [rut, Cliente] para obtener datos adicionales si es necesario.
     * @return Un ByteArray con los datos binarios del archivo PDF generado.
     */
    fun generarBoletasPDF(boletas: List<Boleta>, clientes: Map<String, Cliente>): ByteArray
}