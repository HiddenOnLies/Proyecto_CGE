package com.example.demo.persistencia

import com.example.demo.dominio.Boleta

/**
 * Define el contrato para el repositorio de Boleta.
 * Abstrae el acceso a los datos, permitiendo que los servicios
 * dependan de esta interfaz y no de una implementación concreta.
 */
interface BoletaRepositorio {

    /**
     * Guarda o actualiza una boleta.
     */
    fun guardar(boleta: Boleta): Boleta

    /**
     * Obtiene una boleta específica por RUT, año y mes.
     * Devuelve null si no existe.
     */
    fun obtener(rutCliente: String, anio: Int, mes: Int): Boleta?

    /**
     * Lista todas las boletas de un cliente.
     * Devuelve una lista vacía si no hay.
     */
    fun listarPorCliente(rutCliente: String): List<Boleta>
}