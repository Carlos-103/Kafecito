// ====================================================================
// REPORTES.kt  -- Módulo de REPORTES
// ====================================================================


class GestorReportes(
    private val gestorProductos: GestorProductos,
    private val gestorPedidos: GestorPedidos,
    private val gestorInventario: GestorInventario
) {

    /**
     * Resumen general: ventas, productos, inventari y pedidos, todo en un solo reporte.
     */
    fun mostrarResumenGeneral() {
        try {
            println()
            println("===== RESUMEN DEL SISTEMA =====")

            val pedidos = gestorPedidos.obtenerPedidos()
            val totalVentas = pedidos.sumOf { it.calcularTotal() }

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

        val ventasPorProducto = mutableMapOf<String, Int>()

        pedidos.forEach { pedido ->
            pedido.items.forEach { item ->
                val nombre = item.producto.nombre
                ventasPorProducto[nombre] = (ventasPorProducto[nombre] ?: 0) + item.cantidad
            }
        }

        val masVendido = ventasPorProducto.maxByOrNull { it.value }

        if (masVendido != null) {
            println("\nProducto más vendido: ${masVendido.key} (${masVendido.value} unidades)")
        }
    }

    /**
     * Valor total del inventario (precio x stock de cada producto).
     */
    private fun mostrarValorInventario() {
        val productos = gestorProductos.obtenerProductos()

        val valorTotal = productos.sumOf { producto ->
            val stock = gestorInventario.consultarStock(producto.id)
            producto.precio * stock
        }

        println("\nValor total del inventario: $${"%.2f".format(valorTotal)}")
    }

    /**
     * Productos sin stock (agotados) o marcados manualmente como no disponibles.
     */
    private fun mostrarProductosAgotados() {
        val productos = gestorProductos.obtenerProductos()

        val agotados = productos.filter { producto ->
            !producto.disponible || gestorInventario.consultarStock(producto.id) == 0
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
     * Productos con pocas unidades disponibles (por debajo del umbral),
     * pero que todavía tienen stock.
     */
    private fun mostrarStockBajo(umbral: Int = 5) {
        val productos = gestorProductos.obtenerProductos()

        val stockBajo = productos.filter { producto ->
            val stock = gestorInventario.consultarStock(producto.id)
            stock in 1..umbral
        }

        if (stockBajo.isEmpty()) {
            println("\nProductos con stock bajo: ninguno.")
            return
        }

        println("\n⚠ Productos con stock bajo (${stockBajo.size}):")
        stockBajo.forEach { producto ->
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

        val porCategoria = productos.groupBy { it.categoria }

        println("\n===== RESUMEN POR CATEGORÍA =====")

        porCategoria.forEach { (categoria, listaProductos) ->
            val stockCategoria = listaProductos.sumOf { gestorInventario.consultarStock(it.id) }
            println("$categoria: ${listaProductos.size} producto(s), $stockCategoria unidad(es) en stock")
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

        val porEstado = pedidos.groupBy { it.estado }

        println("\n===== PEDIDOS POR ESTADO =====")

        porEstado.forEach { (estado, lista) ->
            println("$estado: ${lista.size} pedido(s)")
        }
    }
}
