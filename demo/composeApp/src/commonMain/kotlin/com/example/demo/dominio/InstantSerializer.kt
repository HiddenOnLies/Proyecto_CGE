@file:OptIn(ExperimentalTime::class)
package com.example.demo.dominio // O com.example.demo.dominio.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Serializador personalizado para la clase kotlin.time.Instant.
 * Permite a kotlinx.serialization convertir un Instant a/desde un String en formato ISO-8601.
 */
object InstantSerializer : KSerializer<Instant> {

    // 1. Describe el tipo de dato: Le decimos que Instant se representará como un String.
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("kotlin.time.Instant", PrimitiveKind.STRING)

    // 2. Lógica para guardar (Serializar): De Objeto Instant -> A String
    override fun serialize(encoder: Encoder, value: Instant) {
        // Convierte el Instant a su representación de texto estándar.
        encoder.encodeString(value.toString())
    }

    // 3. Lógica para leer (Deserializar): De String -> A Objeto Instant
    override fun deserialize(decoder: Decoder): Instant {
        // Lee el texto y usa el método parse de Instant para crear el objeto.
        return Instant.parse(decoder.decodeString())
    }
}