package com.example.demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Importa los recursos generados (ej. imágenes) para KMP.
import demo.composeapp.generated.resources.Res
import demo.composeapp.generated.resources.compose_multiplatform

// Composable principal de la aplicación.
@Composable
@Preview
fun App() {
    // Aplica el tema de Material Design 3.
    MaterialTheme {
        // Define un estado booleano para mostrar/ocultar el contenido.
        var showContent by remember { mutableStateOf(false) }

        // Columna principal que ocupa toda la pantalla y centra su contenido.
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer) // Fondo de color.
                .safeContentPadding() // Evita áreas del sistema (notch, etc.).
                .fillMaxSize(), // Ocupa todo el espacio.
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Botón que cambia el estado 'showContent' al hacer clic.
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }

            // Muestra u oculta su contenido (la imagen y el texto) con una animación.
            AnimatedVisibility(showContent) {
                // Obtiene el saludo (ej. "Hello, Desktop!") de la clase compartida.
                val greeting = remember { Greeting().greet() }

                // Columna para el contenido animado.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Muestra la imagen de Compose Multiplatform desde los recursos.
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    // Muestra el texto de saludo.
                    Text("Compose: $greeting")
                }
            }
        }
    }
}