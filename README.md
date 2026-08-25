# KAFECITO - Etapa 2 (esqueleto)

Versión de consola en Kotlin. Cada archivo tiene un dueño y comentarios
`// TODO`-style con lo que falta por hacer.

## Reparto de archivos

| Archivo         | Responsable          | Requerimiento que cubre               |
| --------------- | -------------------- | ------------------------------------- |
| `Producto.kt`   | Persona 1            | Gestión principal (CRUD)              |
| `Inventario.kt` | Persona 2            | Actualización dinámica de datos       |
| `Pedido.kt`     | Persona 3            | Procesamiento / cálculo               |
| `Usuario.kt`    | Persona 4            | Validación de entradas y roles        |
| `Reportes.kt`   | Persona 5            | Visualización de resultados / resumen |
| `Main.kt`       | Persona 5            | Menú por rol, conecta todo            |
| `Logger.kt`     | Compartido por todos | Log de errores en archivo de texto    |

## Cómo trabajar en equipo

1. Cada quien crea su rama con su nombre: `feature-producto-carlos-david`, etc.
2. Trabaja SOLO en su archivo asignado (no toques el de otro sin avisar).
3. Usa `Logger.registrarError("NombreDelModulo", "mensaje")` dentro de tus
   bloques `try/catch`.
4. Cuando termines tu módulo, haz _commit_ y _push_ a tu rama, y avisa en
   el grupo para hacer el merge a `main`.

## Cómo correr el proyecto (en IntelliJ / Android Studio)

1. Crea un proyecto de Kotlin (consola, sin Android todavía).
2. Copia estos archivos a la carpeta `src/`.
3. Corre `Main.kt`.
4. Prueba con los usuarios de ejemplo:
   - `admin1` / `1234`
   - `empleado1` / `1234`
   - `cliente1` / `1234`

## Nota importante

`Main.kt` ya tiene datos de ejemplo (usuarios) para que puedan probar el
programa desde el día uno. Los productos, pedidos e inventario todavía
están vacíos: se llenan usando las opciones del menú, o cada quien puede
agregar datos de prueba dentro de su propio módulo mientras desarrolla.
