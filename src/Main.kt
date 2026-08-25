// ====================================================================
// MAIN.kt  -- Punto de entrada / INTEGRACIÓN de todos los módulos
// Responsable:
// ====================================================================
// Aquí se conectan todos los módulos y se arma el menú según el ROL
// del usuario que inició sesión. NO muevas las clases de otros
// archivos aquí; solo se usan (import implícito, mismo proyecto).

fun main() {
    val gestorUsuarios = GestorUsuarios()
    val gestorProductos = GestorProductos()
    val gestorInventario = GestorInventario()
    val gestorPedidos = GestorPedidos()
    val gestorReportes = GestorReportes(gestorProductos, gestorPedidos, gestorInventario)

    // --- Datos de ejemplo, para probar mientras desarrollan ---
    gestorUsuarios.registrarUsuario("admin1", "1234", Rol.ADMIN)
    gestorUsuarios.registrarUsuario("empleado1", "1234", Rol.EMPLEADO)
    gestorUsuarios.registrarUsuario("cliente1", "1234", Rol.CLIENTE)

    println("=== BIENVENIDO A KAFECITO ===")
    print("Usuario: ")
    val nombre = readLine().orEmpty()
    print("Contraseña: ")
    val contrasena = readLine().orEmpty()

    val usuario = gestorUsuarios.iniciarSesion(nombre, contrasena) ?: return

    when (usuario.rol) {
        Rol.ADMIN -> menuAdmin(gestorProductos, gestorInventario, gestorReportes)
        Rol.EMPLEADO -> menuEmpleado(gestorPedidos, gestorInventario)
        Rol.CLIENTE -> menuCliente(gestorProductos, gestorPedidos, usuario)
    }
}

fun menuAdmin(
    gestorProductos: GestorProductos,
    gestorInventario: GestorInventario,
    gestorReportes: GestorReportes
) {
    var salir = false
    while (!salir) {
        println(
            """
            |
            |--- MENÚ ADMINISTRADOR ---
            |1. Agregar producto
            |2. Listar productos
            |3. Ver resumen / reportes
            |0. Salir
            """.trimMargin()
        )
        when (readLine()) {
            "1" -> {
                print("Nombre: "); val nombre = readLine().orEmpty()
                print("Precio: "); val precio = readLine()?.toDoubleOrNull() ?: 0.0
                print("Categoría: "); val categoria = readLine().orEmpty()
                gestorProductos.agregarProducto(nombre, precio, categoria)
            }
            "2" -> gestorProductos.listarProductos()
            "3" -> gestorReportes.mostrarResumenGeneral()
            "0" -> salir = true
            else -> println("Opción inválida")
        }
    }
}

fun menuEmpleado(gestorPedidos: GestorPedidos, gestorInventario: GestorInventario) {
    var salir = false
    while (!salir) {
        println(
            """
            |
            |--- MENÚ EMPLEADO ---
            |1. Ver inventario
            |2. Actualizar estado de un pedido
            |0. Salir
            """.trimMargin()
        )
        when (readLine()) {
            "1" -> gestorInventario.listarInventario()
            "2" -> {
                print("ID de pedido: "); val id = readLine()?.toIntOrNull() ?: -1
                println("Estados: EN_PREPARACION, LISTO, ENTREGADO")
                print("Nuevo estado: ")
                val estado = readLine()
                try {
                    gestorPedidos.actualizarEstado(id, EstadoPedido.valueOf(estado ?: ""))
                } catch (e: Exception) {
                    println("Estado inválido")
                }
            }
            "0" -> salir = true
            else -> println("Opción inválida")
        }
    }
}

fun menuCliente(
    gestorProductos: GestorProductos,
    gestorPedidos: GestorPedidos,
    usuario: Usuario
) {
    var salir = false
    while (!salir) {
        println(
            """
            |
            |--- MENÚ CLIENTE ---
            |1. Ver menú de productos
            |2. Consultar estado de un pedido
            |0. Salir
            """.trimMargin()
        )
        when (readLine()) {
            "1" -> gestorProductos.listarProductos()
            "2" -> {
                print("ID de pedido: "); val id = readLine()?.toIntOrNull() ?: -1
                println("Estado: ${gestorPedidos.consultarEstado(id)}")
            }
            "0" -> salir = true
            else -> println("Opción inválida")
        }
    }
}
