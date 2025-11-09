package com.example.demo.servicios


//Implementación "real" (actual) de la fábrica para la plataforma Desktop.
//Cuando el código de commonMain llame a createPdfService(), y se esté ejecutando
//en la JVM, esta función será la que se invoque.
actual fun createPdfService(): PdfService {
    return DesktopPdfService()
}