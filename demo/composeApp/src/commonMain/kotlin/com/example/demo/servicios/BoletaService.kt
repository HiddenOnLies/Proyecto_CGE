@file:OptIn(ExperimentalTime::class)
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
 * Orquesta toda la lógica de negocio para la gestión de boletas.
 * Utiliza los repositorios para acceder a los datos y otros servicios para tareas específicas.
**/
class BoletaService(
    private val clientes: ClienteRepositorio,
    private val medidores: MedidorRepositorio,
    private val lecturas: LecturaRepositorio,
    private val boletas: BoletaRepositorio,
    private val tarifas: TarifaService
) {

    /**
     * Calcula el consumo total en kWh para un cliente en un mes y año específicos.
     * Suma las lecturas de todos los medidores asociados a ese cliente.
     */
    fun calcularKwhClienteMes(rutCliente: String, anio: Int, mes: Int): Double {
        val medidoresCliente = medidores.listarPorCliente(rutCliente)
        if (medidoresCliente.isEmpty()) {
            println("Advertencia: El cliente con RUT $rutCliente no tiene medidores asociados.")
            return 0.0
        }

        return medidoresCliente.sumOf { medidor ->
            lecturas.listarPorMedidorMes(medidor.id, anio, mes)
                .sumOf(LecturaConsumo::kwhLeidos)
        }
    }

    /**
     * Genera, calcula, guarda y devuelve la boleta para un cliente en un período dado.
     * Si la boleta ya existe, la devuelve directamente.
     */
    fun emitirBoletaMensual(rutCliente: String, anio: Int, mes: Int): Boleta {
        // 1. Verificar si la boleta ya fue emitida
        val boletaExistente = boletas.obtener(rutCliente, anio, mes)
        if (boletaExistente != null) return boletaExistente

        // 2. Obtener el cliente
        val cliente = clientes.obtenerPorRut(rutCliente)
            ?: throw IllegalArgumentException("No se encontró el cliente con RUT $rutCliente")

        // 3. Calcular el consumo total del mes
        val kwhDelMes = calcularKwhClienteMes(rutCliente, anio, mes)

        // 4. Obtener la tarifa aplicable para el cliente
        val tarifaAplicable = tarifas.tarifaPara(cliente)

        // 5. Calcular el detalle de la boleta usando el polimorfismo de la tarifa
        val detalleCobro = tarifaAplicable.calcular(kwhDelMes)

        // 6. Crear la instancia de la nueva boleta
        val nuevaBoleta = Boleta(
            id = "bol-${cliente.rut}-$anio-$mes",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            idCliente = cliente.rut,
            anio = anio,
            mes = mes,
            kwhTotal = kwhDelMes,
            detalle = detalleCobro,
            estado = com.example.demo.dominio.EstadoBoleta.EMITIDA
        )

        // 7. Guardar la nueva boleta en la persistencia
        return boletas.guardar(nuevaBoleta)
    }

    /**
     * Exporta la boleta de un cliente y mes a un archivo PDF.
     */
    fun exportarPdfClienteMes(rutCliente: String, anio: Int, mes: Int): ByteArray {
        // 1. Obtiene la instancia del servicio de PDF para la plataforma actual.
        // KMP se encarga de darnos la implementación correcta (DesktopPdfService en este caso).
        val pdfService = createPdfService()

        // 2. Asegura que la boleta exista o la crea
        val boleta = emitirBoletaMensual(rutCliente, anio, mes)
        val cliente = clientes.obtenerPorRut(rutCliente)
            ?: throw IllegalStateException("Cliente no encontrado después de emitir boleta.")

        // 3. Llama al servicio de PDF para generar el archivo
        return pdfService.generarBoletasPDF(
            boletas = listOf(boleta),
            clientes = mapOf(cliente.rut to cliente)
        )
    }
}