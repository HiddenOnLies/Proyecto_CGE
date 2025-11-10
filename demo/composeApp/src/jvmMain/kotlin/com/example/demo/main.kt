@file:OptIn(ExperimentalTime::class)
package com.example.demo

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.demo.ui.*
import kotlin.time.ExperimentalTime

/**
 * Punto de entrada principal para la app de escritorio.
 * Configura la ventana, el estado del tema y el contenedor de dependencias.
 */
fun main() = application {
    // 1. Estado para controlar si el tema es oscuro (true) o claro (false).
    //    Inicia en modo oscuro por defecto.
    var isDarkTheme by remember { mutableStateOf(true) }

    Window(onCloseRequest = ::exitApplication, title = "CGE Gestión") {
        // 2. Envolvemos toda la app en nuestro tema personalizado.
        //    Le pasamos el estado 'isDarkTheme' para que aplique los colores correctos.
        AppTheme(darkTheme = isDarkTheme) {
            // Surface es un contenedor que aplica el color de fondo/superficie del tema.
            Surface(modifier = Modifier.fillMaxSize()) {
                // 3. Creamos y recordamos la instancia única del backend (AppContainer).
                val appContainer = remember { AppContainer() }

                // 4. Llamamos al Composable principal (App) y le pasamos una lambda (función)
                //    que, al ser llamada, invertirá el estado 'isDarkTheme' (state hoisting).
                App(appContainer = appContainer) {
                    isDarkTheme = !isDarkTheme
                }
            }
        }
    }
}

/**
 * Composable que define la estructura principal de la UI (menú de navegación y contenido).
 * @param appContainer Instancia con todos los servicios y repositorios.
 * @param onThemeChange Función (lambda) que se invoca para cambiar el tema (recibida desde main).
 */
@Composable
@Preview
fun App(appContainer: AppContainer, onThemeChange: () -> Unit) {
    // Estado para la navegación entre pantallas.
    var currentScreen by remember { mutableStateOf(Screen.CLIENTES) }
    // Estado para la navegación maestro/detalle.
    var selectedClientRut by remember { mutableStateOf<String?>(null) }

    // Revisa el tema actual (que fue provisto por AppTheme)
    // para saber si es oscuro o claro.
    val isDark = MaterialTheme.colors.isLight.not()

    // Layout principal (Menú a la izquierda, Contenido a la derecha).
    Row {
        // --- MENÚ LATERAL DE NAVEGACIÓN ---
        NavigationRail(
            // Aplica el color de superficie del tema actual (oscuro o claro).
            backgroundColor = MaterialTheme.colors.surface
        ) {
            // Ítem para la pantalla de Clientes.
            NavigationRailItem(
                icon = { Text("CL") },
                label = { Text("Clientes") },
                selected = currentScreen == Screen.CLIENTES,
                onClick = {
                    currentScreen = Screen.CLIENTES
                    selectedClientRut = null // Limpia la selección al volver al menú.
                }
            )

            // Ítem para la pantalla de Boletas.
            NavigationRailItem(
                icon = { Text("BO") },
                label = { Text("Boletas") },
                selected = currentScreen == Screen.BOLETAS,
                onClick = { currentScreen = Screen.BOLETAS }
            )

            // Un Spacer con 'weight(1f)' empuja los siguientes elementos hacia abajo.
            Spacer(Modifier.weight(1f))

            // --- BOTÓN PARA CAMBIAR EL TEMA ---
            // Al hacer clic, llama a la lambda 'onThemeChange' (que vino desde 'main').
            IconButton(onClick = onThemeChange) {
                Icon(
                    // Muestra el ícono de sol si está oscuro, o de luna si está claro.
                    imageVector = if (isDark) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Cambiar Tema"
                )
            }
            Spacer(Modifier.height(16.dp)) // Espacio inferior.
        }

        // --- ÁREA DE CONTENIDO PRINCIPAL ---
        // Muestra la pantalla correcta según el estado de la navegación.
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                // Lógica de navegación Maestro/Detalle para Clientes.
                Screen.CLIENTES -> {
                    if (selectedClientRut == null) {
                        // Muestra la lista de clientes.
                        PantallaClientes(appContainer) { rut -> selectedClientRut = rut }
                    } else {
                        // Muestra el detalle de un cliente.
                        PantallaDetalleCliente(appContainer, selectedClientRut!!) { selectedClientRut = null }
                    }
                }
                // Muestra la pantalla de boletas.
                Screen.BOLETAS -> PantallaBoletas(appContainer)
            }
        }
    }
}