package com.example.demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo.AppContainer
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Year // Importa Year de Java para validación.

// UI Composable para la pantalla de generación de boletas.
@Composable
fun PantallaBoletas(appContainer: AppContainer) {
    // --- ESTADOS ---
    // Estados para los campos de texto del formulario.
    var rutInput by remember { mutableStateOf("") }
    var anioInput by remember { mutableStateOf(Year.now().value.toString()) } // Inicia con año actual.
    var mesInput by remember { mutableStateOf("11") }
    // Estado para mensajes de éxito/error.
    var mensaje by remember { mutableStateOf("") }

    // --- VALIDACIÓN ---
    // Estados booleanos para la validez de cada campo.
    var isAnioValid by remember { mutableStateOf(true) }
    var isMesValid by remember { mutableStateOf(true) }

    // Obtiene el año actual una vez.
    val currentYear = Year.now().value

    // Valida el año cada vez que 'anioInput' cambia.
    LaunchedEffect(anioInput) {
        val anioNum = anioInput.toIntOrNull()
        // Es válido si es un número Y no es mayor al año actual.
        isAnioValid = anioNum != null && anioNum <= currentYear
    }

    // Valida el mes cada vez que 'mesInput' cambia.
    LaunchedEffect(mesInput) {
        val mesNum = mesInput.toIntOrNull()
        // Es válido si es un número Y está entre 1 y 12.
        isMesValid = mesNum != null && mesNum in 1..12
    }

    // El formulario es válido si todos los campos son válidos.
    val isFormValid = isAnioValid && isMesValid && rutInput.isNotBlank()

    // --- LÓGICA ---
    fun generarYExportarBoleta() {
        // Doble verificación de seguridad.
        if (!isFormValid) {
            mensaje = "Error: Por favor, corrige los campos inválidos."
            return
        }
        try {
            // Genera el PDF en memoria.
            val pdfBytes = appContainer.boletaService.exportarPdfClienteMes(rutInput, anioInput.toInt(), mesInput.toInt())
            // Define la ruta del archivo.
            val path = Paths.get("boleta-$rutInput-${anioInput}-${mesInput}.pdf")
            // Guarda el archivo en disco.
            Files.write(path, pdfBytes)
            mensaje = "¡Éxito! PDF guardado en: ${path.toAbsolutePath()}"
        } catch (e: Exception) {
            mensaje = "Error al generar la boleta: ${e.message}"
            e.printStackTrace()
        }
    }

    // --- UI (VISTA) ---
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
            // Tarjeta del formulario.
            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Ingresar Datos", style = MaterialTheme.typography.h6)

                    // Campo RUT.
                    OutlinedTextField(
                        value = rutInput,
                        onValueChange = { rutInput = it },
                        label = { Text("RUT del Cliente") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Fila para Año y Mes con validaciones visuales.
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Campo AÑO.
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = anioInput,
                                onValueChange = { anioInput = it },
                                label = { Text("Año") },
                                isError = !isAnioValid, // Borde rojo si es inválido.
                                modifier = Modifier.fillMaxWidth()
                            )
                            // Muestra mensaje de error debajo del campo si no es válido.
                            if (!isAnioValid) {
                                Text(
                                    "El año no puede ser futuro.",
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.caption,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }

                        // Campo MES.
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = mesInput,
                                onValueChange = { mesInput = it },
                                label = { Text("Mes") },
                                isError = !isMesValid, // Borde rojo si es inválido.
                                modifier = Modifier.fillMaxWidth()
                            )
                            // Muestra mensaje de error si no es válido.
                            if (!isMesValid) {
                                Text(
                                    "El mes debe ser entre 1 y 12.",
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.caption,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                    }

                    // Botón de acción.
                    Button(
                        onClick = ::generarYExportarBoleta,
                        enabled = isFormValid, // Se deshabilita si el formulario es inválido.
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Generar y Exportar PDF")
                    }
                }
            }

            // Tarjeta de mensajes (éxito/error).
            if (mensaje.isNotBlank()) {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth(),
                    // Color de fondo según el tipo de mensaje.
                    backgroundColor = if (mensaje.startsWith("Error")) MaterialTheme.colors.error.copy(alpha = 0.2f) else MaterialTheme.colors.secondary.copy(alpha = 0.2f)
                ) {
                    Text(text = mensaje, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.body1)
                }
            }
        }
    }
}