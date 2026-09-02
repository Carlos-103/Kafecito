// ====================================================================
// INVENTARIO.kt  -- Módulo de INVENTARIO
// Responsable:
// ====================================================================
// Requerimiento funcional que cubre:
//   5. Actualización dinámica de datos durante la ejecución
//
// Qué tienes que hacer:
//   1. Relacionar el stock con el id de un Producto (ver Producto.kt).
//   2. Implementar descuento de stock al vender.
//   3. Implementar alerta de stock bajo.

class GestorInventario(
    private val stockMinimo: Int = 5
) {

    // ID del producto -> cantidad disponible
    private val stock = mutableMapOf<Int, Int>()

    /**
     * Registra el stock inicial de un producto.
     */
    fun registrarStockInicial(
        idProducto: Int,
        cantidad: Int
    ) {
        try {

            require(idProducto > 0) {
                "El ID del producto debe ser mayor que 0"
            }

            require(cantidad >= 0) {
                "La cantidad no puede ser negativa"
            }

            stock[idProducto] = cantidad

            println(
                "Stock registrado correctamente: " +
                        "producto $idProducto -> $cantidad unidades"
            )

        } catch (e: Exception) {

            Logger.registrarError(
                "Inventario",
                "Error al registrar stock: ${e.message}"
            )

            println("Error: ${e.message}")
        }
    }

    /**
     * Agrega unidades al inventario existente.
     *
     * Ejemplo:
     * Capuccino tiene 4
     * Se agregan 6
     * Nuevo stock = 10
     */
    fun agregarStock(
        idProducto: Int,
        cantidad: Int
    ) {

        try {

            require(idProducto > 0) {
                "El ID del producto debe ser mayor que 0"
            }

            require(cantidad > 0) {
                "La cantidad a agregar debe ser mayor que 0"
            }

            val actual = stock[idProducto] ?: 0

            stock[idProducto] = actual + cantidad

            println(
                "Stock actualizado: producto $idProducto -> " +
                        "${stock[idProducto]} unidades"
            )

        } catch (e: Exception) {

            Logger.registrarError(
                "Inventario",
                "Error al agregar stock: ${e.message}"
            )

            println("Error: ${e.message}")
        }
    }

    /**
     * Consulta el stock disponible.
     *
     * Si el producto no está registrado,
     * devuelve 0.
     */
    fun consultarStock(idProducto: Int): Int {
        return stock[idProducto] ?: 0
    }

    /**
     * Verifica si existe suficiente stock.
     */
    fun hayStock(
        idProducto: Int,
        cantidadSolicitada: Int
    ): Boolean {

        if (cantidadSolicitada <= 0) {
            return false
        }

        return consultarStock(idProducto) >= cantidadSolicitada
    }

    /**
     * Descuenta productos del inventario.
     *
     * Devuelve:
     * true  -> descuento realizado
     * false -> no se pudo descontar
     */
    fun descontarStock(
        idProducto: Int,
        cantidadVendida: Int
    ): Boolean {

        return try {

            require(cantidadVendida > 0) {
                "La cantidad vendida debe ser mayor que 0"
            }

            val actual = stock[idProducto] ?: 0

            // Producto agotado
            if (actual == 0) {

                println(
                    "❌ Producto no disponible. " +
                            "El producto $idProducto está agotado."
                )

                return false
            }

            // No existe suficiente cantidad
            if (actual < cantidadVendida) {

                println(
                    "❌ Stock insuficiente para el producto $idProducto."
                )

                println(
                    "Disponible: $actual unidades"
                )

                println(
                    "Solicitado: $cantidadVendida unidades"
                )

                return false
            }

            // Descontar
            val nuevoStock = actual - cantidadVendida

            stock[idProducto] = nuevoStock

            println(
                "✅ Stock actualizado: producto $idProducto -> " +
                        "$nuevoStock unidades disponibles"
            )

            verificarStockBajo(idProducto)

            true

        } catch (e: Exception) {

            Logger.registrarError(
                "Inventario",
                "Error al descontar stock: ${e.message}"
            )

            println("Error: ${e.message}")

            false
        }
    }

    /**
     * Muestra alerta de stock bajo o agotado.
     */
    private fun verificarStockBajo(
        idProducto: Int
    ) {

        val cantidad = stock[idProducto] ?: return

        when {
            cantidad == 0 -> {

                println(
                    "⚠ Producto $idProducto AGOTADO."
                )

            }

            cantidad <= stockMinimo -> {

                println(
                    "⚠ Alerta: producto $idProducto " +
                            "tiene stock bajo ($cantidad unidades)"
                )
            }
        }
    }

    /**
     * Muestra todo el inventario.
     */
    fun listarInventario() {

        if (stock.isEmpty()) {

            println(
                "No hay inventario registrado."
            )

            return
        }

        println()
        println("========== INVENTARIO ==========")

        stock.forEach { (id, cantidad) ->

            if (cantidad > 0) {

                println(
                    "Producto ID: $id -> " +
                            "$cantidad unidades disponibles"
                )

            } else {

                println(
                    "Producto ID: $id -> " +
                            "❌ NO DISPONIBLE"
                )
            }
        }

        println("================================")
    }
}
