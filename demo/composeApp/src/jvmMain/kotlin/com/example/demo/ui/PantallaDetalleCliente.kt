@file:OptIn(ExperimentalTime::class)
package com.example.demo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.demo.AppContainer
import com.example.demo.dominio.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun PantallaDetalleCliente(container: AppContainer, clientRut: String, onBack: () -> Unit) {
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var medidores by remember { mutableStateOf<List<Medidor>>(emptyList()) }
    var selectedMedidor by remember { mutableStateOf<Medidor?>(null) }
    var showAddMeterForm by remember { mutableStateOf(false) }
    var medidorAEliminar by remember { mutableStateOf<Medidor?>(null) }
    var medidorCodigo by remember { mutableStateOf("") }
    var medidorDireccion by remember { mutableStateOf("") }
    var medidorTipo by remember { mutableStateOf("Monofásico") }
    var medidorActivo by remember { mutableStateOf(true) }
    var lecturaAnio by remember { mutableStateOf("2025") }
    var lecturaMes by remember { mutableStateOf("11") }
    var lecturaKwh by remember { mutableStateOf("") }

    fun refrescarMedidores() { medidores = container.medidorRepo.listarPorCliente(clientRut) }
    LaunchedEffect(clientRut) { cliente = container.clienteRepo.obtenerPorRut(clientRut); refrescarMedidores() }
    fun agregarMedidor() {
        if (medidorCodigo.isBlank() || medidorDireccion.isBlank()) return
        val nuevoMedidor = if (medidorTipo == "Monofásico") {
            MedidorMonofasico(id = medidorCodigo, codigo = medidorCodigo, direccionSuministro = medidorDireccion, activo = medidorActivo, potenciaMaxKw = 5.0, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
        } else {
            MedidorTrifasico(id = medidorCodigo, codigo = medidorCodigo, direccionSuministro = medidorDireccion, activo = medidorActivo, potenciaMaxKw = 15.0, factorPotencia = 0.9, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
        }
        container.medidorRepo.crear(nuevoMedidor, clientRut)
        refrescarMedidores()
        medidorCodigo = ""; medidorDireccion = ""
        showAddMeterForm = false
    }
    fun eliminarMedidor(medidor: Medidor) {
        if (selectedMedidor?.codigo == medidor.codigo) { selectedMedidor = null }
        container.medidorRepo.eliminar(medidor.codigo)
        refrescarMedidores()
    }
    fun cambiarEstadoMedidor(medidor: Medidor) {
        val medidorActualizado = when (medidor) {
            is MedidorMonofasico -> medidor.copy(activo = !medidor.activo)
            is MedidorTrifasico -> medidor.copy(activo = !medidor.activo)
            else -> medidor
        }
        container.medidorRepo.actualizar(medidorActualizado)
        refrescarMedidores()
    }
    fun registrarLectura() {
        selectedMedidor?.let { medidor ->
            val anio = lecturaAnio.toIntOrNull() ?: return@let
            val mes = lecturaMes.toIntOrNull() ?: return@let
            val kwh = lecturaKwh.toDoubleOrNull() ?: return@let
            val nuevaLectura = LecturaConsumo(id = "${medidor.codigo}-$anio-$mes", idMedidor = medidor.codigo, anio = anio, mes = mes, kwhLeidos = kwh, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
            container.lecturaRepo.registrar(nuevaLectura)
            lecturaKwh = ""
        }
    }
    if (medidorAEliminar != null) {
        AlertDialog(
            onDismissRequest = { medidorAEliminar = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el medidor ${medidorAEliminar!!.codigo}?") },
            confirmButton = { Button(onClick = { eliminarMedidor(medidorAEliminar!!); medidorAEliminar = null }, colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { medidorAEliminar = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            // --- CAMBIO CLAVE AQUÍ ---
            Column {
                TopAppBar(
                    title = { Text("Detalle de: ${cliente?.nombre ?: "Cargando..."}") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.surface, // Color base de superficie
                    elevation = 0.dp // Sin elevación para un color plano
                )
                Divider() // Línea divisoria para separación visual
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddMeterForm = !showAddMeterForm }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Medidor")
            }
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            AnimatedVisibility(visible = showAddMeterForm) {
                Card(elevation = 8.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Registrar Nuevo Medidor", style = MaterialTheme.typography.h6)
                        OutlinedTextField(medidorCodigo, { medidorCodigo = it }, label = { Text("Código Medidor") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(medidorDireccion, { medidorDireccion = it }, label = { Text("Dirección Medidor") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { medidorTipo = if (medidorTipo == "Monofásico") "Trifásico" else "Monofásico" }, modifier = Modifier.fillMaxWidth()) { Text("Cambiar Tipo (Actual: $medidorTipo)") }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = medidorActivo, onCheckedChange = { medidorActivo = it })
                            Text("Medidor Activo")
                        }
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showAddMeterForm = false }) { Text("Cancelar") }
                            Button(::agregarMedidor, enabled = medidorCodigo.isNotBlank() && medidorDireccion.isNotBlank()) { Text("Guardar Medidor") }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Columna Izquierda: Lista de Medidores
                Column(Modifier.weight(1f)) {
                    Text("Medidores", style = MaterialTheme.typography.h5)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(medidores) { medidor ->
                            Card(
                                elevation = 4.dp,
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = if (selectedMedidor?.codigo == medidor.codigo) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.surface
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(medidor.codigo, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                                            Text(medidor.tipo(), style = MaterialTheme.typography.body2)
                                        }
                                        IconButton(onClick = { medidorAEliminar = medidor }) {
                                            Icon(Icons.Default.Delete, "Eliminar Medidor", tint = MaterialTheme.colors.error)
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(medidor.direccionSuministro, style = MaterialTheme.typography.caption)
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Switch(checked = medidor.activo, onCheckedChange = { cambiarEstadoMedidor(medidor) })
                                            Spacer(Modifier.width(8.dp))
                                            Text(if (medidor.activo) "Activo" else "Inactivo")
                                        }
                                        TextButton(onClick = { selectedMedidor = medidor }) {
                                            Text(if (selectedMedidor?.codigo == medidor.codigo) "Seleccionado" else "Seleccionar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Columna Derecha: Gestión de Lecturas
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnimatedVisibility(visible = selectedMedidor != null) {
                        Card(elevation = 4.dp) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Registrar Lectura para ${selectedMedidor?.codigo}", style = MaterialTheme.typography.h5)
                                OutlinedTextField(lecturaAnio, { lecturaAnio = it }, label = { Text("Año") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(lecturaMes, { lecturaMes = it }, label = { Text("Mes") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(lecturaKwh, { lecturaKwh = it }, label = { Text("Consumo en kWh") }, modifier = Modifier.fillMaxWidth())
                                Button(::registrarLectura, enabled = lecturaKwh.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Registrar Lectura") }
                            }
                        }
                    }
                    if (selectedMedidor == null) {
                        Text("Selecciona un medidor de la lista para registrar sus lecturas.", modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }
}