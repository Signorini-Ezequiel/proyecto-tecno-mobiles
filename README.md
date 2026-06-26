# SuperAhorro

SuperAhorro es una aplicación Android para registrar compras de supermercado, consultar historial, ver detalle de tickets, adjuntar foto del ticket y revisar estadísticas simples de gasto mensual.

## Integrantes

- Matías Haron
- Ezequiel Signorini

## Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel
- StateFlow
- Hilt
- Room
- Retrofit
- Coil
- SharedPreferences para persistencia de sesión y preferencias

## Arquitectura utilizada

El proyecto sigue una arquitectura MVVM simple:

- `data`: modelos, persistencia local, DAOs y clientes remotos.
- `di`: proveedores de dependencias de Hilt.
- `viewmodel`: estado de pantallas y reglas de validación.
- `ui/screens`: pantallas Compose.
- `navigation`: rutas y navegación principal.
- `ui/theme`: colores, tipografía y tema visual.

Las compras se guardan localmente con Room mediante `PurchaseRepository`, una clase inyectada con Hilt. La integración remota usa Retrofit para sincronizar compras con una API de prueba.

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

Para generar APK debug:

```bat
gradlew.bat :app:assembleDebug
```

El APK debug generado por Gradle se encuentra en `app/build/outputs/apk/debug/app-debug.apk`.

Además, el repositorio incluye una copia lista para instalar en la raíz del proyecto:

- `SuperAhorro-debug.apk`

## Estructura de carpetas

```text
app/src/main/java/com/undef/superahorro/haronsignorini
├── data
├── di
├── navigation
├── ui
│   ├── components
│   ├── screens
│   └── theme
├── util
└── viewmodel

app/src/main/res
├── values
└── values-en
```

## Capturas y demo

- Capturas de referencia / demo: [docs/screenshots.md](docs/screenshots.md)
- Video o GIF de referencia / demo: [docs/demo-video.md](docs/demo-video.md)
- APK debug listo para usar en la raíz del proyecto: `SuperAhorro-debug.apk`
- APK debug generado por Gradle: `app/build/outputs/apk/debug/app-debug.apk`

Las capturas y el material audiovisual se consideran material de demo para mostrar el flujo principal de la app. No forman parte de la lógica de negocio ni de la persistencia del sistema.

## Funcionalidades implementadas

- Splash inicial con validación de sesión.
- Login, registro y cuentas mock.
- Persistencia de sesión.
- Cambio de nombre de usuario.
- Cambio de contraseña.
- Modo oscuro persistente.
- Alta de compra.
- Alta, edición y eliminación temporal de productos dentro de una compra.
- Foto del ticket desde cámara o galería.
- Listado de compras.
- Detalle de compra.
- Historial agrupado por mes.
- Estadísticas mensuales con gráfico de torta.
- Persistencia local de compras con Room.
- Integración con API mediante Retrofit para sincronizar compras.
- Carga de imágenes con Coil.
- Internacionalización en español e inglés mediante resources.

## Requisitos cumplidos

- Jetpack Compose: todas las pantallas están construidas con Composables.
- Navegación: se usa Navigation Compose con rutas centralizadas.
- StateFlow: el estado de compras se expone con `StateFlow`.
- ViewModels: la lógica de auth, compras, nueva compra, sincronización y settings vive en ViewModels.
- Inyección de dependencias: Hilt provee base de datos, DAO, Retrofit y repositorios.
- Persistencia local: Room guarda compras y productos.
- Persistencia de sesión: se usa `SharedPreferences` mediante `SessionManager`.
