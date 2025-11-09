package com.example.demo.persistencia


//Implementación "real" (actual) de la fábrica para la plataforma JVM.
//Cuando el código común llame a createStorageDriver(), esta función se ejecutará.
//Como estamos en jvmMain, SÍ podemos ver y crear una instancia de FileStorageDriver.
actual fun createStorageDriver(): IStorageDriver {
    return FileStorageDriver()
}