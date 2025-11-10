package com.example.demo

import com.example.demo.persistencia.* // Asegúrate de que los imports estén correctos
import com.example.demo.servicios.BoletaService
import com.example.demo.servicios.TarifaService

/**
 * Contenedor de Dependencias (Service Locator).
 * Crea y provee una instancia única de todos los servicios
 * y repositorios que necesita la aplicación.
 */
class AppContainer {

    // 1. Obtiene el driver de almacenamiento (KMP).
    // Llama a la fábrica `expect` que devuelve la implementación `actual`
    // (FileStorageDriver en Desktop, LocalStorageDriver en Web).
    private val storageDriver: IStorageDriver = createStorageDriver()

    // 2. Crea el gestor de persistencia (serialización).
    // Le inyecta el driver específico de la plataforma.
    private val persistenciaDatos: PersistenciaDatos = PersistenciaDatos(storageDriver)

    // 3. Crea todas las implementaciones de repositorios.
    // Todos usan la misma instancia de PersistenciaDatos.
    val clienteRepo: ClienteRepositorio = ClienteRepoImpl(persistenciaDatos)
    val medidorRepo: MedidorRepositorio = MedidorRepoImpl(persistenciaDatos)
    val lecturaRepo: LecturaRepositorio = LecturaRepoImpl(persistenciaDatos)
    val boletaRepo: BoletaRepositorio = BoletaRepoImpl(persistenciaDatos)

    // 4. Crea los servicios de lógica de negocio.

    // Servicio de tarifas (privado, solo lo usa BoletaService).
    private val tarifaService: TarifaService = TarifaService()

    // Servicio principal de boletas (público, usado por la UI).
    // Se le inyectan todos los repositorios que necesita.
    val boletaService: BoletaService = BoletaService(
        clientes = clienteRepo,
        medidores = medidorRepo,
        lecturas = lecturaRepo,
        boletas = boletaRepo,
        tarifas = tarifaService
    )
}