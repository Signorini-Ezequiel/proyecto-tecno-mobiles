# SuperAhorro

SuperAhorro es una aplicación Android para registrar compras de supermercado, consultar historial, ver detalle de tickets y revisar estadísticas simples de gasto mensual.

## Integrantes

- Matias Haron Signorini

## Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel
- StateFlow
- SharedPreferences para persistencia de sesión y preferencias

## Arquitectura utilizada

El proyecto sigue una arquitectura MVVM simple:

- `data`: modelos, datos simulados y persistencia local de sesión.
- `viewmodel`: estado de pantallas y reglas de validación.
- `ui/screens`: pantallas Compose.
- `navigation`: rutas y navegación principal.
- `ui/theme`: colores, tipografía y tema visual.

Para la segunda entrega se separaron responsabilidades de compras en ViewModels más específicos:

- `PurchaseListViewModel`: listado y consulta de compras.
- `NewPurchaseViewModel`: estado temporal, formulario, validaciones y guardado de nueva compra.
- `PurchaseRepository`: fuente en memoria compartida para mantener el comportamiento actual.

## Instrucciones de ejecución

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Seleccionar un emulador o dispositivo físico.
4. Ejecutar la configuración `app`.

También se puede compilar desde terminal:

```bash
./gradlew :app:compileDebugKotlin
```

En Windows:

```bat
gradlew.bat :app:compileDebugKotlin
```

## Estructura de carpetas

```text
app/src/main/java/com/undef/superahorro/haronsignorini
├── data
├── navigation
├── ui
│   ├── screens
│   └── theme
└── viewmodel

app/src/main/res
├── values
└── values-en
```

## Capturas

Las capturas se documentan en [docs/screenshots.md](docs/screenshots.md).

Placeholder:

- Pantalla de inicio
- Nueva compra
- Agregar productos
- Historial
- Estadísticas
- Perfil / configuración

## Funcionalidades implementadas

- Splash inicial con validación de sesión.
- Login, registro y cuentas mock.
- Persistencia de sesión.
- Cambio de nombre de usuario.
- Cambio de contraseña.
- Modo oscuro persistente.
- Alta de compra.
- Alta, edición y eliminación temporal de productos dentro de una compra.
- Listado de compras.
- Detalle de compra.
- Historial agrupado por mes.
- Estadísticas mensuales con gráfico de torta.
- Internacionalización en español e inglés mediante resources.

## Requisitos cumplidos de la primera entrega

- Jetpack Compose: todas las pantallas están construidas con Composables.
- Navegación: se usa Navigation Compose con rutas centralizadas.
- StateFlow: el estado de compras se expone con `StateFlow`.
- ViewModels: la lógica de auth, compras, nueva compra y settings vive en ViewModels.
- Componentes reutilizables: existen componentes compartidos para campos, tarjetas, tablas, filas y layout.
- Persistencia de sesión: se usa `SharedPreferences` mediante `SessionManager`.

