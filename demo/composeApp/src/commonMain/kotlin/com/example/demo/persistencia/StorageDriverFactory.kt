package com.example.demo.persistencia

/**
 * Declara una función "esperada" (expect).
 * Promete que cada plataforma proporcionará una forma de crear un IStorageDriver.
 */
expect fun createStorageDriver(): IStorageDriver