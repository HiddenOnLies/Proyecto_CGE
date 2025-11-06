@file:OptIn(ExperimentalTime::class)
package com.example.demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo.AppContainer
import com.example.demo.dominio.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Pantalla que muestra los detalles de un cliente específico,
 * permitiendo gestionar sus medidores y registrar lecturas.
 * @param container El contenedor de dependencias de la app.
 * @param clientRut El RUT del cliente a mostrar.
 * @param onBack Una función para volver a la lista de clientes.
 */
@Composable
fun PantallaDetalleCliente(container: AppContainer, clientRut: String, onBack: () -> Unit) {
    // --- ESTADO DE DATOS ---
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var medidores by remember { mutableStateOf<List<Medidor>>(emptyList()) }
    var selectedMedidor by remember { mutableStateOf<Medidor?>(null) }

    // --- ESTADO DE FORMULARIOS ---
    var medidorCodigo by remember { mutableStateOf("") }
    var medidorDireccion by remember { mutableStateOf("") }
    var medidorTipo by remember { mutableStateOf("Monofásico") }
    var lecturaAnio by remember { mutableStateOf("2025") }
    var lecturaMes by remember { mutableStateOf("11") }
    var lecturaKwh by remember { mutableStateOf("") }

    // --- LÓGICA DE CARGA Y ACCIONES ---
    LaunchedEffect(clientRut) {
        cliente = container.clienteRepo.obtenerPorRut(clientRut)
        medidores = container.medidorRepo.listarPorCliente(clientRut)
    }

    fun agregarMedidor() {
        if (medidorCodigo.isBlank() || medidorDireccion.isBlank()) return

        val nuevoMedidor = if (medidorTipo == "Monofásico") {
            MedidorMonofasico(id = medidorCodigo, codigo = medidorCodigo, direccionSuministro = medidorDireccion, activo = true, potenciaMaxKw = 5.0, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
        } else {
            MedidorTrifasico(id = medidorCodigo, codigo = medidorCodigo, direccionSuministro = medidorDireccion, activo = true, potenciaMaxKw = 15.0, factorPotencia = 0.9, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
        }
        container.medidorRepo.crear(nuevoMedidor, clientRut)
        medidores = container.medidorRepo.listarPorCliente(clientRut) // Refresh
        medidorCodigo = ""; medidorDireccion = ""
    }

    fun registrarLectura() {
        selectedMedidor?.let { medidor ->
            val anio = lecturaAnio.toIntOrNull() ?: return@let
            val mes = lecturaMes.toIntOrNull() ?: return@let
            val kwh = lecturaKwh.toDoubleOrNull() ?: return@let

            val nuevaLectura = LecturaConsumo(id = "${medidor.codigo}-$anio-$mes", idMedidor = medidor.codigo, anio = anio, mes = mes, kwhLeidos = kwh, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
            container.lecturaRepo.registrar(nuevaLectura)
            lecturaKwh = "" // Limpia el campo después de registrar
        }
    }

    // --- INTERFAZ DE USUARIO ---
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack) { Text("← Volver a la Lista") }
        Spacer(Modifier.height(16.dp))
        Text("Gestión para Cliente: ${cliente?.nombre ?: "Cargando..."}", style = MaterialTheme.typography.h4)

        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Columna Izquierda: Gestión de Medidores
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Medidores", style = MaterialTheme.typography.h5)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(medidores) { medidor ->
                        Button(
                            onClick = { selectedMedidor = medidor },
                            modifier = Modifier.fillMaxWidth(),
                            colors = if (selectedMedidor?.codigo == medidor.codigo) ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary) else ButtonDefaults.buttonColors()
                        ) {
                            Text("${medidor.codigo} (${medidor.tipo()})")
                        }
                    }
                }
                // Formulario para añadir medidor
                OutlinedTextField(medidorCodigo, { medidorCodigo = it }, label = { Text("Código Medidor") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(medidorDireccion, { medidorDireccion = it }, label = { Text("Dirección Medidor") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { medidorTipo = if (medidorTipo == "Monofásico") "Trifásico" else "Monofásico" }, modifier = Modifier.fillMaxWidth()) { Text("Cambiar Tipo (Actual: $medidorTipo)") }
                Button(::agregarMedidor, enabled = medidorCodigo.isNotBlank() && medidorDireccion.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Agregar Medidor") }
            }

            // Columna Derecha: Gestión de Lecturas
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectedMedidor != null) {
                    Text("Registrar Lectura para ${selectedMedidor?.codigo}", style = MaterialTheme.typography.h5)
                    OutlinedTextField(lecturaAnio, { lecturaAnio = it }, label = { Text("Año") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(lecturaMes, { lecturaMes = it }, label = { Text("Mes") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(lecturaKwh, { lecturaKwh = it }, label = { Text("Consumo en kWh") }, modifier = Modifier.fillMaxWidth())
                    Button(::registrarLectura, enabled = lecturaKwh.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Registrar Lectura") }
                } else {
                    Text("Selecciona un medidor de la lista para registrar sus lecturas.", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}