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

    // Genera un PDF (como ByteArray) con los datos de una o más boletas.
    override fun generarBoletasPDF(boletas: List<Boleta>, clientes: Map<String, Cliente>): ByteArray {
        // Usa un ByteArrayOutputStream para escribir el PDF en memoria.
        val outputStream = ByteArrayOutputStream()
        val document = Document()
        // Vincula el documento con el stream de salida.
        PdfWriter.getInstance(document, outputStream)

        document.open()

        // Define las fuentes para el PDF.
        val titleFont = Font(Font.HELVETICA, 18f, Font.BOLD)
        val headerFont = Font(Font.HELVETICA, 12f, Font.BOLD)
        val bodyFont = Font(Font.HELVETICA, 12f, Font.NORMAL)

        // Itera sobre cada boleta para agregarla al documento.
        boletas.forEach { boleta ->
            // Obtiene los datos del cliente usando el ID de la boleta.
            val cliente = clientes[boleta.idCliente]

            // --- Añade Título y Datos del Cliente ---
            document.add(Paragraph("Boleta Electrónica CGE", titleFont))
            document.add(Paragraph(" ")) // Espacio
            document.add(Paragraph("Cliente: ${cliente?.nombre ?: "N/A"}", bodyFont))
            document.add(Paragraph("RUT: ${boleta.idCliente}", bodyFont))
            document.add(Paragraph("Período: ${boleta.mes}/${boleta.anio}", bodyFont))
            document.add(Paragraph(" ")) // Espacio

            // --- Crea la Tabla con el Detalle de la boleta ---
            val table = PdfPTable(2) // Tabla de 2 columnas.
            table.widthPercentage = 100f

            // Configura y añade los encabezados de la tabla.
            val headerCell1 = Paragraph("Concepto", headerFont)
            val headerCell2 = Paragraph("Valor", headerFont)
            val cell1 = table.defaultCell
            cell1.backgroundColor = Color.LIGHT_GRAY // Fondo gris para cabecera.
            table.addCell(headerCell1)
            table.addCell(headerCell2)

            // Obtiene las filas de datos desde la boleta (usando toPdfTable)
            // y las añade a la tabla.
            boleta.toPdfTable().rows.forEach { row ->
                table.addCell(Paragraph(row[0], bodyFont))
                table.addCell(Paragraph(row[1], bodyFont))
            }
            // Añade la tabla terminada al documento.
            document.add(table)

            // Si se está generando más de una boleta, añade un salto de página.
            if (boletas.size > 1) {
                document.newPage()
            }
        }

        // Cierra el documento (finaliza la escritura en el stream).
        document.close()
        // Devuelve el PDF como un array de bytes.
        return outputStream.toByteArray()
    }
}