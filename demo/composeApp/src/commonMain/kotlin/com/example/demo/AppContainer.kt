package com.example.demo

import com.example.demo.persistencia.* // Asegúrate de que los imports estén correctos
import com.example.demo.servicios.BoletaService
import com.example.demo.servicios.TarifaService

class AppContainer {
    // --- LÍNEA CORREGIDA ---
    // Llama a la fábrica `expect` que será reemplazada por la implementación `actual` correcta.
    private val storageDriver: IStorageDriver = createStorageDriver()

    // El resto del archivo no necesita cambios
    private val persistenciaDatos: PersistenciaDatos = PersistenciaDatos(storageDriver)

    val clienteRepo: ClienteRepositorio = ClienteRepoImpl(persistenciaDatos)
    val medidorRepo: MedidorRepositorio = MedidorRepoImpl(persistenciaDatos)
    val lecturaRepo: LecturaRepositorio = LecturaRepoImpl(persistenciaDatos)
    val boletaRepo: BoletaRepositorio = BoletaRepoImpl(persistenciaDatos)

    private val tarifaService: TarifaService = TarifaService()
    val boletaService: BoletaService = BoletaService(
        clientes = clienteRepo,
        medidores = medidorRepo,
        lecturas = lecturaRepo,
        boletas = boletaRepo,
        tarifas = tarifaService
    )
}