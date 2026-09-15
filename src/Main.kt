// ====================================================================
// MAIN.kt
// ====================================================================


fun main() {
    // Creación de gestores
    val gestorUsuarios =
        GestorUsuarios()

    val gestorProductos =
        GestorProductos()

    val gestorInventario =
        GestorInventario()

    val gestorPedidos =
        GestorPedidos()

    val gestorReportes =
        GestorReportes(
            gestorProductos,
            gestorPedidos,
            gestorInventario
        )
    // Usuarios de prueba
    gestorUsuarios.registrarUsuario(
        "admin1",
        "1234",
        Rol.ADMIN
    )

    gestorUsuarios.registrarUsuario(
        "empleado1",
        "1234",
        Rol.EMPLEADO
    )

    gestorUsuarios.registrarUsuario(
        "cliente1",
        "1234",
        Rol.CLIENTE
    )

    // Productos de prueba
    val capuccino =
        gestorProductos.agregarProducto(
            "Capuccino",
            2.50,
            "Café"
        )

    if (capuccino != null) {

        gestorInventario.registrarStockInicial(
            capuccino.id,
            4
        )
    }

    val cafe =
        gestorProductos.agregarProducto(
            "Café Americano",
            2.00,
            "Café"
        )

    if (cafe != null) {

        gestorInventario.registrarStockInicial(
            cafe.id,
            8
        )
    }

    val pastel =
        gestorProductos.agregarProducto(
            "Pastel de Chocolate",
            3.00,
            "Postre"
        )

    if (pastel != null) {

        gestorInventario.registrarStockInicial(
            pastel.id,
            2
        )
    }

    // ================================================================
    // Bucle principal: login, registro y logout
    // ================================================================
    // Permite volver a la pantalla de bienvenida después de cerrar sesión.
    // El programa solo termina cuando se selecciona "3. Salir".

    var continuarPrograma = true

    while (continuarPrograma) {

        println()
        println("====================================")
        println("        BIENVENIDO A KAFECITO")
        println("====================================")
        println("1. Iniciar sesión")
        println("2. Registrarse (como cliente)")
        println("3. Salir del programa")
        print("Seleccione una opción: ")

        val opcionInicial = readLine()

        when (opcionInicial) {

            "1" -> {

                print("Usuario: ")
                val nombre =
                    readLine().orEmpty()

                print("Contraseña: ")
                val contrasena =
                    readLine().orEmpty()

                val usuario =
                    gestorUsuarios.iniciarSesion(
                        nombre,
                        contrasena
                    )

                if (usuario == null) {

                    // El mensaje de error ya lo imprime iniciarSesion().
                    // Se vuelve al menú de bienvenida.

                } else {

                    // ================================================
                    // Menú según el rol del usuario
                    // ================================================

                    when (usuario.rol) {

                        Rol.ADMIN -> {

                            menuAdmin(
                                gestorProductos,
                                gestorInventario,
                                gestorReportes,
                                usuario
                            )
                        }

                        Rol.EMPLEADO -> {

                            menuEmpleado(
                                gestorPedidos,
                                gestorInventario,
                                usuario
                            )
                        }

                        Rol.CLIENTE -> {

                            menuCliente(
                                gestorProductos,
                                gestorPedidos,
                                gestorInventario,
                                usuario
                            )
                        }
                    }

                    println()
                    println(
                        "👋 Sesión cerrada. Volviendo al menú de bienvenida..."
                    )
                }
            }

            "2" -> {

                print("Nombre de usuario: ")
                val nuevoNombre =
                    readLine().orEmpty()

                print("Contraseña: ")
                val nuevaContrasena =
                    readLine().orEmpty()

                // No se pide rol: todo registro público entra como CLIENTE.
                gestorUsuarios.registrarUsuario(
                    nuevoNombre,
                    nuevaContrasena
                )

                println(
                    "Ahora puedes iniciar sesión con tu nuevo usuario."
                )
            }

            "3" -> {

                continuarPrograma = false

                println(
                    "Gracias por usar Kafecito. ¡Hasta pronto!"
                )
            }

            else -> {

                println(
                    "❌ Opción inválida."
                )
            }
        }
    }
}

// Menú administrador

fun menuAdmin(
    gestorProductos: GestorProductos,
    gestorInventario: GestorInventario,
    gestorReportes: GestorReportes,
    usuario: Usuario
) {

    var salir = false

    while (!salir) {

        println()

        println(
            """
            |========== MENÚ ADMINISTRADOR ==========
            |1. Agregar producto
            |2. Listar productos
            |3. Ver inventario
            |4. Ver resumen / reportes
            |5. Buscar producto por nombre
            |6. Listar solo disponibles
            |7. Listar por categoría
            |8. Marcar producto como agotado
            |9. Marcar producto como disponible
            |10. Agregar stock a un producto
            |0. Salir
            |=========================================
            """.trimMargin()
        )

        when (readLine()) {

            // Agregar producto (con stock inicial)

            "1" -> {

                print("Nombre: ")
                val nombre =
                    readLine().orEmpty()

                print("Precio: ")
                val precio =
                    readLine()
                        ?.toDoubleOrNull()
                        ?: 0.0

                print("Categoría: ")
                val categoria =
                    readLine().orEmpty()

                print("Stock inicial: ")
                val stock =
                    readLine()
                        ?.toIntOrNull()
                        ?: -1

                if (stock < 0) {

                    println(
                        "❌ El stock debe ser 0 o mayor."
                    )

                } else {

                    val producto =
                        gestorProductos.agregarProducto(
                            nombre,
                            precio,
                            categoria
                        )

                    if (producto != null) {

                        gestorInventario.registrarStockInicial(
                            producto.id,
                            stock
                        )
                    }
                }
            }

            // Listar productos (con inventario)

            "2" -> {

                gestorProductos.listarProductos(
                    gestorInventario
                )
            }

            // Inventario
            "3" -> {

                gestorInventario.listarInventario()
            }
            // Reportes

            "4" -> {

                gestorReportes.mostrarResumenGeneral()
            }

            // Buscar por nombre

            "5" -> {

                print("Nombre a buscar: ")
                val nombre = readLine().orEmpty()
                gestorProductos.buscarPorNombre(nombre)
            }

            // Listar disponibles

            "6" -> {

                gestorProductos.listarDisponibles()
            }
            // Listar por categoria
            "7" -> {

                print("Categoría: ")
                val categoria = readLine().orEmpty()
                gestorProductos.listarPorCategoria(categoria)
            }

            // Marcar no disponible

            "8" -> {

                print("ID del producto: ")
                val id = readLine()?.toIntOrNull() ?: -1
                gestorProductos.marcarNoDisponible(id)
            }

            // Marcar disponible

            "9" -> {

                print("ID del producto: ")
                val id = readLine()?.toIntOrNull() ?: -1
                gestorProductos.marcarDisponible(id)
            }

            // Agrega stock (solo ADMIN / EMPLEADO)

            "10" -> {

                if (!usuario.rol.puedeModificarInventario()) {

                    println(
                        "❌ No tienes permiso para modificar el inventario."
                    )

                } else {

                    print("ID del producto: ")
                    val id = readLine()?.toIntOrNull() ?: -1

                    print("Cantidad a agregar: ")
                    val cantidad = readLine()?.toIntOrNull() ?: -1

                    gestorInventario.agregarStock(id, cantidad)
                }
            }

            // Salir

            "0" -> {

                salir = true

                println(
                    "Saliendo del menú administrador..."
                )
            }

            else -> {

                println(
                    "❌ Opción inválida."
                )
            }
        }
    }
}

// Menú empleado

fun menuEmpleado(
    gestorPedidos: GestorPedidos,
    gestorInventario: GestorInventario,
    usuario: Usuario
) {

    var salir = false

    while (!salir) {

        println()

        println(
            """
            |========== MENÚ EMPLEADO ==========
            |1. Ver inventario
            |2. Actualizar estado de un pedido
            |3. Agregar stock a un producto
            |0. Salir
            |===================================
            """.trimMargin()
        )

        when (readLine()) {

            "1" -> {

                gestorInventario.listarInventario()
            }

            "2" -> {

                print("ID de pedido: ")

                val id =
                    readLine()
                        ?.toIntOrNull()
                        ?: -1

                println()
                println(
                    "Estados disponibles:"
                )

                println(
                    "1. EN_PREPARACION"
                )

                println(
                    "2. LISTO"
                )

                println(
                    "3. ENTREGADO"
                )

                print("Seleccione estado: ")

                val opcion =
                    readLine()

                val estado =
                    when (opcion) {

                        "1" ->
                            EstadoPedido.EN_PREPARACION

                        "2" ->
                            EstadoPedido.LISTO

                        "3" ->
                            EstadoPedido.ENTREGADO

                        else -> null
                    }

                if (estado == null) {

                    println(
                        "❌ Estado inválido."
                    )

                } else {

                    gestorPedidos.actualizarEstado(
                        id,
                        estado
                    )
                }
            }

            "3" -> {

                if (!usuario.rol.puedeModificarInventario()) {

                    println(
                        "❌ No tienes permiso para modificar el inventario."
                    )

                } else {

                    print("ID del producto: ")
                    val id = readLine()?.toIntOrNull() ?: -1

                    print("Cantidad a agregar: ")
                    val cantidad = readLine()?.toIntOrNull() ?: -1

                    gestorInventario.agregarStock(id, cantidad)
                }
            }

            "0" -> {

                salir = true
            }

            else -> {

                println(
                    "❌ Opción inválida."
                )
            }
        }
    }
}

// Menú cliente
fun menuCliente(
    gestorProductos: GestorProductos,
    gestorPedidos: GestorPedidos,
    gestorInventario: GestorInventario,
    usuario: Usuario
) {
 
    var salir = false
 
    while (!salir) {
 
        println()
 
        println(
            """
            |============ MENÚ CLIENTE ============
            |1. Ver menú de productos
            |2. Realizar pedido
            |3. Consultar estado de un pedido
            |0. Salir
            |=======================================
            """.trimMargin()
        )
 
        when (readLine()) {

            // Ver menú

            "1" -> {
 
                gestorProductos.listarProductos(
                    gestorInventario
                )
            }
 
            //Realizar pedido
 
            "2" -> {
 
                realizarPedido(
                    gestorProductos,
                    gestorPedidos,
                    gestorInventario,
                    usuario
                )
            }

            // Consultar pedido
 
            "3" -> {
 
                print("ID de pedido: ")
 
                val id =
                    readLine()
                        ?.toIntOrNull()
                        ?: -1
 
                val estado =
                    gestorPedidos.consultarEstado(id)
 
                if (estado == null) {
 
                    println(
                        "❌ No existe el pedido #$id."
                    )
 
                } else {
 
                    println(
                        "Pedido #$id"
                    )
 
                    println(
                        "Estado: $estado"
                    )
                }
            }
 
            "0" -> {
 
                salir = true
            }
 
            else -> {
 
                println(
                    "❌ Opción inválida."
                )
            }
        }
    }
}

// Realizar pedido
 
fun realizarPedido(
    gestorProductos: GestorProductos,
    gestorPedidos: GestorPedidos,
    gestorInventario: GestorInventario,
    usuario: Usuario
) {
 
    val productos =
        gestorProductos.obtenerProductos()
 
    if (productos.isEmpty()) {
 
        println(
            "No hay productos registrados."
        )
 
        return
    }
 
    // Carrito temporal
 
    val carrito =
        mutableListOf<ItemPedido>()
 
    var continuar = true
 
    while (continuar) {
 
        println()
        println(
            "========== PRODUCTOS =========="
        )
 
        productos.forEach { producto ->
 
            val stock =
                gestorInventario.consultarStock(
                    producto.id
                )
 
            if (stock > 0) {
 
                println(
                    "${producto.id}. " +
                            "${producto.nombre} - " +
                            "$${"%.2f".format(producto.precio)} " +
                            "- Disponible: $stock"
                )
 
            } else {
 
                println(
                    "${producto.id}. " +
                            "${producto.nombre} - " +
                            "❌ NO DISPONIBLE"
                )
            }
        }
 
        println()
        println("0. Terminar selección")
 
        print(
            "Ingrese ID del producto: "
        )
 
        val idProducto =
            readLine()
                ?.toIntOrNull()
 
        if (idProducto == 0) {
 
            continuar = false
 
        } else if (idProducto == null) {
 
            println(
                "❌ ID inválido."
            )
 
        } else {
 
            val producto =
                productos.find {
                    it.id == idProducto
                }
 
            if (producto == null) {
 
                println(
                    "❌ No existe un producto con ID $idProducto."
                )
 
            } else {
 
                val stock =
                    gestorInventario.consultarStock(
                        producto.id
                    )
 
                if (stock == 0) {
 
                    println()
                    println(
                        "❌ PRODUCTO NO DISPONIBLE"
                    )
 
                    println(
                        "${producto.nombre} está agotado."
                    )
 
                } else {
 
                    print(
                        "Cantidad de ${producto.nombre}: "
                    )
 
                    val cantidad =
                        readLine()
                            ?.toIntOrNull()
 
                    if (cantidad == null ||
                        cantidad <= 0
                    ) {
 
                        println(
                            "❌ Cantidad inválida."
                        )
 
                    } else if (cantidad > stock) {
 
                        println()
                        println(
                            "❌ No hay suficiente stock."
                        )
 
                        println(
                            "Disponible: $stock unidades."
                        )
 
                    } else {

                        // Buscar si ya esta en el carrito
 
                        val existente =
                            carrito.find {
                                it.producto.id ==
                                        producto.id
                            }
 
                        if (existente != null) {
 
                            val nuevaCantidad =
                                existente.cantidad +
                                        cantidad
 
                            if (nuevaCantidad > stock) {
 
                                println(
                                    "❌ La cantidad total " +
                                            "supera el stock disponible."
                                )
 
                            } else {
 
                                carrito.remove(
                                    existente
                                )
 
                                carrito.add(
                                    ItemPedido(
                                        producto,
                                        nuevaCantidad
                                    )
                                )
 
                                println(
                                    "✅ Cantidad actualizada " +
                                            "en el carrito."
                                )
                            }
 
                        } else {
 
                            carrito.add(
                                ItemPedido(
                                    producto,
                                    cantidad
                                )
                            )
 
                            println(
                                "✅ Producto agregado al carrito."
                            )
                        }
                    }
                }
            }
        }
    }
 
  //Si el carrito esta vacio
 
    if (carrito.isEmpty()) {
 
        println(
            "El carrito está vacío."
        )
 
        return
    }
 
    // Mostrar carrito
 
    println()
    println("====================================")
    println("             CARRITO")
    println("====================================")
 
    carrito.forEach { item ->
 
        println(
            "${item.cantidad} x " +
                    "${item.producto.nombre} = " +
                    "$${"%.2f".format(item.calcularSubtotal())}"
        )
    }
 
    val total =
        carrito.sumOf {
            it.calcularSubtotal()
        }
 
    println("------------------------------------")
 
    println(
        "TOTAL: $${"%.2f".format(total)}"
    )
 
    println("====================================")
 
    // Confirmar pedido
 
    print(
        "¿Desea confirmar el pedido? (S/N): "
    )
 
    val confirmar =
        readLine()
            ?.trim()
            ?.uppercase()
 
    if (confirmar == "S") {
 
        gestorPedidos.crearPedidoConInventario(
            usuario.nombre,
            carrito,
            gestorInventario
        )
 
    } else {
 
        println(
            "Pedido cancelado."
        )
    }
}
 