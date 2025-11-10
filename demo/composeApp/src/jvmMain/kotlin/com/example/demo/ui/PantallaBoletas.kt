package com.example.demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo.AppContainer
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Year // Importamos la clase Year de Java para obtener el año actual

@Composable
fun PantallaBoletas(appContainer: AppContainer) {
    // --- ESTADO DE LOS INPUTS ---
    var rutInput by remember { mutableStateOf("") }
    var anioInput by remember { mutableStateOf(Year.now().value.toString()) } // Inicia con el año actual
    var mesInput by remember { mutableStateOf("11") }
    var mensaje by remember { mutableStateOf("") }

    // --- NUEVOS ESTADOS PARA LA VALIDACIÓN ---
    var isAnioValid by remember { mutableStateOf(true) }
    var isMesValid by remember { mutableStateOf(true) }

    // Obtenemos el año actual una sola vez
    val currentYear = Year.now().value

    // --- LÓGICA DE VALIDACIÓN ---
    // Usamos LaunchedEffect para validar los valores iniciales y cada vez que cambian
    LaunchedEffect(anioInput) {
        val anioNum = anioInput.toIntOrNull()
        isAnioValid = anioNum != null && anioNum <= currentYear
    }

    LaunchedEffect(mesInput) {
        val mesNum = mesInput.toIntOrNull()
        isMesValid = mesNum != null && mesNum in 1..12
    }

    val isFormValid = isAnioValid && isMesValid && rutInput.isNotBlank()

    fun generarYExportarBoleta() {
        // Aunque el botón está deshabilitado, mantenemos esta guarda por seguridad
        if (!isFormValid) {
            mensaje = "Error: Por favor, corrige los campos inválidos."
            return
        }
        try {
            val pdfBytes = appContainer.boletaService.exportarPdfClienteMes(rutInput, anioInput.toInt(), mesInput.toInt())
            val path = Paths.get("boleta-$rutInput-${anioInput}-${mesInput}.pdf")
            Files.write(path, pdfBytes)
            mensaje = "¡Éxito! PDF guardado en: ${path.toAbsolutePath()}"
        } catch (e: Exception) {
            mensaje = "Error al generar la boleta: ${e.message}"
            e.printStackTrace()
        }
    }

    // --- UI CON VALIDACIÓN INTEGRADA ---
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
            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Ingresar Datos", style = MaterialTheme.typography.h6)

                    OutlinedTextField(
                        value = rutInput,
                        onValueChange = { rutInput = it },
                        label = { Text("RUT del Cliente") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // --- CAMPO AÑO CON VALIDACIÓN ---
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = anioInput,
                                onValueChange = { anioInput = it },
                                label = { Text("Año") },
                                isError = !isAnioValid, // El campo se marca en rojo si no es válido
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (!isAnioValid) {
                                Text(
                                    "El año no puede ser futuro.",
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.caption,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }

                        // --- CAMPO MES CON VALIDACIÓN ---
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = mesInput,
                                onValueChange = { mesInput = it },
                                label = { Text("Mes") },
                                isError = !isMesValid, // El campo se marca en rojo si no es válido
                                modifier = Modifier.fillMaxWidth()
                            )
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

                    // --- BOTÓN CON ESTADO HABILITADO/DESHABILITADO ---
                    Button(
                        onClick = ::generarYExportarBoleta,
                        enabled = isFormValid, // El botón solo se activa si todo el formulario es válido
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Generar y Exportar PDF")
                    }
                }
            }

            if (mensaje.isNotBlank()) {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (mensaje.startsWith("Error")) MaterialTheme.colors.error.copy(alpha = 0.2f) else MaterialTheme.colors.secondary.copy(alpha = 0.2f)
                ) {
                    Text(text = mensaje, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.body1)
                }
            }
        }
    }
}