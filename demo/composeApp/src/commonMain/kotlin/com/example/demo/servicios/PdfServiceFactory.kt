package com.example.demo.servicios

/**
 * Declara una función "esperada" (expect).
 * Esto es una promesa de que cada plataforma (desktop, js, etc.)
 * proporcionará una implementación real (actual) de esta función.
 *
 * El código en commonMain puede llamar a createPdfService() y KMP se encargará
 * de invocar la implementación correcta según la plataforma en la que se compile.
 */
expect fun createPdfService(): PdfService