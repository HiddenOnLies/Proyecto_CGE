package com.example.demo.servicios

import com.example.demo.dominio.Boleta
import com.example.demo.dominio.Cliente
import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import java.awt.Color
import java.io.ByteArrayOutputStream

/**
 * Implementación REAL del PdfService para la plataforma Desktop (JVM).
 * Utiliza la librería OpenPDF para generar un archivo PDF válido.
 */
class DesktopPdfService : PdfService {
    override fun generarBoletasPDF(boletas: List<Boleta>, clientes: Map<String, Cliente>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val document = Document()
        PdfWriter.getInstance(document, outputStream)

        document.open()

        val titleFont = Font(Font.HELVETICA, 18f, Font.BOLD)
        val headerFont = Font(Font.HELVETICA, 12f, Font.BOLD)
        val bodyFont = Font(Font.HELVETICA, 12f, Font.NORMAL)

        boletas.forEach { boleta ->
            val cliente = clientes[boleta.idCliente]

            // --- Título y Datos del Cliente ---
            document.add(Paragraph("Boleta Electrónica CGE", titleFont))
            document.add(Paragraph(" ")) // Espacio
            document.add(Paragraph("Cliente: ${cliente?.nombre ?: "N/A"}", bodyFont))
            document.add(Paragraph("RUT: ${boleta.idCliente}", bodyFont))
            document.add(Paragraph("Período: ${boleta.mes}/${boleta.anio}", bodyFont))
            document.add(Paragraph(" ")) // Espacio

            // --- Tabla con el Detalle ---
            val table = PdfPTable(2) // 2 columnas
            table.widthPercentage = 100f

            // Encabezados de la tabla
            val headerCell1 = Paragraph("Concepto", headerFont)
            val headerCell2 = Paragraph("Valor", headerFont)
            val cell1 = table.defaultCell
            cell1.backgroundColor = Color.LIGHT_GRAY
            table.addCell(headerCell1)
            table.addCell(headerCell2)

            // Contenido de la tabla
            boleta.toPdfTable().rows.forEach { row ->
                table.addCell(Paragraph(row[0], bodyFont))
                table.addCell(Paragraph(row[1], bodyFont))
            }
            document.add(table)

            // Si hay más de una boleta, añade una nueva página
            if (boletas.size > 1) {
                document.newPage()
            }
        }

        document.close()
        return outputStream.toByteArray()
    }
}