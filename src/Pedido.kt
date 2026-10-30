// ====================================================================
// PEDIDO.kt
// ====================================================================

enum class EstadoPedido {
    EN_PREPARACION,
    LISTO,
    ENTREGADO
}

data class ItemPedido(
    val producto: Producto,
    val cantidad: Int
) {
    fun calcularSubtotal(): Double {
        return producto.precio * cantidad
    }
}

data class Pedido(
    val id: Int,
    val cliente: String,
    val items: MutableList<ItemPedido> = mutableListOf(),
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION,
    val modalidadEntrega: ModalidadEntrega = ModalidadEntrega.RECOGER_EN_TIENDA,
    val direccionEntrega: String? = null,
    val tipoPago: TipoPagoCheckout = TipoPagoCheckout.EFECTIVO_EN_TIENDA,
    val metodoPago: MetodoPago? = null
) {
    fun calcularTotal(): Double {
        return items.sumOf { it.calcularSubtotal() }
    }
}

class GestorPedidos {

    private val pedidos = mutableListOf<Pedido>()
    private var siguienteId = 1

    fun crearPedido(
        cliente: String,
        items: List<ItemPedido>
    ): Pedido? {
        return try {
            require(cliente.isNotBlank()) {
                "El cliente no puede estar vacío."
            }
            require(items.isNotEmpty()) {
                "El pedido debe tener al menos un producto."
            }

            val nuevo = Pedido(
                id = siguienteId,
                cliente = cliente,
                items = items.toMutableList()
            )

            pedidos.add(nuevo)
            siguienteId++

            println("✅ Pedido creado: #${nuevo.id}")
            println("Total: $${"%.2f".format(nuevo.calcularTotal())}")
            nuevo
        } catch (e: Exception) {
            Logger.registrarError(
                "Pedido",
                "Error al crear pedido: ${e.message}"
            )
            println("Error: ${e.message}")
            null
        }
    }

    fun crearPedidoConInventario(
        cliente: String,
        items: List<ItemPedido>,
        inventario: GestorInventario
    ): Pedido? {
        return try {
            require(cliente.isNotBlank()) {
                "El cliente no puede estar vacío."
            }
            require(items.isNotEmpty()) {
                "El pedido debe tener al menos un producto."
            }

            // Validar todas las cantidades y existencias antes de descontar.
            for (item in items) {
                require(item.cantidad > 0) {
                    "La cantidad de ${item.producto.nombre} debe ser mayor que cero."
                }

                val disponible = inventario.consultarStock(item.producto.id)

                require(disponible >= item.cantidad) {
                    "Stock insuficiente para ${item.producto.nombre}. " +
                            "Disponible: $disponible, solicitado: ${item.cantidad}."
                }
            }

            // Descontar existencias.
            for (item in items) {
                check(
                    inventario.descontarStock(
                        item.producto.id,
                        item.cantidad
                    )
                ) {
                    "No fue posible actualizar el inventario."
                }
            }

            val nuevo = Pedido(
                id = siguienteId,
                cliente = cliente,
                items = items.toMutableList()
            )

            pedidos.add(nuevo)
            siguienteId++

            println("====================================")
            println("          PEDIDO CREADO")
            println("====================================")
            println("Pedido #${nuevo.id}")
            println("Cliente: ${nuevo.cliente}")
            println("Total: $${"%.2f".format(nuevo.calcularTotal())}")
            println("Estado: ${nuevo.estado}")
            println("====================================")

            nuevo
        } catch (e: Exception) {
            Logger.registrarError(
                "Pedido",
                "Error al crear pedido con inventario: ${e.message}"
            )
            println("Error: ${e.message}")
            null
        }
    }

    /**
     * Crea un pedido con la información validada del checkout.
     * Comprueba el inventario antes de descontar y trata de restaurarlo
     * si ocurre un error durante la creación.
     */
    fun crearPedidoConCheckout(
        cliente: String,
        resumen: ResumenCheckout,
        inventario: GestorInventario
    ): Pedido? {

        val cantidadesPorProducto = resumen.items
            .groupBy { it.producto.id }
            .mapValues { (_, lineas) ->
                lineas.sumOf { it.cantidad }
            }

        val descuentosRealizados = mutableListOf<Pair<Int, Int>>()

        return try {
            require(cliente.isNotBlank()) {
                "El cliente no puede estar vacío."
            }
            require(resumen.items.isNotEmpty()) {
                "El pedido debe contener productos."
            }
            require(resumen.items.all { it.cantidad > 0 }) {
                "Todas las cantidades deben ser mayores que cero."
            }
            require(resumen.items.all { it.producto.precio >= 0.0 }) {
                "El precio de los productos no puede ser negativo."
            }

            when (resumen.modalidadEntrega) {
                ModalidadEntrega.RECOGER_EN_TIENDA -> {
                    require(resumen.direccion == null) {
                        "La recogida en tienda no debe incluir dirección."
                    }
                }

                ModalidadEntrega.A_DOMICILIO -> {
                    require(resumen.direccion != null) {
                        "Debe seleccionar una dirección guardada."
                    }
                }
            }

            when (resumen.tipoPago) {
                TipoPagoCheckout.EFECTIVO_EN_TIENDA -> {
                    require(resumen.metodoPago == null) {
                        "El pago en efectivo no debe incluir una tarjeta."
                    }
                }

                TipoPagoCheckout.TARJETA -> {
                    require(resumen.metodoPago != null) {
                        "Debe seleccionar una tarjeta."
                    }
                }
            }

            // Verificar el stock total por producto.
            for ((idProducto, cantidadSolicitada) in cantidadesPorProducto) {
                val disponible = inventario.consultarStock(idProducto)
                val nombre = resumen.items
                    .first { it.producto.id == idProducto }
                    .producto.nombre

                require(disponible >= cantidadSolicitada) {
                    "Stock insuficiente para $nombre. " +
                            "Disponible: $disponible, solicitado: $cantidadSolicitada."
                }
            }

            // Descontar una vez por producto.
            for ((idProducto, cantidadSolicitada) in cantidadesPorProducto) {
                check(
                    inventario.descontarStock(
                        idProducto,
                        cantidadSolicitada
                    )
                ) {
                    "No se pudo descontar el inventario del producto $idProducto."
                }

                descuentosRealizados.add(idProducto to cantidadSolicitada)
            }

            val nuevo = Pedido(
                id = siguienteId,
                cliente = cliente.trim(),
                items = resumen.items.toMutableList(),
                modalidadEntrega = resumen.modalidadEntrega,
                direccionEntrega = resumen.direccion?.descripcion,
                tipoPago = resumen.tipoPago,
                metodoPago = resumen.metodoPago
            )

            pedidos.add(nuevo)
            siguienteId++

            println()
            println("====================================")
            println("             PEDIDO CREADO")
            println("====================================")
            println("Pedido #${nuevo.id}")
            println("Cliente: ${nuevo.cliente}")
            println("Entrega: ${nuevo.modalidadEntrega}")

            nuevo.direccionEntrega?.let {
                println("Dirección: $it")
            }

            println("Pago: ${nuevo.tipoPago}")

            nuevo.metodoPago?.let {
                println("Tarjeta: ${it.obtenerDescripcion()}")
            }

            println("------------------------------------")

            nuevo.items.forEach { item ->
                println(
                    "${item.cantidad} x ${item.producto.nombre} = " +
                            "$${"%.2f".format(item.calcularSubtotal())}"
                )
            }

            println("------------------------------------")
            println("TOTAL: $${"%.2f".format(nuevo.calcularTotal())}")
            println("Estado: ${nuevo.estado}")
            println("====================================")

            nuevo
        } catch (e: Exception) {
            // Intentar restaurar el stock descontado.
            descuentosRealizados.asReversed().forEach { (idProducto, cantidad) ->
                try {
                    inventario.agregarStock(idProducto, cantidad)
                } catch (errorRestauracion: Exception) {
                    Logger.registrarError(
                        "Pedido",
                        "No se pudo restaurar el stock del producto " +
                                "$idProducto: ${errorRestauracion.message}"
                    )
                }
            }

            Logger.registrarError(
                "Pedido",
                "Error al crear pedido desde checkout: ${e.message}"
            )
            println("No se pudo crear el pedido: ${e.message}")
            null
        }
    }

    fun actualizarEstado(
        idPedido: Int,
        nuevoEstado: EstadoPedido
    ) {
        try {
            val pedido = pedidos.find { it.id == idPedido }
                ?: throw NoSuchElementException(
                    "No existe el pedido $idPedido."
                )

            pedido.estado = nuevoEstado
            println("✅ Pedido #$idPedido actualizado a: $nuevoEstado")
        } catch (e: Exception) {
            Logger.registrarError(
                "Pedido",
                "Error al actualizar estado: ${e.message}"
            )
            println("Error: ${e.message}")
        }
    }

    fun consultarEstado(idPedido: Int): EstadoPedido? {
        return pedidos.find { it.id == idPedido }?.estado
    }

    fun obtenerPedidos(): List<Pedido> {
        return pedidos.toList()
    }
}
