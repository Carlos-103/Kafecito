// ====================================================================
// REPORTES.kt  -- Módulo de REPORTES
// Responsable:
// ====================================================================
// Requerimientos funcionales que cubre:
//   3. Visualización de resultados en consola
//   4. Generación de reporte o resumen del sistema
//
// Qué tienes que hacer:
//   1. Recibir referencias a los gestores de los otros módulos.
//   2. Calcular totales, producto más vendido, resumen general.

class GestorReportes(
    private val gestorProductos: GestorProductos,
    private val gestorPedidos: GestorPedidos,
    private val gestorInventario: GestorInventario
) {

    fun mostrarResumenGeneral() {
        try {
            println("\n===== RESUMEN DEL SISTEMA =====")

            val pedidos = gestorPedidos.obtenerPedidos()
            val totalVentas = pedidos.sumOf { it.calcularTotal() }
            println("Total de pedidos: ${pedidos.size}")
            println("Total vendido: $${"%.2f".format(totalVentas)}")

            println("\nProductos registrados: ${gestorProductos.obtenerProductos().size}")

            mostrarProductoMasVendido()

        } catch (e: Exception) {
            Logger.registrarError("Reportes", "Error al generar resumen: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    private fun mostrarProductoMasVendido() {
        val pedidos = gestorPedidos.obtenerPedidos()
        if (pedidos.isEmpty()) {
            println("Aún no hay pedidos para calcular el producto más vendido.")
            return
        }

        // Agrupa por producto y suma cantidades vendidas
        val ventasPorProducto = mutableMapOf<String, Int>()
        pedidos.forEach { pedido ->
            pedido.items.forEach { item ->
                val nombre = item.producto.nombre
                ventasPorProducto[nombre] = (ventasPorProducto[nombre] ?: 0) + item.cantidad
            }
        }

        val masVendido = ventasPorProducto.maxByOrNull { it.value }
        if (masVendido != null) {
            println("Producto más vendido: ${masVendido.key} (${masVendido.value} unidades)")
        }
    }
}
