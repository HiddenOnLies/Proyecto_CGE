@file:OptIn(ExperimentalTime::class) // Habilita el uso de APIs experimentales de Kotlin (Time).
package com.example.demo.servicios

import com.example.demo.dominio.Boleta
import com.example.demo.dominio.LecturaConsumo
import com.example.demo.persistencia.BoletaRepositorio
import com.example.demo.persistencia.ClienteRepositorio
import com.example.demo.persistencia.LecturaRepositorio
import com.example.demo.persistencia.MedidorRepositorio
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Orquesta la lógica de negocio para la gestión de boletas.
 * Coordina los repositorios y otros servicios.
 *
 * @property clientes Repositorio para datos de clientes.
 * @property medidores Repositorio para datos de medidores.
 * @property lecturas Repositorio para datos de lecturas.
 * @property boletas Repositorio para datos de boletas.
 * @property tarifas Servicio para calcular tarifas.
 */
class BoletaService(
    private val clientes: ClienteRepositorio,
    private val medidores: MedidoresRepositorio,
    private val lecturas: LecturaRepositorio,
    private val boletas: BoletaRepositorio,
    private val tarifas: TarifaService
) {

    /**
     * Calcula el consumo total (kWh) de un cliente en un mes/año.
     * Suma las lecturas de todos sus medidores en ese período.
     */
    fun calcularKwhClienteMes(rutCliente: String, anio: Int, mes: Int): Double {
        // Obtiene todos los medidores del cliente.
        val medidoresCliente = medidores.listarPorCliente(rutCliente)
        if (medidoresCliente.isEmpty()) {
            println("Advertencia: El cliente con RUT $rutCliente no tiene medidores asociados.")
            return 0.0
        }

        // Suma el total de kWh leídos en todos sus medidores para ese mes.
        return medidoresCliente.sumOf { medidor ->
            lecturas.listarPorMedidorMes(medidor.id, anio, mes)
                .sumOf(LecturaConsumo::kwhLeidos)
        }
    }

    /**
     * Genera, calcula y guarda la boleta de un cliente para un período.
     * Si la boleta ya existe, la devuelve sin recalcular.
     */
    fun emitirBoletaMensual(rutCliente: String, anio: Int, mes: Int): Boleta {
        // 1. Revisa si la boleta ya existe en la base de datos.
        val boletaExistente = boletas.obtener(rutCliente, anio, mes)
        if (boletaExistente != null) return boletaExistente // La devuelve si la encuentra.

        // 2. Obtiene los datos del cliente.
        val cliente = clientes.obtenerPorRut(rutCliente)
            ?: throw IllegalArgumentException("No se encontró el cliente con RUT $rutCliente")

        // 3. Calcula el consumo total de kWh del cliente en ese mes.
        val kwhDelMes = calcularKwhClienteMes(rutCliente, anio, mes)

        // 4. Determina qué tipo de tarifa aplica al cliente (ej. Residencial, Comercial).
        val tarifaAplicable = tarifas.tarifaPara(cliente)

        // 5. Usa el polimorfismo de la tarifa para calcular el detalle del cobro.
        val detalleCobro = tarifaAplicable.calcular(kwhDelMes)

        // 6. Crea el nuevo objeto Boleta.
        val nuevaBoleta = Boleta(
            id = "bol-${cliente.rut}-$anio-$mes", // ID único.
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            idCliente = cliente.rut,
            anio = anio,
            mes = mes,
            kwhTotal = kwhDelMes,
            detalle = detalleCobro, // Asigna el detalle calculado.
            estado = com.example.demo.dominio.EstadoBoleta.EMITIDA
        )

        // 7. Guarda la boleta recién creada en el repositorio.
        return boletas.guardar(nuevaBoleta)
    }

    /**
     * Exporta la boleta de un cliente/mes a un PDF (como ByteArray).
     */
    fun exportarPdfClienteMes(rutCliente: String, anio: Int, mes: Int): ByteArray {
        // 1. Llama a la fábrica `expect` para obtener el servicio de PDF
        //    correspondiente a la plataforma actual (Desktop o Web).
        val pdfService = createPdfService()

        // 2. Asegura que la boleta exista (la emite si es necesario).
        val boleta = emitirBoletaMensual(rutCliente, anio, mes)
        // Obtiene al cliente para añadir sus datos al PDF.
        val cliente = clientes.obtenerPorRut(rutCliente)
            ?: throw IllegalStateException("Cliente no encontrado después de emitir boleta.")

        // 3. Llama al servicio de PDF para generar los bytes del archivo.
        return pdfService.generarBoletasPDF(
            boletas = listOf(boleta), // Lista con la boleta única.
            clientes = mapOf(cliente.rut to cliente) // Mapa para buscar datos del cliente.
        )
    }

    /**
     * Elimina un cliente y todos sus medidores asociados.
     * (Lógica de borrado en cascada manual).
     */
    fun eliminarClienteCompleto(rutCliente: String) {
        // 1. Llama al repositorio de medidores para que borre todos los de este cliente.
        medidores.eliminarPorCliente(rutCliente)

        // 2. Llama al repositorio de clientes para borrar el cliente en sí.
        clientes.eliminar(rutCliente)

        // Nota: Las lecturas y boletas quedan "huérfanas", pero se acceden
        // a través del cliente/medidor, por lo que no aparecerán.
    }
}