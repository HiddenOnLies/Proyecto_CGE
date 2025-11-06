@file:OptIn(ExperimentalTime::class)
package com.example.demo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo.AppContainer
import com.example.demo.dominio.Cliente
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Muestra la lista de clientes y un formulario para añadir nuevos.
 * @param onClientClick Una función que se ejecuta cuando el usuario hace clic en un cliente,
 * pasando el RUT de ese cliente.
 */
@Composable
fun PantallaClientes(appContainer: AppContainer, onClientClick: (String) -> Unit) {
    var listaClientes by remember { mutableStateOf(emptyList<Cliente>()) }
    var nombreInput by remember { mutableStateOf("") }
    var rutInput by remember { mutableStateOf("") }
    var direccionInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        listaClientes = appContainer.clienteRepo.listar()
    }

    fun agregarCliente() {
        if (nombreInput.isNotBlank() && rutInput.isNotBlank() && direccionInput.isNotBlank()) {
            val nuevoCliente = Cliente(
                id = rutInput, rut = rutInput, nombre = nombreInput,
                email = "ejemplo@correo.com", direccionFacturacion = direccionInput,
                estado = com.example.demo.dominio.EstadoCliente.ACTIVO,
                createdAt = Clock.System.now(), updatedAt = Clock.System.now()
            )
            appContainer.clienteRepo.crear(nuevoCliente)
            listaClientes = appContainer.clienteRepo.listar()
            nombreInput = ""; rutInput = ""; direccionInput = ""
        }
    }

    Row(Modifier.fillMaxSize()) {
        // Columna Izquierda: Lista de Clientes
        Column(Modifier.weight(1f).padding(16.dp)) {
            Text("Lista de Clientes", style = MaterialTheme.typography.h5)
            Text("(Haz clic en un cliente para ver sus detalles)")
            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listaClientes) { cliente ->
                    Card(
                        elevation = 4.dp,
                        modifier = Modifier.fillMaxWidth().clickable { onClientClick(cliente.rut) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(cliente.nombre, style = MaterialTheme.typography.h6)
                            Text("RUT: ${cliente.rut}")
                            Text("Dirección: ${cliente.direccionFacturacion}")
                        }
                    }
                }
            }
        }

        // Columna Derecha: Formulario para Agregar Nuevo Cliente
        Column(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Agregar Nuevo Cliente", style = MaterialTheme.typography.h5)
            OutlinedTextField(
                value = nombreInput,
                onValueChange = { nombreInput = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = rutInput,
                onValueChange = { rutInput = it },
                label = { Text("RUT") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = direccionInput,
                onValueChange = { direccionInput = it },
                label = { Text("Dirección de Facturación") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = ::agregarCliente, modifier = Modifier.fillMaxWidth()) {
                Text("Agregar Cliente")
            }
        }
    }
}