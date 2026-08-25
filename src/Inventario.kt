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

class GestorInventario(private val stockMinimo: Int = 5) {

    // Manejo de colecciones: id del producto -> cantidad en stock
    private val stock = mutableMapOf<Int, Int>()

    fun registrarStockInicial(idProducto: Int, cantidad: Int) {
        try {
            require(cantidad >= 0) { "La cantidad no puede ser negativa" }
            stock[idProducto] = cantidad
            println("Stock inicial registrado: producto $idProducto -> $cantidad unidades")
        } catch (e: Exception) {
            Logger.registrarError("Inventario", "Error al registrar stock: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    fun descontarStock(idProducto: Int, cantidadVendida: Int) {
        try {
            val actual = stock[idProducto]
                ?: throw NoSuchElementException("El producto $idProducto no tiene stock registrado")
            require(cantidadVendida > 0) { "La cantidad vendida debe ser mayor a 0" }
            require(actual >= cantidadVendida) { "Stock insuficiente (disponible: $actual)" }

            stock[idProducto] = actual - cantidadVendida
            verificarStockBajo(idProducto)
        } catch (e: Exception) {
            Logger.registrarError("Inventario", "Error al descontar stock: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    private fun verificarStockBajo(idProducto: Int) {
        val cantidad = stock[idProducto] ?: return
        if (cantidad <= stockMinimo) {
            println("⚠ Alerta: el producto $idProducto tiene stock bajo ($cantidad unidades)")
        }
    }

    fun consultarStock(idProducto: Int): Int? = stock[idProducto]

    fun listarInventario() {
        if (stock.isEmpty()) {
            println("No hay inventario registrado.")
            return
        }
        stock.forEach { (id, cantidad) -> println("Producto $id -> $cantidad unidades") }
    }
}
