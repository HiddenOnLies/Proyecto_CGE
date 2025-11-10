# CGE Gestión - Sistema de Facturación Eléctrica

Este es un sistema de escritorio para la gestión de clientes y consumo eléctrico, desarrollado con **Kotlin Multiplatform** y **Compose for Desktop**. La aplicación permite administrar clientes, sus medidores asociados, registrar consumos y generar boletas de facturación en formato PDF.

## ✨ Características Principales

-   **Gestión de Clientes (CRUD):**
    -   Crear, leer, y eliminar clientes.
    -   Formulario de registro desplegable para una interfaz limpia.
    -   Diálogo de confirmación para evitar eliminaciones accidentales.
-   **Filtrado en Tiempo Real:** Barra de búsqueda para filtrar clientes por nombre o RUT instantáneamente.
-   **Gestión de Medidores (CRUD):**
    -   Asociar múltiples medidores a cada cliente.
    -   Crear medidores Monofásicos o Trifásicos (demostrando herencia).
    -   Modificar el estado de un medidor (Activo/Inactivo) en cualquier momento.
    -   Eliminar medidores con diálogo de confirmación.
-   **Registro de Consumo:** Interfaz para registrar las lecturas mensuales (en kWh) de cada medidor.
-   **Generación de Boletas en PDF:**
    -   Cálculo automático del monto a pagar aplicando polimorfismo para tarifas **Residencial** y **Comercial**.
    -   Exportación a un archivo PDF real y bien formateado.
-   **Persistencia Local:** Todos los datos (clientes, medidores, lecturas) se guardan en archivos locales, persistiendo entre sesiones de la aplicación.
-   **Interfaz de Usuario Moderna:**
    -   Diseño limpio y consistente basado en Material Design.
    -   Selector de tema para cambiar entre **Modo Claro** y **Modo Oscuro**.

## 🛠️ Tecnologías y Librerías Utilizadas

-   **Lenguaje:** Kotlin
-   **Framework:** Kotlin Multiplatform (KMP)
-   **Interfaz de Usuario:** Jetpack Compose for Desktop
-   **Serialización:** `kotlinx.serialization` (para convertir objetos a JSON y viceversa)
-   **Manejo de Fechas:** `kotlin.time`
-   **Generación de PDF:** `OpenPDF`
-   **IDE:** IntelliJ IDEA

## 🏗️ Arquitectura del Software

El proyecto sigue una arquitectura limpia y modular, separando las responsabilidades en capas bien definidas. Esto facilita el mantenimiento, la escalabilidad y la reutilización de código.

-   **Módulo `shared` (El Núcleo):** Contiene toda la lógica de negocio y es 100% independiente de la plataforma.
    -   `dominio`: Define las entidades y reglas de negocio puras (ej. `Cliente`, `Medidor`, `Boleta`). No tiene dependencias externas.
    -   `persistencia` (Interfaces): Define los **contratos** para el almacenamiento de datos (ej. `ClienteRepositorio`). La lógica de negocio solo conoce estas interfaces, no la implementación real.
    -   `servicios`: Orquesta las operaciones de negocio complejas (ej. `BoletaService`), coordinando entre diferentes repositorios.

-   **Módulo `composeApp` (La Plataforma):** Contiene el código específico de la plataforma de escritorio (JVM).
    -   `persistencia` (Implementación): Proporciona las implementaciones concretas de las interfaces de persistencia (ej. `FileStorageDriver`, `ClienteRepoImpl`).
    -   `ui`: Contiene todos los componentes de la interfaz de usuario (`@Composable`) construidos con Compose for Desktop. Estas pantallas interactúan con los servicios del módulo `shared`.

## 📂 Estructura del Proyecto

```
.
├── composeApp/
│   └── src/
│       └── jvmMain/
│           └── kotlin/
│               └── com/example/demo/
│                   ├── ui/              <-- Pantallas de la UI (Compose)
│                   ├── persistencia/    <-- Implementación del FileStorageDriver
│                   ├── main.kt          <-- Punto de entrada de la app Desktop
│                   └── ...
└── shared/
    └── src/
        └── commonMain/
            └── kotlin/
                └── com/example/demo/
                    ├── dominio/       <-- Entidades del negocio (Cliente, Medidor...)
                    ├── persistencia/  <-- Interfaces de repositorios (Contratos)
                    └── servicios/     <-- Lógica de negocio (BoletaService)
```

## 🧠 Lógica Clave Explicada

#### Polimorfismo en el Cálculo de Tarifas

El sistema demuestra un uso clave de polimorfismo. La interfaz `Tarifa` define un método `calcular(kwh: Double)`. Existen dos implementaciones:
-   `TarifaResidencial`: Calcula el total con un cargo fijo simple.
-   `TarifaComercial`: Aplica un recargo adicional antes de calcular el total.

El `BoletaService` simplemente obtiene la tarifa correcta para un cliente y llama a `tarifa.calcular()`, sin necesidad de saber de qué tipo específico es. El sistema ejecuta el cálculo correcto automáticamente.

#### Persistencia Local con `FileStorageDriver`

Los datos se guardan gracias a un `FileStorageDriver` personalizado que:
1.  Recibe un objeto (ej. un `Cliente`).
2.  Usa `kotlinx.serialization` para convertir el objeto en un texto con formato JSON.
3.  Guarda ese texto en un archivo dentro de la carpeta oculta `.cge_gestion_data` en el directorio del usuario.
    El nombre del archivo actúa como clave (ej. `cliente-111-1`). Al leer, se realiza el proceso inverso.

#### Eliminación en Cascada

Para asegurar la integridad de los datos, la eliminación de un cliente es una operación orquestada por el `BoletaService`. La función `eliminarClienteCompleto` se encarga de:
1.  Llamar al `MedidorRepositorio` para que elimine todos los medidores asociados a ese cliente.
2.  Solo después, llamar al `ClienteRepositorio` para eliminar al cliente.

## 🚀 Cómo Empezar

### Prerrequisitos

-   **JDK 17** o superior.
-   **IntelliJ IDEA** (Community o Ultimate) con el plugin de Kotlin.

### Pasos para Ejecutar

1.  **Clonar el repositorio:**
    ```bash
    git clone <URL-del-repositorio>
    ```
2.  **Abrir el proyecto** en IntelliJ IDEA.
3.  **Esperar a que Gradle sincronice** todas las dependencias. Esto puede tardar unos minutos la primera vez.
4.  **Ejecutar la aplicación:** Busca la tarea de Gradle `run` dentro de `composeApp > Tasks > run` y haz doble clic, o busca el ícono de "play" verde al lado de la función `main` en el archivo `composeApp/src/jvmMain/kotlin/main.kt`.

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.