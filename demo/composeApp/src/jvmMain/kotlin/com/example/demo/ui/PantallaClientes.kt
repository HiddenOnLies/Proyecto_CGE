@file:OptIn(ExperimentalTime::class)
package com.example.demo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo.AppContainer
import com.example.demo.dominio.Cliente
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun PantallaClientes(appContainer: AppContainer, onClientClick: (String) -> Unit) {
    var listaClientes by remember { mutableStateOf(emptyList<Cliente>()) }
    var nombreInput by remember { mutableStateOf("") }
    var rutInput by remember { mutableStateOf("") }
    var direccionInput by remember { mutableStateOf("") }
    var showAddClientForm by remember { mutableStateOf(false) }
    var filtro by remember { mutableStateOf("") }
    var clienteAEliminar by remember { mutableStateOf<Cliente?>(null) }

    fun refrescarClientes() { listaClientes = appContainer.clienteRepo.listar() }
    LaunchedEffect(Unit) { refrescarClientes() }

    fun agregarCliente() {
        if (nombreInput.isNotBlank() && rutInput.isNotBlank() && direccionInput.isNotBlank()) {
            val nuevoCliente = Cliente(id = rutInput, rut = rutInput, nombre = nombreInput, email = "ejemplo@correo.com", direccionFacturacion = direccionInput, estado = com.example.demo.dominio.EstadoCliente.ACTIVO, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
            appContainer.clienteRepo.crear(nuevoCliente)
            refrescarClientes()
            nombreInput = ""; rutInput = ""; direccionInput = ""
            showAddClientForm = false
        }
    }

    // --- FUNCIÓN ACTUALIZADA ---
    fun eliminarCliente(cliente: Cliente) {
        // Ahora llama al servicio en lugar del repositorio directamente.
        appContainer.boletaService.eliminarClienteCompleto(cliente.rut)
        refrescarClientes()
    }

    val clientesFiltrados = remember(listaClientes, filtro) {
        if (filtro.isBlank()) { listaClientes } else {
            listaClientes.filter { it.nombre.contains(filtro, ignoreCase = true) || it.rut.contains(filtro, ignoreCase = true) }
        }
    }

    if (clienteAEliminar != null) {
        AlertDialog(
            onDismissRequest = { clienteAEliminar = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar al cliente ${clienteAEliminar!!.nombre}? TODOS sus medidores también serán eliminados.") },
            confirmButton = { Button(onClick = { eliminarCliente(clienteAEliminar!!); clienteAEliminar = null }, colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { clienteAEliminar = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddClientForm = !showAddClientForm }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cliente")
            }
        },
        topBar = {
            Column {
                TopAppBar(title = { Text("Gestión de Clientes") }, backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp)
                Divider()
            }
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Filtrar por nombre o RUT") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") }
            )
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(visible = showAddClientForm) {
                Card(elevation = 8.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Registrar Nuevo Cliente", style = MaterialTheme.typography.h6)
                        OutlinedTextField(nombreInput, { nombreInput = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(rutInput, { rutInput = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(direccionInput, { direccionInput = it }, label = { Text("Dirección de Facturación") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showAddClientForm = false }) { Text("Cancelar") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = ::agregarCliente) { Text("Guardar Cliente") }
                        }
                    }
                }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(clientesFiltrados) { cliente ->
                    Card(elevation = 4.dp) {
                        Row(
                            modifier = Modifier.clickable { onClientClick(cliente.rut) }.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cliente.nombre, style = MaterialTheme.typography.h6)
                                Text("RUT: ${cliente.rut}", style = MaterialTheme.typography.body2)
                            }
                            IconButton(onClick = { clienteAEliminar = cliente }) {
                                Icon(Icons.Default.Delete, "Eliminar Cliente", tint = MaterialTheme.colors.error)
                            }
                        }
                    }
                }
            }
        }
    }
}