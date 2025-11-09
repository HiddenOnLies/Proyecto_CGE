package com.example.demo.persistencia

/**
 * Declara una función "esperada" (expect) para Kotlin Multiplatform.
 * Promete que cada plataforma (desktopApp, webApp) proporcionará
 * su propia implementación "actual" de esta función.
 * Se usará para obtener un driver de almacenamiento específico
 * para cada plataforma (ej. FileStorage en Desktop, LocalStorage en Web).
 */
expect fun createStorageDriver(): IStorageDriver