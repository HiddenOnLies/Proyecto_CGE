@file:OptIn(ExperimentalTime::class) // Habilita el uso de APIs experimentales de Kotlin (Time).
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

// Punto de entrada de la aplicación de escritorio.
fun main() = application {
    // 1. Crea y "recuerda" la instancia única del contenedor de dependencias (backend).
    val appContainer = remember { AppContainer() }

    // Crea la ventana principal de la aplicación.
    Window(onCloseRequest = ::exitApplication, title = "CGE Gestión") {
        App(appContainer) // Llama a la UI principal y le pasa el contenedor.
    }
}

// Composable principal que define la estructura de la UI.
@Composable
@Preview
fun App(appContainer: AppContainer) {
    // --- ESTADO DE NAVEGACIÓN ---

    // Estado para saber qué pantalla principal se muestra (Clientes o Boletas).
    var currentScreen by remember { mutableStateOf(Screen.CLIENTES) }
    // Estado para la vista maestro/detalle. null = lista, "RUT" = detalle.
    var selectedClientRut by remember { mutableStateOf<String?>(null) }

    // Aplica el tema de Material Design.
    MaterialTheme {
        // Divide la pantalla horizontalmente: Menú lateral | Contenido.
        Row {
            // --- MENÚ LATERAL DE NAVEGACIÓN ---
            NavigationRail {
                // Botón de navegación para "Clientes".
                NavigationRailItem(
                    icon = { Text("CL") },
                    label = { Text("Clientes") },
                    selected = currentScreen == Screen.CLIENTES,
                    onClick = {
                        currentScreen = Screen.CLIENTES
                        // Limpia la selección de detalle al volver al menú principal.
                        selectedClientRut = null
                    }
                )
                // Botón de navegación para "Boletas".
                NavigationRailItem(
                    icon = { Text("BO") },
                    label = { Text("Boletas") },
                    selected = currentScreen == Screen.BOLETAS,
                    onClick = { currentScreen = Screen.BOLETAS }
                )
            }

            // --- ÁREA DE CONTENIDO PRINCIPAL ---
            // Ocupa todo el espacio restante.
            Box(modifier = Modifier.fillMaxSize()) {
                // Decide qué pantalla mostrar.
                when (currentScreen) {
                    // Si la pantalla actual es CLIENTES...
                    Screen.CLIENTES -> {
                        if (selectedClientRut == null) {
                            // Muestra la LISTA (Maestro) si no hay cliente seleccionado.
                            PantallaClientes(appContainer) { rut ->
                                // Al hacer clic en un cliente, guarda su RUT para mostrar el detalle.
                                selectedClientRut = rut
                            }
                        } else {
                            // Muestra el DETALLE si hay un cliente seleccionado.
                            PantallaDetalleCliente(appContainer, selectedClientRut!!) {
                                // La función "onBack" (botón Atrás) limpia la selección
                                // y vuelve a la lista.
                                selectedClientRut = null
                            }
                        }
                    }
                    // Si la pantalla actual es BOLETAS...
                    Screen.BOLETAS -> PantallaBoletas(appContainer)
                }
            }
        }
    }
}