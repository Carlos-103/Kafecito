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
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
) {

    fun calcularTotal(): Double {

        return items.sumOf {
            it.calcularSubtotal()
        }
    }
}

class GestorPedidos {

    private val pedidos =
        mutableListOf<Pedido>()

    private var siguienteId = 1

    /**
     * Crea un pedido SIN modificar inventario.
     *
     * Se mantiene para compatibilidad con
     * otros módulos.
     */
    fun crearPedido(
        cliente: String,
        items: List<ItemPedido>
    ): Pedido? {

        return try {

            require(cliente.isNotBlank()) {
                "El cliente no puede estar vacío"
            }

            require(items.isNotEmpty()) {
                "El pedido debe tener al menos un producto"
            }

            val nuevo = Pedido(
                id = siguienteId,
                cliente = cliente,
                items = items.toMutableList()
            )

            pedidos.add(nuevo)

            siguienteId++

            println()
            println(
                "✅ Pedido creado: #${nuevo.id}"
            )

            println(
                "Total: $${"%.2f".format(nuevo.calcularTotal())}"
            )

            nuevo

        } catch (e: Exception) {

            Logger.registrarError(
                "Pedido",
                "Error al crear pedido: ${e.message}"
            )

            println(
                "Error: ${e.message}"
            )

            null
        }
    }

    /**
     * Crea un pedido y controla automáticamente
     * el inventario.
     *
     * Primero verifica TODO el stock.
     * Solo si todo está disponible se descuenta.
     */
    fun crearPedidoConInventario(
        cliente: String,
        items: List<ItemPedido>,
        inventario: GestorInventario
    ): Pedido? {

        return try {

            require(cliente.isNotBlank()) {
                "El cliente no puede estar vacío"
            }

            require(items.isNotEmpty()) {
                "El pedido debe tener al menos un producto"
            }

            // ========================================================
            // PASO 1
            // VERIFICAR TODO EL STOCK
            // ========================================================

            for (item in items) {

                if (item.cantidad <= 0) {

                    println(
                        "❌ La cantidad de " +
                                "${item.producto.nombre} " +
                                "debe ser mayor que 0."
                    )

                    return null
                }

                val disponible =
                    inventario.consultarStock(
                        item.producto.id
                    )

                if (disponible == 0) {

                    println()
                    println(
                        "❌ PRODUCTO NO DISPONIBLE"
                    )

                    println(
                        "${item.producto.nombre} " +
                                "está agotado."
                    )

                    return null
                }

                if (disponible < item.cantidad) {

                    println()
                    println(
                        "❌ STOCK INSUFICIENTE"
                    )

                    println(
                        "Producto: ${item.producto.nombre}"
                    )

                    println(
                        "Disponible: $disponible"
                    )

                    println(
                        "Solicitado: ${item.cantidad}"
                    )

                    return null
                }
            }

            // ========================================================
            // PASO 2
            // DESCONTAR STOCK
            // ========================================================

            for (item in items) {

                val descuentoCorrecto =
                    inventario.descontarStock(
                        item.producto.id,
                        item.cantidad
                    )

                if (!descuentoCorrecto) {

                    println(
                        "❌ No fue posible actualizar " +
                                "el inventario."
                    )

                    return null
                }
            }

            // ========================================================
            // PASO 3
            // CREAR PEDIDO
            // ========================================================

            val nuevo = Pedido(
                id = siguienteId,
                cliente = cliente,
                items = items.toMutableList()
            )

            pedidos.add(nuevo)

            siguienteId++

            println()
            println("====================================")
            println("          PEDIDO CREADO")
            println("====================================")
            println("Pedido #${nuevo.id}")
            println("Cliente: ${nuevo.cliente}")
            println("------------------------------------")

            nuevo.items.forEach { item ->

                println(
                    "${item.cantidad} x " +
                            "${item.producto.nombre} = " +
                            "$${"%.2f".format(item.calcularSubtotal())}"
                )
            }

            println("------------------------------------")
            println(
                "TOTAL: $${"%.2f".format(nuevo.calcularTotal())}"
            )

            println(
                "Estado: ${nuevo.estado}"
            )

            println("====================================")

            nuevo

        } catch (e: Exception) {

            Logger.registrarError(
                "Pedido",
                "Error al crear pedido con inventario: ${e.message}"
            )

            println(
                "Error: ${e.message}"
            )

            null
        }
    }

    /**
     * Actualiza el estado del pedido.
     */
    fun actualizarEstado(
        idPedido: Int,
        nuevoEstado: EstadoPedido
    ) {

        try {

            val pedido =
                pedidos.find {
                    it.id == idPedido
                }
                    ?: throw NoSuchElementException(
                        "No existe el pedido $idPedido"
                    )

            pedido.estado = nuevoEstado

            println(
                "✅ Pedido #$idPedido actualizado a: " +
                        "$nuevoEstado"
            )

        } catch (e: Exception) {

            Logger.registrarError(
                "Pedido",
                "Error al actualizar estado: ${e.message}"
            )

            println(
                "Error: ${e.message}"
            )
        }
    }

    /**
     * Consulta el estado de un pedido.
     */
    fun consultarEstado(
        idPedido: Int
    ): EstadoPedido? {

        return pedidos.find {
            it.id == idPedido
        }?.estado
    }

    /**
     * Devuelve todos los pedidos.
     */
    fun obtenerPedidos(): List<Pedido> =
        pedidos
}
