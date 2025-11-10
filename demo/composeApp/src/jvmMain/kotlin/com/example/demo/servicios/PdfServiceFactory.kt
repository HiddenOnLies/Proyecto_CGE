package com.example.demo.servicios

/**
 * Implementación "real" (actual) de la fábrica de PdfService
 * para la plataforma Desktop (JVM).
 *
 * Esta función es llamada por el código compartido (shared)
 * cuando se ejecuta en la app de escritorio.
 */
actual fun createPdfService(): PdfService {
    // Devuelve la implementación específica de PDF para escritorio,
    // que usa la librería OpenPDF.
    return DesktopPdfService()
}