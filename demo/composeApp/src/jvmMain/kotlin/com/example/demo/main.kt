@file:OptIn(ExperimentalTime::class)
package com.example.demo

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.demo.AppContainer
import com.example.demo.ui.PantallaBoletas
import com.example.demo.ui.PantallaClientes
import com.example.demo.ui.PantallaDetalleCliente
import com.example.demo.ui.Screen
import kotlin.time.ExperimentalTime

fun main() = application {
    // 1. Instancia única de nuestro backend
    val appContainer = remember { AppContainer() }

    Window(onCloseRequest = ::exitApplication, title = "CGE Gestión") {
        App(appContainer) // Pasamos el contenedor a nuestra UI principal
    }
}

@Composable
@Preview
fun App(appContainer: AppContainer) {
    // --- ESTADO DE NAVEGACIÓN ---
    var currentScreen by remember { mutableStateOf(Screen.CLIENTES) }
    // Nuevo estado para saber qué cliente está seleccionado. Si es null, mostramos la lista.
    var selectedClientRut by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        Row {
            // --- MENÚ LATERAL DE NAVEGACIÓN ---
            NavigationRail {
                NavigationRailItem(
                    icon = { Text("CL") },
                    label = { Text("Clientes") },
                    selected = currentScreen == Screen.CLIENTES,
                    onClick = {
                        currentScreen = Screen.CLIENTES
                        selectedClientRut = null // Limpia la selección al volver al menú principal
                    }
                )
                NavigationRailItem(
                    icon = { Text("BO") },
                    label = { Text("Boletas") },
                    selected = currentScreen == Screen.BOLETAS,
                    onClick = { currentScreen = Screen.BOLETAS }
                )
            }

            // --- ÁREA DE CONTENIDO PRINCIPAL ---
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    Screen.CLIENTES -> {
                        if (selectedClientRut == null) {
                            // Si no hay cliente seleccionado, muestra la lista
                            PantallaClientes(appContainer) { rut ->
                                selectedClientRut = rut // Al hacer clic en un cliente, guardamos su RUT
                            }
                        } else {
                            // Si hay un cliente seleccionado, muestra la pantalla de detalle
                            PantallaDetalleCliente(appContainer, selectedClientRut!!) {
                                selectedClientRut = null // El botón "Atrás" limpia la selección
                            }
                        }
                    }
                    Screen.BOLETAS -> PantallaBoletas(appContainer)
                }
            }
        }
    }
}