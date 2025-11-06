package com.example.demo.dominio

import kotlinx.serialization.Serializable
@Serializable
enum class EstadoBoleta {
    EMITIDA,
    ENVIADA,
    PAGADA,
    ANULADA
}