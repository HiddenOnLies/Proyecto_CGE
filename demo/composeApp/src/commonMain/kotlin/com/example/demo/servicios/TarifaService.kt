package com.example.demo.servicios

import com.example.demo.dominio.Cliente
import com.example.demo.dominio.Tarifa
import com.example.demo.dominio.TarifaComercial
import com.example.demo.dominio.TarifaResidencial

/**
 * Servicio encargado de la lógica de negocio relacionada con las tarifas.
 */
class TarifaService {

    /**
     * Determina qué tipo de tarifa corresponde a un cliente específico.
     * La lógica de negocio podría basarse en el tipo de cliente, su consumo, etc.
     * Para este caso, simularemos una lógica simple.
     **/
    fun tarifaPara(cliente: Cliente): Tarifa {
        // Lógica de ejemplo: Si la dirección de facturación contiene "empresa" o "local",
        // se asume que es comercial. De lo contrario, es residencial.
        return if (cliente.direccionFacturacion.contains("empresa", ignoreCase = true) ||
            cliente.direccionFacturacion.contains("local", ignoreCase = true)) {
            TarifaComercial(
                cargoFijo = 5000.0,
                precioKwh = 150.0,
                recargoComercial = 2500.0,
                iva = 0.19
            )
        } else {
            TarifaResidencial(
                cargoFijo = 1200.0,
                precioKwh = 120.0,
                iva = 0.19
            )
        }
    }
}