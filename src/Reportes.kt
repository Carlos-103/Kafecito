// ====================================================================
// REPORTES.kt
// ====================================================================

class GestorReportes(
    private val gestorProductos: GestorProductos,
    private val gestorPedidos: GestorPedidos,
    private val gestorInventario: GestorInventario
) {

    /**
     * Muestra un resumen general del sistema
     */
    fun mostrarResumenGeneral() {
        try {
            println()
            println("===== RESUMEN DEL SISTEMA =====")

            val pedidos = gestorPedidos.obtenerPedidos()
            var totalVentas = 0.0
            for (pedido in pedidos) {
                totalVentas += pedido.calcularTotal()
            }

            println("Total de pedidos: ${pedidos.size}")
            println("Total vendido: $${"%.2f".format(totalVentas)}")
            println("Productos registrados: ${gestorProductos.obtenerProductos().size}")

            mostrarProductoMasVendido()
            mostrarValorInventario()
            mostrarProductosAgotados()
            mostrarStockBajo()
            mostrarResumenPorCategoria()
            mostrarPedidosPorEstado()

            println("================================")

        } catch (e: Exception) {
            Logger.registrarError("Reportes", "Error al generar resumen: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    /**
     * Producto con más unidades vendidas, según todos los pedidos.
     */
    private fun mostrarProductoMasVendido() {
        val pedidos = gestorPedidos.obtenerPedidos()

        if (pedidos.isEmpty()) {
            println("\nAún no hay pedidos para calcular el producto más vendido.")
            return
        }
        val nombres = mutableListOf<String>()
        val cantidades = mutableListOf<Int>()

        for (pedido in pedidos) {
            for (item in pedido.items) {
                val nombre = item.producto.nombre
                val indice = nombres.indexOf(nombre)

                if (indice == -1) {
                    nombres.add(nombre)
                    cantidades.add(item.cantidad)
                } else {
                    cantidades[indice] = cantidades[indice] + item.cantidad
                }
            }
        }

        if (nombres.isEmpty()) {
            return
        }

        var indiceMax = 0
        for (i in 1 until cantidades.size) {
            if (cantidades[i] > cantidades[indiceMax]) {
                indiceMax = i
            }
        }

        println("\nProducto más vendido: ${nombres[indiceMax]} (${cantidades[indiceMax]} unidades)")

    }

    /**
     * Valor total del inventario (precio x stock de cada producto).
     */
    private fun mostrarValorInventario() {
        val productos = gestorProductos.obtenerProductos()

        var valorTotal = 0.0
        for (producto in productos) {
            val stock = gestorInventario.consultarStock(producto.id)
            valorTotal += producto.precio * stock
        }

        println("\nValor total del inventario: $${"%.2f".format(valorTotal)}")
    }

    /**
     * Muestra los productos agotados o no disponibles.
     */
    private fun mostrarProductosAgotados() {
        val productos = gestorProductos.obtenerProductos()

        val agotados = mutableListOf<Producto>()
        for (producto in productos) {
            val stock = gestorInventario.consultarStock(producto.id)
            if (!producto.disponible || stock == 0) {
                agotados.add(producto)
            }
        }

        if (agotados.isEmpty()) {
            println("\nProductos agotados: ninguno.")
            return
        }

        println("\n⚠ Productos agotados (${agotados.size}):")
        agotados.forEach { producto ->
            println("- ${producto.nombre} (ID: ${producto.id})")
        }
    }

    /**
     * Muestra los productos con stock bajo.
     */
    private fun mostrarStockBajo(umbral: Int = 5) {
        val productos = gestorProductos.obtenerProductos()

        val stockBajo = mutableListOf<Producto>()
        for (producto in productos) {
            val stock = gestorInventario.consultarStock(producto.id)
            if (stock in 1..umbral) {
                stockBajo.add(producto)
            }
        }
        if (stockBajo.isEmpty()) {
            println("\nProductos con stock bajo: ninguno.")
            return
        }

        println("\n⚠ Productos con stock bajo (${stockBajo.size}):")
        for (producto in stockBajo) {
            val stock = gestorInventario.consultarStock(producto.id)
            println("- ${producto.nombre}: $stock unidades")
        }
    }

    /*
     * Cantidad de productos y stock total agrupados por categoría.
     */
    private fun mostrarResumenPorCategoria() {
        val productos = gestorProductos.obtenerProductos()

        if (productos.isEmpty()) {
            println("\nSin productos registrados para resumir por categoría.")
            return
        }

        val categorias = mutableListOf<String>()
        val cantidadPorCategoria = mutableListOf<Int>()
        val stockPorCategoria = mutableListOf<Int>()

        for (producto in productos) {
            val stock = gestorInventario.consultarStock(producto.id)
            val indice = categorias.indexOf(producto.categoria)

            if (indice == -1) {
                categorias.add(producto.categoria)
                cantidadPorCategoria.add(1)
                stockPorCategoria.add(stock)
            } else {
                cantidadPorCategoria[indice] = cantidadPorCategoria[indice] + 1
                stockPorCategoria[indice] = stockPorCategoria[indice] + stock
            }
        }

        println("\n===== RESUMEN POR CATEGORÍA =====")

        for (i in categorias.indices) {
            println("${categorias[i]}: ${cantidadPorCategoria[i]} producto(s), ${stockPorCategoria[i]} unidad(es) en stock")
        }
    }

    /*
     * Cantidad de pedidos agrupados por su estado actual.
     */
    private fun mostrarPedidosPorEstado() {
        val pedidos = gestorPedidos.obtenerPedidos()

        if (pedidos.isEmpty()) {
            println("\nSin pedidos registrados para resumir por estado.")
            return
        }

        val estados = mutableListOf<EstadoPedido>()
        val cantidadPorEstado = mutableListOf<Int>()

        for (pedido in pedidos) {
            val indice = estados.indexOf(pedido.estado)

            if (indice == -1) {
                estados.add(pedido.estado)
                cantidadPorEstado.add(1)
            } else {
                cantidadPorEstado[indice] = cantidadPorEstado[indice] + 1
            }
        }

        println("\n===== PEDIDOS POR ESTADO =====")

        for (i in estados.indices) {
            println("${estados[i]}: ${cantidadPorEstado[i]} pedido(s)")
        }
    }
}
