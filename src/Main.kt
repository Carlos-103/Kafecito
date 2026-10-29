// ====================================================================
// MAIN.kt - Kafecito
// Flujo de compra con entrega y pago simulados en memoria.
// ====================================================================

fun main() {
    val gestorUsuarios = GestorUsuarios()
    val gestorProductos = GestorProductos()
    val gestorInventario = GestorInventario()
    val gestorPedidos = GestorPedidos()
    val gestorReportes = GestorReportes(
        gestorProductos,
        gestorPedidos,
        gestorInventario
    )

    // Repositorio de tarjetas simulado en memoria.
    val metodoPagoRepository = MetodoPagoRepository()
    val checkoutService = CheckoutService()

    // Usuarios de prueba
    gestorUsuarios.registrarUsuario("admin1", "1234", Rol.ADMIN)
    gestorUsuarios.registrarUsuario("empleado1", "1234", Rol.EMPLEADO)
    gestorUsuarios.registrarUsuario("cliente1", "1234", Rol.CLIENTE)

    // Productos de prueba
    val capuccino = gestorProductos.agregarProducto("Capuccino", 2.50, "Café")
    if (capuccino != null) gestorInventario.registrarStockInicial(capuccino.id, 4)

    val cafe = gestorProductos.agregarProducto("Café Americano", 2.00, "Café")
    if (cafe != null) gestorInventario.registrarStockInicial(cafe.id, 8)

    val pastel = gestorProductos.agregarProducto("Pastel de Chocolate", 3.00, "Postre")
    if (pastel != null) gestorInventario.registrarStockInicial(pastel.id, 2)

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

        when (readLine()) {
            "1" -> {
                print("Usuario: ")
                val nombre = readLine().orEmpty()
                print("Contraseña: ")
                val contrasena = readLine().orEmpty()

                val usuario = gestorUsuarios.iniciarSesion(nombre, contrasena)
                if (usuario != null) {
                    when (usuario.rol) {
                        Rol.ADMIN -> menuAdmin(gestorProductos, gestorInventario, gestorReportes, usuario)
                        Rol.EMPLEADO -> menuEmpleado(gestorPedidos, gestorInventario, usuario)
                        Rol.CLIENTE -> menuCliente(
                            gestorProductos,
                            gestorPedidos,
                            gestorInventario,
                            usuario,
                            metodoPagoRepository,
                            checkoutService
                        )
                    }
                    println()
                    println("👋 Sesión cerrada. Volviendo al menú de bienvenida...")
                }
            }

            "2" -> {
                print("Nombre de usuario: ")
                val nuevoNombre = readLine().orEmpty()
                print("Contraseña: ")
                val nuevaContrasena = readLine().orEmpty()
                gestorUsuarios.registrarUsuario(nuevoNombre, nuevaContrasena)
                println("Ahora puedes iniciar sesión con tu nuevo usuario.")
            }

            "3" -> {
                continuarPrograma = false
                println("Gracias por usar Kafecito. ¡Hasta pronto!")
            }

            else -> println("❌ Opción inválida.")
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
            "1" -> {
                print("Nombre: ")
                val nombre = readLine().orEmpty()
                print("Precio: ")
                val precio = readLine()?.toDoubleOrNull() ?: 0.0
                print("Categoría: ")
                val categoria = readLine().orEmpty()
                print("Stock inicial: ")
                val stock = readLine()?.toIntOrNull() ?: -1

                if (stock < 0) {
                    println("❌ El stock debe ser 0 o mayor.")
                } else {
                    val producto = gestorProductos.agregarProducto(nombre, precio, categoria)
                    if (producto != null) gestorInventario.registrarStockInicial(producto.id, stock)
                }
            }

            "2" -> gestorProductos.listarProductos(gestorInventario)
            "3" -> gestorInventario.listarInventario()
            "4" -> gestorReportes.mostrarResumenGeneral()
            "5" -> {
                print("Nombre a buscar: ")
                gestorProductos.buscarPorNombre(readLine().orEmpty())
            }
            "6" -> gestorProductos.listarDisponibles()
            "7" -> {
                print("Categoría: ")
                gestorProductos.listarPorCategoria(readLine().orEmpty())
            }
            "8" -> {
                print("ID del producto: ")
                gestorProductos.marcarNoDisponible(readLine()?.toIntOrNull() ?: -1)
            }
            "9" -> {
                print("ID del producto: ")
                gestorProductos.marcarDisponible(readLine()?.toIntOrNull() ?: -1)
            }
            "10" -> {
                if (!usuario.rol.puedeModificarInventario()) {
                    println("❌ No tienes permiso para modificar el inventario.")
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
                println("Saliendo del menú administrador...")
            }
            else -> println("❌ Opción inválida.")
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
            "1" -> gestorInventario.listarInventario()
            "2" -> {
                print("ID de pedido: ")
                val id = readLine()?.toIntOrNull() ?: -1
                println("Estados disponibles:")
                println("1. EN_PREPARACION")
                println("2. LISTO")
                println("3. ENTREGADO")
                print("Seleccione estado: ")

                val estado = when (readLine()) {
                    "1" -> EstadoPedido.EN_PREPARACION
                    "2" -> EstadoPedido.LISTO
                    "3" -> EstadoPedido.ENTREGADO
                    else -> null
                }

                if (estado == null) {
                    println("❌ Estado inválido.")
                } else {
                    gestorPedidos.actualizarEstado(id, estado)
                }
            }
            "3" -> {
                if (!usuario.rol.puedeModificarInventario()) {
                    println("❌ No tienes permiso para modificar el inventario.")
                } else {
                    print("ID del producto: ")
                    val id = readLine()?.toIntOrNull() ?: -1
                    print("Cantidad a agregar: ")
                    val cantidad = readLine()?.toIntOrNull() ?: -1
                    gestorInventario.agregarStock(id, cantidad)
                }
            }
            "0" -> salir = true
            else -> println("❌ Opción inválida.")
        }
    }
}

// Menú cliente
fun menuCliente(
    gestorProductos: GestorProductos,
    gestorPedidos: GestorPedidos,
    gestorInventario: GestorInventario,
    usuario: Usuario,
    metodoPagoRepository: MetodoPagoRepository,
    checkoutService: CheckoutService
) {
    var salir = false
    while (!salir) {
        println(
            """
            |============ MENÚ CLIENTE ============
            |1. Ver menú de productos
            |2. Realizar pedido
            |3. Consultar estado de un pedido
            |4. Gestionar tarjetas guardadas
            |0. Salir
            |=======================================
            """.trimMargin()
        )

        when (readLine()) {
            "1" -> gestorProductos.listarProductos(gestorInventario)
            "2" -> realizarPedido(
                gestorProductos,
                gestorPedidos,
                gestorInventario,
                usuario,
                metodoPagoRepository,
                checkoutService
            )
            "3" -> {
                print("ID de pedido: ")
                val id = readLine()?.toIntOrNull() ?: -1
                val estado = gestorPedidos.consultarEstado(id)
                if (estado == null) {
                    println("❌ No existe el pedido #$id.")
                } else {
                    println("Pedido #$id")
                    println("Estado: $estado")
                }
            }
            "4" -> gestionarTarjetas(metodoPagoRepository)
            "0" -> salir = true
            else -> println("❌ Opción inválida.")
        }
    }
}

// Gestionar métodos de pago simulados: solo se guarda marca y últimos cuatro dígitos.
fun gestionarTarjetas(repository: MetodoPagoRepository) {
    var salir = false
    while (!salir) {
        println()
        println("========== TARJETAS GUARDADAS ==========")
        println("1. Listar tarjetas")
        println("2. Agregar tarjeta de prueba")
        println("3. Eliminar tarjeta")
        println("0. Volver")
        print("Seleccione una opción: ")

        when (readLine()) {
            "1" -> {
                val tarjetas = repository.listar()
                if (tarjetas.isEmpty()) {
                    println("No hay tarjetas guardadas.")
                } else {
                    tarjetas.forEach { println("${it.id}. ${it.obtenerDescripcion()}") }
                }
            }
            "2" -> {
                print("Marca de la tarjeta (Visa, Mastercard, etc.): ")
                val marca = readLine().orEmpty()
                print("Últimos cuatro dígitos: ")
                val ultimos = readLine().orEmpty()
                try {
                    val tarjeta = repository.agregar(marca, ultimos)
                    println("Tarjeta guardada: ${tarjeta.obtenerDescripcion()}")
                    println("No se almacena el número completo de la tarjeta.")
                } catch (e: IllegalArgumentException) {
                    println("❌ No se pudo guardar la tarjeta: ${e.message}")
                }
            }
            "3" -> {
                val tarjetas = repository.listar()
                if (tarjetas.isEmpty()) {
                    println("No hay tarjetas para eliminar.")
                } else {
                    tarjetas.forEach { println("${it.id}. ${it.obtenerDescripcion()}") }
                    print("ID de la tarjeta que desea eliminar: ")
                    val id = readLine()?.toIntOrNull()
                    if (id == null) {
                        println("❌ ID inválido.")
                    } else if (repository.borrar(id)) {
                        println("Tarjeta eliminada.")
                    } else {
                        println("❌ No existe una tarjeta con ese ID.")
                    }
                }
            }
            "0" -> salir = true
            else -> println("❌ Opción inválida.")
        }
    }
}

// Realizar pedido con selección de entrega y método de pago.
fun realizarPedido(
    gestorProductos: GestorProductos,
    gestorPedidos: GestorPedidos,
    gestorInventario: GestorInventario,
    usuario: Usuario,
    metodoPagoRepository: MetodoPagoRepository,
    checkoutService: CheckoutService
) {
    val productos = gestorProductos.obtenerProductos()
    if (productos.isEmpty()) {
        println("No hay productos registrados.")
        return
    }

    val carrito = mutableListOf<ItemPedido>()
    var continuar = true

    while (continuar) {
        println()
        println("========== PRODUCTOS ==========")
        productos.forEach { producto ->
            val stock = gestorInventario.consultarStock(producto.id)
            if (stock > 0) {
                println("${producto.id}. ${producto.nombre} - $${"%.2f".format(producto.precio)} - Disponible: $stock")
            } else {
                println("${producto.id}. ${producto.nombre} - ❌ NO DISPONIBLE")
            }
        }

        println("0. Terminar selección")
        print("Ingrese ID del producto: ")
        val idProducto = readLine()?.toIntOrNull()

        if (idProducto == 0) {
            continuar = false
        } else if (idProducto == null) {
            println("❌ ID inválido.")
        } else {
            val producto = productos.find { it.id == idProducto }
            if (producto == null) {
                println("❌ No existe un producto con ID $idProducto.")
            } else {
                val stock = gestorInventario.consultarStock(producto.id)
                if (stock <= 0) {
                    println("❌ ${producto.nombre} está agotado.")
                } else {
                    print("Cantidad de ${producto.nombre}: ")
                    val cantidad = readLine()?.toIntOrNull()
                    if (cantidad == null || cantidad <= 0) {
                        println("❌ Cantidad inválida.")
                    } else {
                        val existente = carrito.find { it.producto.id == producto.id }
                        val nuevaCantidad = (existente?.cantidad ?: 0) + cantidad
                        if (nuevaCantidad > stock) {
                            println("❌ La cantidad total supera el stock disponible. Disponible: $stock unidades.")
                        } else {
                            if (existente != null) carrito.remove(existente)
                            carrito.add(ItemPedido(producto, nuevaCantidad))
                            println("✅ ${producto.nombre} agregado/actualizado en el carrito.")
                        }
                    }
                }
            }
        }
    }

    if (carrito.isEmpty()) {
        println("El carrito está vacío.")
        return
    }

    println()
    println("====================================")
    println("                CARRITO")
    println("====================================")
    carrito.forEach { item ->
        println("${item.cantidad} x ${item.producto.nombre} = $${"%.2f".format(item.calcularSubtotal())}")
    }
    val total = carrito.sumOf { it.calcularSubtotal() }
    println("------------------------------------")
    println("TOTAL: $${"%.2f".format(total)}")
    println("====================================")

    // La dirección de ejemplo simula una dirección guardada; no se persiste.
    val direccionesGuardadas = listOf(
        DireccionGuardada(1, "Dirección de prueba: Colonia Centro, casa 10")
    )

    println()
    println("Modalidad de entrega:")
    println("1. Recoger en tienda")
    println("2. Entrega a domicilio")
    print("Seleccione modalidad: ")
    val modalidad = when (readLine()?.trim()) {
        "1" -> ModalidadEntrega.RECOGER_EN_TIENDA
        "2" -> ModalidadEntrega.A_DOMICILIO
        else -> {
            println("❌ Modalidad inválida. El pedido no se creó.")
            return
        }
    }

    val direccionSeleccionada = if (modalidad == ModalidadEntrega.A_DOMICILIO) {
        println("Direcciones guardadas:")
        direccionesGuardadas.forEach { println("${it.id}. ${it.descripcion}") }
        print("Seleccione ID de dirección: ")
        val direccionId = readLine()?.toIntOrNull()
        direccionesGuardadas.find { it.id == direccionId } ?: run {
            println("❌ Dirección inválida. El pedido no se creó.")
            return
        }
    } else {
        null
    }

    println()
    println("Método de pago:")
    println("1. Efectivo en tienda")
    println("2. Tarjeta guardada")
    print("Seleccione método de pago: ")
    val opcionPago = readLine()?.trim()

    val tipoPago: TipoPagoCheckout
    val tarjetaSeleccionada: MetodoPago?

    when (opcionPago) {
        "1" -> {
            tipoPago = TipoPagoCheckout.EFECTIVO_EN_TIENDA
            tarjetaSeleccionada = null
        }
        "2" -> {
            var tarjetas = metodoPagoRepository.listar()
            if (tarjetas.isEmpty()) {
                println("No hay tarjetas guardadas.")
                print("¿Desea agregar una tarjeta de prueba ahora? (S/N): ")
                val agregar = readLine()?.trim()?.uppercase()
                if (agregar != "S") {
                    println("Puede elegir efectivo en tienda en un nuevo intento. Pedido no creado.")
                    return
                }

                print("Marca de la tarjeta (Visa, Mastercard, etc.): ")
                val marca = readLine().orEmpty()
                print("Últimos cuatro dígitos: ")
                val ultimos = readLine().orEmpty()
                try {
                    metodoPagoRepository.agregar(marca, ultimos)
                } catch (e: IllegalArgumentException) {
                    println("❌ Datos de tarjeta inválidos: ${e.message}")
                    return
                }
                tarjetas = metodoPagoRepository.listar()
            }

            println("Tarjetas guardadas:")
            tarjetas.forEach { println("${it.id}. ${it.obtenerDescripcion()}") }
            print("Seleccione ID de tarjeta: ")
            val tarjetaId = readLine()?.toIntOrNull()
            val elegida = tarjetas.find { it.id == tarjetaId }
            if (elegida == null) {
                println("❌ Tarjeta inválida. El pedido no se creó.")
                return
            }
            tipoPago = TipoPagoCheckout.TARJETA
            tarjetaSeleccionada = elegida
        }
        else -> {
            println("❌ Método de pago inválido. El pedido no se creó.")
            return
        }
    }

    val resumen = try {
        checkoutService.crearResumen(
            items = carrito.toList(),
            modalidadEntrega = modalidad,
            direccionSeleccionada = direccionSeleccionada,
            tipoPago = tipoPago,
            tarjetaId = tarjetaSeleccionada?.id,
            metodosPagoDisponibles = metodoPagoRepository.listar()
        )
    } catch (e: IllegalArgumentException) {
        println("❌ No se pudo preparar el pedido: ${e.message}")
        return
    }

    println()
    println("========== RESUMEN DEL PEDIDO ==========")
    println("Cliente: ${usuario.nombre}")
    println("Entrega: ${resumen.modalidadEntrega}")
    if (resumen.direccion != null) println("Dirección: ${resumen.direccion.descripcion}")
    println(
        "Pago: " + when (resumen.tipoPago) {
            TipoPagoCheckout.EFECTIVO_EN_TIENDA -> "Efectivo en tienda"
            TipoPagoCheckout.TARJETA -> resumen.metodoPago?.obtenerDescripcion() ?: "Tarjeta"
        }
    )
    println("Productos:")
    resumen.items.forEach { item ->
        println("  ${item.cantidad} x ${item.producto.nombre}: $${"%.2f".format(item.calcularSubtotal())}")
    }
    println("TOTAL: $${"%.2f".format(resumen.total)}")
    println("========================================")
    print("¿Desea confirmar el pedido? (S/N): ")

    val confirmar = readLine()?.trim()?.uppercase()
    if (confirmar != "S") {
        println("Pedido cancelado. No se descontó inventario.")
        return
    }

    val pedidoCreado = gestorPedidos.crearPedidoConCheckout(
        usuario.nombre,
        resumen,
        gestorInventario
    )

    if (pedidoCreado != null) {
        // El carrito solo se vacía cuando el gestor confirma que creó el pedido.
        carrito.clear()
        println("✅ Pedido #${pedidoCreado.id} confirmado correctamente.")
    } else {
        println("❌ No se pudo crear el pedido. No se confirmó la compra.")
        // No limpiar el carrito en caso de error.
    }
}