@file:OptIn(ExperimentalTime::class) // Habilita el uso de APIs experimentales de Kotlin (Time).
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

// UI Composable para la pantalla "Maestro" (lista) de Clientes.
@Composable
fun PantallaClientes(appContainer: AppContainer, onClientClick: (String) -> Unit) {
    // --- ESTADOS ---
    // Almacena la lista de clientes cargada desde el repositorio.
    var listaClientes by remember { mutableStateOf(emptyList<Cliente>()) }
    // Campos de texto del formulario para agregar un cliente.
    var nombreInput by remember { mutableStateOf("") }
    var rutInput by remember { mutableStateOf("") }
    var direccionInput by remember { mutableStateOf("") }
    // Controla si el formulario de agregar está visible.
    var showAddClientForm by remember { mutableStateOf(false) }
    // Texto del campo de búsqueda/filtro.
    var filtro by remember { mutableStateOf("") }
    // Almacena el cliente que se va a eliminar (para mostrar diálogo de confirmación).
    var clienteAEliminar by remember { mutableStateOf<Cliente?>(null) }

    // --- LÓGICA ---
    // Función para recargar la lista de clientes desde el repositorio.
    fun refrescarClientes() { listaClientes = appContainer.clienteRepo.listar() }
    // Carga la lista inicial de clientes una vez al entrar a la pantalla.
    LaunchedEffect(Unit) { refrescarClientes() }

    // Lógica para agregar un nuevo cliente.
    fun agregarCliente() {
        // 1. Valida que los campos no estén vacíos.
        if (nombreInput.isNotBlank() && rutInput.isNotBlank() && direccionInput.isNotBlank()) {
            // 2. Crea el nuevo objeto Cliente.
            val nuevoCliente = Cliente(
                id = rutInput, rut = rutInput, nombre = nombreInput,
                email = "ejemplo@correo.com", direccionFacturacion = direccionInput,
                estado = com.example.demo.dominio.EstadoCliente.ACTIVO,
                createdAt = Clock.System.now(), updatedAt = Clock.System.now()
            )
            // 3. Guarda el cliente en el repositorio.
            appContainer.clienteRepo.crear(nuevoCliente)
            // 4. Recarga la lista en la UI.
            refrescarClientes()
            // 5. Limpia los campos y cierra el formulario.
            nombreInput = ""; rutInput = ""; direccionInput = ""
            showAddClientForm = false
        }
    }

    // Lógica para eliminar un cliente.
    fun eliminarCliente(cliente: Cliente) {
        // Llama al servicio para borrar al cliente Y todos sus medidores asociados.
        appContainer.boletaService.eliminarClienteCompleto(cliente.rut)
        // Recarga la lista en la UI.
        refrescarClientes()
    }

    // Lista derivada que se filtra automáticamente cuando cambia `listaClientes` o `filtro`.
    val clientesFiltrados = remember(listaClientes, filtro) {
        if (filtro.isBlank()) {
            listaClientes // Muestra todos si el filtro está vacío.
        } else {
            // Filtra por nombre o RUT (ignorando mayúsculas/minúsculas).
            listaClientes.filter {
                it.nombre.contains(filtro, ignoreCase = true) ||
                        it.rut.contains(filtro, ignoreCase = true)
            }
        }
    }

    // --- VISTAS (UI) ---

    // Diálogo de confirmación de borrado.
    // Se muestra solo si `clienteAEliminar` no es nulo.
    if (clienteAEliminar != null) {
        AlertDialog(
            onDismissRequest = { clienteAEliminar = null }, // Cierra si se toca fuera.
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar al cliente ${clienteAEliminar!!.nombre}? TODOS sus medidores también serán eliminados.") },
            // Botón de confirmación (rojo).
            confirmButton = {
                Button(
                    onClick = {
                        eliminarCliente(clienteAEliminar!!); // Ejecuta el borrado.
                        clienteAEliminar = null // Cierra el diálogo.
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) { Text("Eliminar") }
            },
            // Botón de cancelación.
            dismissButton = { TextButton(onClick = { clienteAEliminar = null }) { Text("Cancelar") } }
        )
    }

    // Estructura principal de la pantalla (con barra superior y botón flotante).
    Scaffold(
        // Botón flotante (+) para mostrar/ocultar el formulario de agregar.
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddClientForm = !showAddClientForm }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cliente")
            }
        },
        // Barra superior de la app.
        topBar = {
            Column {
                TopAppBar(title = { Text("Gestión de Clientes") }, backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp)
                Divider() // Línea divisoria.
            }
        }
    ) { paddingValues -> // Contenido principal.
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            // Campo de texto para la barra de búsqueda/filtro.
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Filtrar por nombre o RUT") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") }
            )
            Spacer(Modifier.height(16.dp))

            // Formulario para agregar cliente (aparece/desaparece con animación).
            AnimatedVisibility(visible = showAddClientForm) {
                Card(elevation = 8.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Registrar Nuevo Cliente", style = MaterialTheme.typography.h6)
                        // Campos de texto del formulario.
                        OutlinedTextField(nombreInput, { nombreInput = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(rutInput, { rutInput = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(direccionInput, { direccionInput = it }, label = { Text("Dirección de Facturación") }, modifier = Modifier.fillMaxWidth())
                        // Botones de acción del formulario (Cancelar, Guardar).
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showAddClientForm = false }) { Text("Cancelar") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = ::agregarCliente) { Text("Guardar Cliente") }
                        }
                    }
                }
            }

            // Lista optimizada (LazyColumn) que muestra los clientes.
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Itera sobre la lista *filtrada*.
                items(clientesFiltrados) { cliente ->
                    // Tarjeta individual para cada cliente.
                    Card(elevation = 4.dp) {
                        Row(
                            // Toda la fila es clickeable para navegar al detalle.
                            modifier = Modifier.clickable { onClientClick(cliente.rut) }.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Columna para Nombre y RUT.
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cliente.nombre, style = MaterialTheme.typography.h6)
                                Text("RUT: ${cliente.rut}", style = MaterialTheme.typography.body2)
                            }
                            // Botón de eliminar (ícono de basura) que activa el diálogo.
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