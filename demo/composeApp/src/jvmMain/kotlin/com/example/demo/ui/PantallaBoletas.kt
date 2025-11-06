package com.example.demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo.AppContainer
import java.nio.file.Files
import java.nio.file.Paths

@Composable
fun PantallaBoletas(appContainer: AppContainer) {
    // --- ESTADO ---
    var rutInput by remember { mutableStateOf("") }
    var anioInput by remember { mutableStateOf("2025") }
    var mesInput by remember { mutableStateOf("11") }
    var mensaje by remember { mutableStateOf("") }

    // --- LÓGICA ---
    fun generarYExportarBoleta() {
        val anio = anioInput.toIntOrNull()
        val mes = mesInput.toIntOrNull()

        if (rutInput.isBlank() || anio == null || mes == null) {
            mensaje = "Error: Todos los campos son obligatorios y deben ser válidos."
            return
        }

        try {
            // 1. Llama al servicio del módulo shared para obtener los bytes del PDF
            val pdfBytes = appContainer.boletaService.exportarPdfClienteMes(rutInput, anio, mes)

            // 2. Usa APIs de la JVM para guardar el archivo en el disco
            val path = Paths.get("boleta-$rutInput-$anio-$mes.pdf")
            Files.write(path, pdfBytes)

            mensaje = "¡Éxito! PDF guardado en: ${path.toAbsolutePath()}"

        } catch (e: Exception) {
            mensaje = "Error al generar la boleta: ${e.message}"
            e.printStackTrace()
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Generar y Exportar Boleta", style = MaterialTheme.typography.h4)

        OutlinedTextField(
            value = rutInput,
            onValueChange = { rutInput = it },
            label = { Text("RUT del Cliente") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = anioInput,
                onValueChange = { anioInput = it },
                label = { Text("Año") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = mesInput,
                onValueChange = { mesInput = it },
                label = { Text("Mes") },
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = ::generarYExportarBoleta,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Generar y Exportar PDF")
        }

        if (mensaje.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = mensaje,
                style = if (mensaje.startsWith("Error")) MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
                else MaterialTheme.typography.body1
            )
        }
    }
}