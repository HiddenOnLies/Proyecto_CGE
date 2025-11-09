package com.example.demo.dominio

import kotlinx.serialization.Serializable


// Representa los posibles estados de una boleta dentro del sistema.
@Serializable
enum class EstadoBoleta {
    EMITIDA,
    ENVIADA,
    PAGADA,
    ANULADA
}