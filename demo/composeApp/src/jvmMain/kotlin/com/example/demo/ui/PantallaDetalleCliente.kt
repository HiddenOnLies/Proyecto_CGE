@file:OptIn(ExperimentalTime::class) // Habilita el uso de APIs experimentales de Kotlin (Time).
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

// UI Composable para la pantalla "Detalle" de un cliente.
// Muestra sus medidores y permite registrar lecturas.
@Composable
fun PantallaDetalleCliente(
    container: AppContainer, // Contenedor de dependencias (backend).
    clientRut: String,       // RUT del cliente que se está viendo.
    onBack: () -> Unit         // Función para volver a la pantalla anterior (lista).
) {
    // --- ESTADOS ---
    // Almacena el objeto Cliente cargado.
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    // Almacena la lista de medidores del cliente.
    var medidores by remember { mutableStateOf<List<Medidor>>(emptyList()) }
    // Almacena el medidor seleccionado para registrar lecturas.
    var selectedMedidor by remember { mutableStateOf<Medidor?>(null) }
    // Controla la visibilidad del formulario de "Añadir Medidor".
    var showAddMeterForm by remember { mutableStateOf(false) }
    // Almacena el medidor a eliminar (para el diálogo de confirmación).
    var medidorAEliminar by remember { mutableStateOf<Medidor?>(null) }
    // Campos del formulario "Añadir Medidor".
    var medidorCodigo by remember { mutableStateOf("") }
    var medidorDireccion by remember { mutableStateOf("") }
    var medidorTipo by remember { mutableStateOf("Monofásico") }
    var medidorActivo by remember { mutableStateOf(true) }
    // Campos del formulario "Registrar Lectura".
    var lecturaAnio by remember { mutableStateOf("2025") }
    var lecturaMes by remember { mutableStateOf("11") }
    var lecturaKwh by remember { mutableStateOf("") }

    // --- LÓGICA ---
    // Función para recargar la lista de medidores desde el repo.
    fun refrescarMedidores() { medidores = container.medidorRepo.listarPorCliente(clientRut) }
    // Carga el cliente y sus medidores una vez al entrar a la pantalla.
    LaunchedEffect(clientRut) {
        cliente = container.clienteRepo.obtenerPorRut(clientRut)
        refrescarMedidores()
    }
    // Lógica para guardar un nuevo medidor.
    fun agregarMedidor() {
        if (medidorCodigo.isBlank() || medidorDireccion.isBlank()) return // Validación simple.
        // Crea el tipo de medidor correcto (Polimorfismo).
        val nuevoMedidor = if (medidorTipo == "Monofásico") {
            MedidorMonofasico(id = medidorCodigo, codigo = medidorCodigo, direccionSuministro = medidorDireccion, activo = medidorActivo, potenciaMaxKw = 5.0, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
        } else {
            MedidorTrifasico(id = medidorCodigo, codigo = medidorCodigo, direccionSuministro = medidorDireccion, activo = medidorActivo, potenciaMaxKw = 15.0, factorPotencia = 0.9, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
        }
        // Guarda en el repo y actualiza la UI.
        container.medidorRepo.crear(nuevoMedidor, clientRut)
        refrescarMedidores()
        medidorCodigo = ""; medidorDireccion = "" // Limpia y cierra el formulario.
        showAddMeterForm = false
    }
    // Lógica para eliminar un medidor.
    fun eliminarMedidor(medidor: Medidor) {
        // Si el medidor borrado era el seleccionado, lo deselecciona.
        if (selectedMedidor?.codigo == medidor.codigo) { selectedMedidor = null }
        container.medidorRepo.eliminar(medidor.codigo)
        refrescarMedidores()
    }
    // Lógica para el Switch Activo/Inactivo.
    fun cambiarEstadoMedidor(medidor: Medidor) {
        // Crea una copia del medidor con el estado 'activo' invertido.
        val medidorActualizado = when (medidor) {
            is MedidorMonofasico -> medidor.copy(activo = !medidor.activo)
            is MedidorTrifasico -> medidor.copy(activo = !medidor.activo)
            else -> medidor
        }
        // Guarda la copia actualizada en el repo y refresca la UI.
        container.medidorRepo.actualizar(medidorActualizado)
        refrescarMedidores()
    }
    // Lógica para registrar una lectura de consumo.
    fun registrarLectura() {
        // Se ejecuta solo si hay un medidor seleccionado ('let').
        selectedMedidor?.let { medidor ->
            // Valida los números.
            val anio = lecturaAnio.toIntOrNull() ?: return@let
            val mes = lecturaMes.toIntOrNull() ?: return@let
            val kwh = lecturaKwh.toDoubleOrNull() ?: return@let
            // Crea y guarda la nueva lectura.
            val nuevaLectura = LecturaConsumo(id = "${medidor.codigo}-$anio-$mes", idMedidor = medidor.codigo, anio = anio, mes = mes, kwhLeidos = kwh, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
            container.lecturaRepo.registrar(nuevaLectura)
            lecturaKwh = "" // Limpia el campo kwh.
        }
    }
    // Diálogo de confirmación de borrado de medidor.
    if (medidorAEliminar != null) {
        AlertDialog(
            onDismissRequest = { medidorAEliminar = null }, // Cierra al tocar fuera.
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el medidor ${medidorAEliminar!!.codigo}?") },
            confirmButton = { Button(onClick = { eliminarMedidor(medidorAEliminar!!); medidorAEliminar = null }, colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { medidorAEliminar = null }) { Text("Cancelar") } }
        )
    }

    // --- VISTAS (UI) ---
    Scaffold(
        // Barra superior con título y botón de "Atrás".
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Detalle de: ${cliente?.nombre ?: "Cargando..."}") },
                    // Botón de flecha que ejecuta la función 'onBack'.
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp
                )
                Divider() // Línea divisoria.
            }
        },
        // Botón flotante (+) para mostrar/ocultar el formulario de añadir medidor.
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddMeterForm = !showAddMeterForm }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Medidor")
            }
        }
    ) { paddingValues -> // Contenido principal.
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {

            // Formulario para "Añadir Medidor" (visible condicionalmente).
            AnimatedVisibility(visible = showAddMeterForm) {
                Card(elevation = 8.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Registrar Nuevo Medidor", style = MaterialTheme.typography.h6)
                        OutlinedTextField(medidorCodigo, { medidorCodigo = it }, label = { Text("Código Medidor") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(medidorDireccion, { medidorDireccion = it }, label = { Text("Dirección Medidor") }, modifier = Modifier.fillMaxWidth())
                        // Botón para cambiar el tipo (Monofásico/Trifásico).
                        Button(onClick = { medidorTipo = if (medidorTipo == "Monofásico") "Trifásico" else "Monofásico" }, modifier = Modifier.fillMaxWidth()) { Text("Cambiar Tipo (Actual: $medidorTipo)") }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = medidorActivo, onCheckedChange = { medidorActivo = it })
                            Text("Medidor Activo")
                        }
                        // Botones de acción del formulario.
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showAddMeterForm = false }) { Text("Cancelar") }
                            Button(::agregarMedidor, enabled = medidorCodigo.isNotBlank() && medidorDireccion.isNotBlank()) { Text("Guardar Medidor") }
                        }
                    }
                }
            }

            // Layout principal de dos columnas (Lista | Detalle).
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                // Columna Izquierda: Lista de Medidores.
                Column(Modifier.weight(1f)) {
                    Text("Medidores", style = MaterialTheme.typography.h5)
                    // Lista optimizada de medidores.
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(medidores) { medidor ->
                            // Tarjeta para cada medidor.
                            Card(
                                elevation = 4.dp,
                                modifier = Modifier.fillMaxWidth(),
                                // Cambia el color de fondo si está seleccionado.
                                backgroundColor = if (selectedMedidor?.codigo == medidor.codigo) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.surface
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    // Fila superior: Código, Tipo, Botón Eliminar.
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(medidor.codigo, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                                            Text(medidor.tipo(), style = MaterialTheme.typography.body2) // Método polimórfico.
                                        }
                                        // Botón eliminar (activa el diálogo).
                                        IconButton(onClick = { medidorAEliminar = medidor }) {
                                            Icon(Icons.Default.Delete, "Eliminar Medidor", tint = MaterialTheme.colors.error)
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(medidor.direccionSuministro, style = MaterialTheme.typography.caption) // Dirección.
                                    Spacer(Modifier.height(8.dp))
                                    // Fila inferior: Switch Activo, Botón Seleccionar.
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Switch para cambiar estado (activo/inactivo).
                                            Switch(checked = medidor.activo, onCheckedChange = { cambiarEstadoMedidor(medidor) })
                                            Spacer(Modifier.width(8.dp))
                                            Text(if (medidor.activo) "Activo" else "Inactivo")
                                        }
                                        // Botón para seleccionar este medidor.
                                        TextButton(onClick = { selectedMedidor = medidor }) {
                                            Text(if (selectedMedidor?.codigo == medidor.codigo) "Seleccionado" else "Seleccionar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Columna Derecha: Formulario de Registrar Lectura.
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Solo es visible si hay un medidor seleccionado.
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
                    // Mensaje de ayuda si no hay medidor seleccionado.
                    if (selectedMedidor == null) {
                        Text("Selecciona un medidor de la lista para registrar sus lecturas.", modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }
}