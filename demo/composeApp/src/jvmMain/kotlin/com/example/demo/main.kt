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
 * Punto de entrada principal para la aplicación de escritorio.
 * Configura la ventana, el estado del tema y el contenedor de dependencias.
 */
fun main() = application {
    // 1. Estado para controlar si el tema es oscuro o claro.
    //    'remember' asegura que el estado sobreviva a las recomposiciones.
    var isDarkTheme by remember { mutableStateOf(true) } // Inicia en modo oscuro por defecto

    Window(onCloseRequest = ::exitApplication, title = "CGE Gestión") {
        // 2. Envolvemos toda la aplicación en nuestro tema personalizado.
        //    Le pasamos el estado actual para que aplique los colores correctos.
        AppTheme(darkTheme = isDarkTheme) {
            // Surface es un contenedor que aplica el color de fondo del tema.
            Surface(modifier = Modifier.fillMaxSize()) {
                // 3. Creamos una instancia de nuestro backend.
                val appContainer = remember { AppContainer() }

                // 4. Llamamos a nuestro Composable principal, pasándole una función
                //    lambda que le permite cambiar el estado del tema.
                App(appContainer = appContainer) {
                    isDarkTheme = !isDarkTheme
                }
            }
        }
    }
}

/**
 * El Composable que define la estructura principal de la UI (menú de navegación y contenido).
 * @param appContainer La instancia con todos los servicios y repositorios.
 * @param onThemeChange Una función que se invoca para cambiar el tema.
 */
@Composable
@Preview
fun App(appContainer: AppContainer, onThemeChange: () -> Unit) {
    // Estado para la navegación
    var currentScreen by remember { mutableStateOf(Screen.CLIENTES) }
    var selectedClientRut by remember { mutableStateOf<String?>(null) }

    // Obtenemos el estado actual del tema desde el contexto de MaterialTheme
    // para decidir qué ícono mostrar (sol o luna).
    val isDark = MaterialTheme.colors.isLight.not()

    Row {
        // --- MENÚ LATERAL DE NAVEGACIÓN ---
        NavigationRail(
            backgroundColor = MaterialTheme.colors.surface
        ) {
            // Ítem para la pantalla de Clientes
            NavigationRailItem(
                icon = { Text("CL") },
                label = { Text("Clientes") },
                selected = currentScreen == Screen.CLIENTES,
                onClick = {
                    currentScreen = Screen.CLIENTES
                    selectedClientRut = null // Limpia la selección al volver
                }
            )

            // Ítem para la pantalla de Boletas
            NavigationRailItem(
                icon = { Text("BO") },
                label = { Text("Boletas") },
                selected = currentScreen == Screen.BOLETAS,
                onClick = { currentScreen = Screen.BOLETAS }
            )

            // Un Spacer con 'weight' empuja los siguientes elementos hacia la parte inferior.
            Spacer(Modifier.weight(1f))

            // --- BOTÓN PARA CAMBIAR EL TEMA ---
            IconButton(onClick = onThemeChange) {
                Icon(
                    imageVector = if (isDark) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Cambiar Tema"
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // --- ÁREA DE CONTENIDO PRINCIPAL ---
        // Muestra la pantalla correcta según el estado de la navegación.
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.CLIENTES -> {
                    if (selectedClientRut == null) {
                        PantallaClientes(appContainer) { rut -> selectedClientRut = rut }
                    } else {
                        PantallaDetalleCliente(appContainer, selectedClientRut!!) { selectedClientRut = null }
                    }
                }
                Screen.BOLETAS -> PantallaBoletas(appContainer)
            }
        }
    }
}