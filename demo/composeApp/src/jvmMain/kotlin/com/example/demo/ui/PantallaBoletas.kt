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
    var rutInput by remember { mutableStateOf("") }
    var anioInput by remember { mutableStateOf("2025") }
    var mesInput by remember { mutableStateOf("11") }
    var mensaje by remember { mutableStateOf("") }

    fun generarYExportarBoleta() {
        val anio = anioInput.toIntOrNull()
        val mes = mesInput.toIntOrNull()
        if (rutInput.isBlank() || anio == null || mes == null) {
            mensaje = "Error: Todos los campos son obligatorios y deben ser válidos."
            return
        }
        try {
            val pdfBytes = appContainer.boletaService.exportarPdfClienteMes(rutInput, anio, mes)
            val path = Paths.get("boleta-$rutInput-$anio-$mes.pdf")
            Files.write(path, pdfBytes)
            mensaje = "¡Éxito! PDF guardado en: ${path.toAbsolutePath()}"
        } catch (e: Exception) {
            mensaje = "Error al generar la boleta: ${e.message}"
            e.printStackTrace()
        }
    }

    // --- NUEVA ESTRUCTURA DE UI CON SCAFFOLD ---
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Generación de Boletas") },
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp
                )
                Divider()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // El formulario ahora está dentro de una Card para consistencia visual
            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Ingresar Datos", style = MaterialTheme.typography.h6)

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
                }
            }

            // Muestra el mensaje de éxito o error
            if (mensaje.isNotBlank()) {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (mensaje.startsWith("Error")) MaterialTheme.colors.error.copy(alpha = 0.2f) else MaterialTheme.colors.secondary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = mensaje,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}